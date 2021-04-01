package com.itextpdf.dito.manager.service.template.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.component.mapper.template.TemplateMapper;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;
import com.itextpdf.dito.manager.exception.template.TemplateHasWrongStructureException;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.model.template.part.PartSettings;
import com.itextpdf.dito.manager.model.template.part.TemplatePartModel;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.TemplateFilePartService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TemplateFilePartServiceImpl implements TemplateFilePartService {

    private static final Logger LOG = LogManager.getLogger(TemplateFilePartServiceImpl.class);

    private final ObjectMapper objectMapper;

    private final TemplateRepository templateRepository;

    private final TemplateMapper templateMapper;

    public TemplateFilePartServiceImpl(final TemplateRepository templateRepository,
                                       final ObjectMapper objectMapper,
                                       final TemplateMapper templateMapper) {
        this.templateRepository = templateRepository;
        this.objectMapper = objectMapper;
        this.templateMapper = templateMapper;
    }

    @Override
    public List<TemplateFilePartEntity> createTemplatePartEntities(final String dataCollectionName,
                                                                   final List<TemplatePartModel> templateParts) {
        LOG.info("Create template parts by dataCollectionName: {} and templateParts: {} was started", dataCollectionName, templateParts);
        final List<TemplateEntity> templatePartList = templateRepository.getTemplatesWithLatestFileByName(templateParts.stream().map(TemplatePartModel::getTemplateName).collect(Collectors.toList()));
        final Map<String, TemplateFileEntity> templateFilePartMap = templatePartList.stream().collect(Collectors.toMap(TemplateEntity::getName, TemplateEntity::getLatestFile, (templateFileEntity1, templateFileEntity2) -> templateFileEntity1));

        //checks that all templates exists
        throwExceptionIfSomeTemplatesAreNotFound(templateParts, templateFilePartMap);

        //checks that all templates has the same (or absent dataCollection)
        throwExceptionIfTemplatesHaveAnotherDataCollections(templatePartList, dataCollectionName);

        //checks that exists at most one HEADER and FOOTER
        throwExceptionIfPartsSizeAreIncorrect(templateParts, templateFilePartMap);

        List<TemplateFilePartEntity> templateFilePartEntities = templateParts.stream().map(templatePart -> {
            final TemplateFileEntity partTemplateFileEntity = templateFilePartMap.get(templatePart.getTemplateName());
            return createTemplateFilePartEntity(partTemplateFileEntity, templatePart);
        }).collect(Collectors.toList());
        LOG.info("Create template parts by dataCollectionName: {} and templateParts: {} was finished successfully", dataCollectionName, templateParts);
        return templateFilePartEntities;
    }

    private TemplateFilePartEntity createTemplateFilePartEntity(final TemplateFileEntity partTemplateFileEntity,
                                                                final TemplatePartModel templatePart) {
        LOG.info("Create template part by templateFileEntity: {} and templatePartModel: {} was started", partTemplateFileEntity, templatePart);
        final TemplateFilePartEntity filePartEntity = createTemplateFilePartEntity(null, partTemplateFileEntity, templatePart.getCondition(), getSettingsString(templatePart));
        LOG.info("Create template part by templateFileEntity: {} and templatePartModel: {} was finished successfully", partTemplateFileEntity, templatePart);
        return filePartEntity;
    }

    private String getSettingsString(final TemplatePartModel templatePart) {
        final PartSettings partSettings = templatePart.getPartSettings();
        try {
            return objectMapper.writeValueAsString(partSettings);
        } catch (JsonProcessingException e) {
            LOG.error(e);
        }
        return null;
    }

    @Override
    public TemplateFilePartEntity updateComposition(final TemplateFilePartEntity example,
                                                    final TemplateFileEntity composition) {
        LOG.info("Update composition with templateFilePartEntity: {} and composition: {} was started", example, composition);
        final TemplateFilePartEntity templateFilePartEntity = createTemplateFilePartEntity(composition, example.getPart(), example.getCondition(), example.getSettings());
        LOG.info("Update composition with templateFilePartEntity: {} and composition: {} was finished successfully", example, composition);
        return templateFilePartEntity;
    }

    private TemplateFilePartEntity createTemplateFilePartEntity(final TemplateFileEntity compositionTemplateFileEntity,
                                                                final TemplateFileEntity partTemplateFileEntity,
                                                                final String condition,
                                                                final String settings) {
        final TemplateFilePartEntity templateFilePartEntity = new TemplateFilePartEntity();
        templateFilePartEntity.setPart(partTemplateFileEntity);
        templateFilePartEntity.setComposition(compositionTemplateFileEntity);
        templateFilePartEntity.setCondition(condition);
        templateFilePartEntity.setSettings(settings);
        return templateFilePartEntity;
    }

    private void throwExceptionIfPartsSizeAreIncorrect(final List<TemplatePartModel> templateParts, final Map<String, TemplateFileEntity> templatePartMap) {
        final Map<TemplateTypeEnum, Integer> mapOfPartsCount = templateParts.stream().flatMap(part -> Stream.of(templatePartMap.get(part.getTemplateName()).getTemplate())).collect(
                Collectors.groupingBy(TemplateEntity::getType,
                        Collectors.collectingAndThen(Collectors.toList(), List::size)));
        throwExceptionIfHaveCompositeParts(mapOfPartsCount);
        throwExceptionIfTooManyForType(mapOfPartsCount, TemplateTypeEnum.HEADER);
        throwExceptionIfTooManyForType(mapOfPartsCount, TemplateTypeEnum.FOOTER);
        throwExceptionIfTooFewForType(mapOfPartsCount, TemplateTypeEnum.STANDARD);
    }

    private void throwExceptionIfTooManyForType(final Map<TemplateTypeEnum, Integer> mapOfPartsCount, final TemplateTypeEnum checkedType) {
        if (mapOfPartsCount.getOrDefault(checkedType, 0) > 1) {
            throw new TemplateHasWrongStructureException(new StringBuilder("Template parts have more than one ").append(checkedType).toString());
        }
    }

    private void throwExceptionIfHaveCompositeParts(final Map<TemplateTypeEnum, Integer> mapOfPartsCount) {
        if (mapOfPartsCount.containsKey(TemplateTypeEnum.COMPOSITION)) {
            throw new TemplateHasWrongStructureException("Can not include composition template to another one");
        }
    }

    private void throwExceptionIfTooFewForType(final Map<TemplateTypeEnum, Integer> mapOfPartsCount, final TemplateTypeEnum checkedType) {
        if (mapOfPartsCount.getOrDefault(checkedType, 0) < 1) {
            throw new TemplateHasWrongStructureException(new StringBuilder("Template parts have too few of ").append(checkedType).append(" templates").toString());
        }
    }

    private void throwExceptionIfTemplatesHaveAnotherDataCollections(final List<TemplateEntity> templatePartList, final String dataCollectionName) {
        if (!templatePartList.stream().allMatch(entity -> {
            final String dataCollection = Optional.ofNullable(entity.getLatestFile().getDataCollectionFile()).map(DataCollectionFileEntity::getDataCollection).map(DataCollectionEntity::getName).orElse(null);
            return Objects.equals(dataCollectionName, dataCollection) || Objects.equals(null, dataCollection);
        })) {
            throw new TemplateHasWrongStructureException("Template parts belong to different Data Collections");
        }
    }

    private void throwExceptionIfSomeTemplatesAreNotFound(final List<TemplatePartModel> templateParts, final Map<String, TemplateFileEntity> templateFilePartMap) {
        final List<String> missedTemplateNames = templateParts.stream().map(TemplatePartModel::getTemplateName).filter(partName -> !templateFilePartMap.containsKey(partName)).collect(Collectors.toList());
        if (!missedTemplateNames.isEmpty()) {
            throw new TemplateNotFoundException(missedTemplateNames.get(0));
        }
    }

    @Override
    public TemplatePartModel mapFromEntity(final TemplateFilePartEntity partEntity) {
        final TemplatePartModel model = new TemplatePartModel();
        model.setCondition(partEntity.getCondition());
        model.setTemplateName(partEntity.getPart().getTemplate().getName());
        model.setPartSettings(templateMapper.mapPartSettings(partEntity));
        return model;
    }
}
