package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.editor.server.common.elements.converter.DefaultFromLegacyProjectElementsConverter;
import com.itextpdf.dito.editor.server.common.elements.converter.ProjectElementsConverterProperties;
import com.itextpdf.dito.editor.server.common.elements.entity.ProjectElements;
import com.itextpdf.dito.editor.server.common.elements.entity.TemplateElement;
import com.itextpdf.dito.editor.server.common.elements.urigenerator.ResourceUriGenerator;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.resource.ResourceNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplateAlreadyExistsException;
import com.itextpdf.dito.manager.exception.template.TemplateImportHasDuplicateNamesException;
import com.itextpdf.dito.manager.exception.template.TemplateImportProjectException;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.integration.editor.service.template.TemplateManagementService;
import com.itextpdf.dito.manager.model.template.duplicates.DuplicatesList;
import com.itextpdf.dito.manager.model.template.duplicates.impl.DuplicatesListImpl;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionImportService;
import com.itextpdf.dito.manager.service.resource.EmbeddedResourceImportService;
import com.itextpdf.dito.manager.service.resource.ResourceImportService;
import com.itextpdf.dito.manager.service.template.TemplateImportService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.util.TemplateUtils.readStreamable;

@Service
public class TemplateImportServiceImpl implements TemplateImportService {
    private static final Logger log = LogManager.getLogger(TemplateImportServiceImpl.class);

    private final TemplateRepository templateRepository;
    private final TemplateManagementService templateManagementService;
    private final DataCollectionImportService dataCollectionImportService;
    private final ResourceImportService resourceImportService;
    private final List<EmbeddedResourceImportService> embeddedResourceImportServices;

    public TemplateImportServiceImpl(final TemplateManagementService templateManagementService,
                                     final DataCollectionImportService dataCollectionImportService,
                                     final ResourceImportService resourceImportService,
                                     final TemplateRepository templateRepository,
                                     final List<EmbeddedResourceImportService> embeddedResourceImportServices) {
        this.templateManagementService = templateManagementService;
        this.dataCollectionImportService = dataCollectionImportService;
        this.resourceImportService = resourceImportService;
        this.templateRepository = templateRepository;
        this.embeddedResourceImportServices = embeddedResourceImportServices;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TemplateEntity importTemplate(final String fileName,
                                         final byte[] ditoData,
                                         final String email,
                                         final Map<SettingType, Map<String, TemplateImportNameModel>> settings) {
        try {
            final DuplicatesList duplicatesList = new DuplicatesListImpl();
            final ResourceUriGenerator resourcesUriGenerator = resourceImportService.getResourceUriGenerator(fileName, email, settings, duplicatesList);

            final ProjectElementsConverterProperties projectElementsConverterProperties = new ProjectElementsConverterProperties();
            projectElementsConverterProperties.setEnableEmbeddedStylesConverting(true);
            projectElementsConverterProperties.setResourceUriGenerator(resourcesUriGenerator);

            final ProjectElements projectElements = new DefaultFromLegacyProjectElementsConverter(projectElementsConverterProperties).convert(new ByteArrayInputStream(ditoData));

            final DataCollectionEntity dataCollectionEntity = dataCollectionImportService.importDataCollectionAndSamples(fileName, projectElements.getDataSamples(), settings.get(SettingType.DATA_COLLECTION), duplicatesList, email);

            final List<TemplateEntity> templateEntityList = importTemplates(fileName, projectElements.getTemplates(), settings.get(SettingType.TEMPLATE), settings.get(SettingType.STYLESHEET), dataCollectionEntity, duplicatesList, email);

            if (!duplicatesList.isEmpty()) {
                throw new TemplateImportHasDuplicateNamesException("Template file got duplicates", duplicatesList);
            }

            return templateEntityList.get(0);

        } catch (IOException e) {
            throw new TemplateImportProjectException(e);
        }
    }

    private List<TemplateEntity> importTemplates(final String fileName,
                                                 final List<TemplateElement> templateElements,
                                                 final Map<String, TemplateImportNameModel> templateSettings,
                                                 final Map<String, TemplateImportNameModel> stylesheetsSettings,
                                                 final DataCollectionEntity dataCollection,
                                                 final DuplicatesList duplicatesList, final String email) {
        return templateElements.stream().map(templateElement -> {
                    TemplateEntity entity;
                    try {
                        entity = importTemplateEntity(fileName, templateElement, dataCollection, templateSettings.get(templateElement.getTemplateName()), stylesheetsSettings, duplicatesList, email);
                    } catch (ResourceNotFoundException e) {
                        log.warn("Could not provide consistency for imported template: {}", e.getMessage());
                        entity = null;
                    } catch (TemplateAlreadyExistsException e) {
                        duplicatesList.putToDuplicates(SettingType.TEMPLATE, templateElement.getTemplateName());
                        entity = null;
                    } catch (IOException e) {
                        throw new TemplateImportProjectException(e);
                    }
                    return entity;
                }
        ).collect(Collectors.toList());
    }

    private TemplateEntity importTemplateEntity(final String fileName,
                                                final TemplateElement templateElement,
                                                final DataCollectionEntity dataCollectionEntity,
                                                final TemplateImportNameModel templateName,
                                                final Map<String, TemplateImportNameModel> stylesheetsSettings,
                                                final DuplicatesList duplicatesList,
                                                final String email) throws IOException {
        final byte[] originalData = readStreamable(templateElement.getTemplateStream());
        final String dataCollection = Optional.ofNullable(dataCollectionEntity).map(DataCollectionEntity::getName).orElse(null);

        byte[] data = originalData;

        for (EmbeddedResourceImportService embeddedResourceImportService: embeddedResourceImportServices) {
            data = embeddedResourceImportService.importEmbedded(data, fileName, stylesheetsSettings, duplicatesList, email);
        }

        TemplateEntity templateEntity;
        try {
            templateManagementService.get(templateElement.getTemplateName());

            if (templateName == null) {
                throw new TemplateAlreadyExistsException(templateElement.getTemplateName());
            }

            if (Boolean.TRUE.equals(templateName.getAllowedNewVersion())) {
                templateEntity = templateManagementService.createNewVersion(templateElement.getTemplateName(), data, email, null, "Import template");
            } else {
                int currentNumber = templateRepository.findMaxIntegerByNamePattern(fileName).orElse(0) + 1;
                templateEntity = templateManagementService.create(new StringBuilder(fileName).append("(").append(currentNumber).append(")").toString(),
                        data, dataCollection, email);
            }
        } catch (TemplateNotFoundException e) {
            templateEntity = templateManagementService.create(templateElement.getTemplateName(), data, dataCollection, email);
        }

        return templateEntity;
    }

}
