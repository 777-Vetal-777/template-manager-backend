package com.itextpdf.dito.manager.service.resource.impl;

import com.itextpdf.dito.editor.server.common.core.stream.Streamable;
import com.itextpdf.dito.editor.server.common.elements.urigenerator.ResourceUriGenerator;
import com.itextpdf.dito.manager.component.validator.resource.ContentTypeDetector;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.exception.resource.ResourceNotFoundException;
import com.itextpdf.dito.manager.integration.editor.dto.ResourceIdDTO;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.service.resource.ResourceImportService;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.itextpdf.dito.manager.util.TemplateImportUtils.readStreamable;

@Service
public class ResourceImportServiceImpl implements ResourceImportService {

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
    public ResourceEntity importResource(final Streamable stream, final String name, final String uri, final String email) throws IOException {
        ResourceIdDTO resourceId = resourceLeafDescriptorMapper.map(uri.replaceFirst("dito-asset://", ""));
        final byte[] data = readStreamable(stream);

        ResourceEntity entity;
        if (resourceId == null) {
            entity = null;
        } else {
            try {
                entity = resourceService.get(resourceId.getName(), resourceId.getType());
            } catch (ResourceNotFoundException e) {
                entity = resourceService.create(resourceId.getName(), resourceId.getType(), data, name, email);
            }
        }
        return entity;
    }

    @Override
    public ResourceUriGenerator getResourceUriGenerator(final String fileName) {
        final String namePattern = new StringBuilder("import-").append(fileName).toString();
        final int initialValue = resourceRepository.findMaxIntegerByNamePattern(namePattern).orElse(0);
        final AtomicInteger resourceId = new AtomicInteger(initialValue);
        return resourceUriGeneratorContext -> {
            final ResourceTypeEnum type = getContentType(resourceUriGeneratorContext.getModifiedContent());
            return type == null ? "dito-asset://unknown_resource" : "dito-asset://".concat(resourceLeafDescriptorMapper.encodeId(new StringBuilder(namePattern).append("(").append(resourceId.incrementAndGet()).append(")").toString(), type, null));
        };
    }

    private ResourceTypeEnum getContentType(final Streamable stream) {
        ResourceTypeEnum detectedType;

        try {
            final byte[] data = readStreamable(stream);
            detectedType = contentTypeDetector.detectType(data);
        } catch (IOException e) {
            detectedType = null;
        }

        return detectedType;

    }

}
