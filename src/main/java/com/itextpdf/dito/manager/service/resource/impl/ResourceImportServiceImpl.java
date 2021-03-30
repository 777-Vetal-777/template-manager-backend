package com.itextpdf.dito.manager.service.resource.impl;

import com.itextpdf.dito.editor.server.common.elements.urigenerator.ResourceUriGenerator;
import com.itextpdf.dito.manager.component.validator.resource.ContentTypeDetector;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.exception.resource.ResourceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.resource.ResourceNotFoundException;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;
import com.itextpdf.dito.manager.model.template.duplicates.DuplicatesList;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.service.resource.ResourceImportService;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.itextpdf.dito.manager.util.TemplateUtils.DITO_ASSET_TAG;
import static com.itextpdf.dito.manager.util.TemplateUtils.readStreamable;

@Service
public class ResourceImportServiceImpl implements ResourceImportService {

    private  static final String DITO_ASSET_UNKNOWN_RESOURCE = "dito-asset://unknown_resource";
    private final ResourceService resourceService;
    private final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper;
    private final ResourceRepository resourceRepository;
    private final ContentTypeDetector contentTypeDetector;

    public ResourceImportServiceImpl(final ResourceService resourceService,
                                     final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper,
                                     final ResourceRepository resourceRepository,
                                     final ContentTypeDetector contentTypeDetector) {
        this.resourceService = resourceService;
        this.resourceLeafDescriptorMapper = resourceLeafDescriptorMapper;
        this.resourceRepository = resourceRepository;
        this.contentTypeDetector = contentTypeDetector;
    }

    @Override
    public ResourceEntity importResource(final byte[] data, final ResourceTypeEnum type, final String name, final String additionalName,
                                         final String email, final TemplateImportNameModel nameModel) throws IOException {
        ResourceEntity entity;

        try {
            resourceService.get(name, type);

            if (nameModel == null) {
                throw new ResourceAlreadyExistsException(name);
            }

            if (Boolean.TRUE.equals(nameModel.getAllowedNewVersion())) {
                entity = resourceService.createNewVersion(name, type, data, name, email, "Import template");
            } else {
                entity = resourceService.create(additionalName, type, data, name, email);
            }

        } catch (ResourceNotFoundException e) {
            entity = resourceService.create(name, type, data, name, email);
        }

        return entity;
    }

    @Override
    public ResourceUriGenerator getResourceUriGenerator(final String fileName, final String email,
                                                        final Map<SettingType, Map<String, TemplateImportNameModel>> settings, final DuplicatesList duplicatesList) {
        final String namePattern = new StringBuilder(fileName).toString();
        final int initialValue = resourceRepository.findMaxIntegerByNamePattern(namePattern).orElse(0);
        final AtomicInteger resourceId = new AtomicInteger(initialValue);
        return resourceUriGeneratorContext -> {
            String resourceUri;
            ResourceTypeEnum type = null;
            try {
                final byte[] data = readStreamable(resourceUriGeneratorContext.getModifiedContent());
                type = getContentType(data);
                if (type == null) {
                    resourceUri = DITO_ASSET_UNKNOWN_RESOURCE;
                } else {
                    final String additionalName = getAdditionalName(namePattern, resourceId);
                    final String name = Optional.ofNullable(resourceUriGeneratorContext.getOriginalPath()).map(Path::getFileName).map(Path::toString).orElse(additionalName);
                    ResourceEntity resourceEntity = importResource(data, type, name, additionalName, email, settings.get(SettingType.valueOf(type.toString())).get(name));
                    resourceUri = DITO_ASSET_TAG.concat(resourceLeafDescriptorMapper.encodeId(resourceEntity.getName(), resourceEntity.getType(), null));
                }
            } catch (ResourceAlreadyExistsException e) {
                if (type != null) {
                    duplicatesList.putToDuplicates(SettingType.valueOf(type.toString()), Optional.ofNullable(resourceUriGeneratorContext.getOriginalPath()).map(Path::getFileName).map(Path::toString).orElseGet(() -> getAdditionalName(namePattern, resourceId)));
                }
                resourceUri = DITO_ASSET_UNKNOWN_RESOURCE;
            } catch (IOException e) {
                resourceUri = null;
            }

            return resourceUri;
        };
    }

    private String getAdditionalName(final String namePattern, final AtomicInteger resourceId) {
        return new StringBuilder(namePattern).append("(").append(resourceId.incrementAndGet()).append(")").toString();
    }

    private ResourceTypeEnum getContentType(final byte[] data) {
        return contentTypeDetector.detectType(data);
    }

}
