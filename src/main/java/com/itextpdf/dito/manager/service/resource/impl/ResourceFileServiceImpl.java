package com.itextpdf.dito.manager.service.resource.impl;

import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.exception.resource.ResourceUuidNotFoundException;
import com.itextpdf.dito.manager.repository.resource.ResourceFileRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.service.resource.ResourceFileService;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ResourceFileServiceImpl implements ResourceFileService {

    private final ResourceRepository resourceRepository;
    private final ResourceFileRepository resourceFileRepository;

    public ResourceFileServiceImpl(final ResourceRepository resourceRepository,
                                   final ResourceFileRepository resourceFileRepository) {
        this.resourceRepository = resourceRepository;
        this.resourceFileRepository = resourceFileRepository;
    }

    @Override
    public ResourceFileEntity getByUuid(final String uuid) {
        return getDefaultResourceFileEntity(uuid);
    }

    private ResourceFileEntity getDefaultResourceFileEntity(final String uuid) {
        return resourceRepository.findByUuid(uuid).map(ResourceEntity::getLatestFile)
                .stream().flatMap(Collection::stream)
                .findFirst().orElseGet( () ->
                        resourceFileRepository.findFirstByUuid(uuid).orElseThrow(() -> new ResourceUuidNotFoundException(uuid))
                );
    }

}
