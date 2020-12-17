package com.itextpdf.dito.manager.service.resource.impl;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceLogEntity;
import com.itextpdf.dito.manager.exception.resource.ResourceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.resource.ResourceNotFoundException;
import com.itextpdf.dito.manager.filter.resource.ResourceFilter;
import com.itextpdf.dito.manager.repository.resource.ResourceFileRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceLogRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Service
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceLogRepository resourceLogRepository;
    private final ResourceFileRepository resourceFileRepository;
    private final UserService userService;

    public ResourceServiceImpl(
            final ResourceRepository resourceRepository,
            final ResourceLogRepository resourceLogRepository,
            final ResourceFileRepository resourceFileRepository,
            final UserService userService) {
        this.resourceRepository = resourceRepository;
        this.resourceLogRepository = resourceLogRepository;
        this.resourceFileRepository = resourceFileRepository;
        this.userService = userService;
    }

    @Override
    public ResourceEntity create(final String name, final ResourceTypeEnum type, final byte[] data, final String fileName, final String email) {
        if (resourceRepository.existsByNameEqualsAndTypeEquals(name, type)) {
            throw new ResourceAlreadyExistsException(name);
        }

        final ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setName(name);
        resourceEntity.setType(type);
        resourceEntity.setCreatedOn(new Date());
        final UserEntity userEntity = userService.findByEmail(email);
        resourceEntity.setCreatedBy(userEntity);

        final ResourceFileEntity fileEntity = new ResourceFileEntity();
        fileEntity.setResource(resourceEntity);
        fileEntity.setVersion(1L);
        fileEntity.setFile(data);
        fileEntity.setFileName(fileName);
        resourceEntity.setResourceFiles(Collections.singletonList(fileEntity));
        return resourceRepository.save(resourceEntity);
    }

    @Override
    public ResourceEntity get(final String name, final ResourceTypeEnum type) {
        final ResourceEntity resourceEntity = getResource(name, type);
        //specially made to reduce the load of files
        ResourceFileEntity file = resourceFileRepository.findFirstByResource_IdOrderByVersionDesc(resourceEntity.getId());
        ResourceLogEntity log = resourceLogRepository.findFirstByResource_IdOrderByDateDesc(resourceEntity.getId());
        resourceEntity.setResourceFiles(Collections.singletonList(file));
        resourceEntity.setResourceLogs(log != null ? Collections.singletonList(log) : null);
        return resourceEntity;
    }

    @Override
    public ResourceEntity update(final String name, final ResourceEntity entity, final String mail) {
        final ResourceEntity existingResource = getResource(name, entity.getType());
        throwExceptiontIfResourceExist(entity);
        existingResource.setName(entity.getName());
        existingResource.setDescription(entity.getDescription());

        final ResourceLogEntity log = new ResourceLogEntity();
        log.setResource(existingResource);
        log.setDate(new Date());
        log.setAuthor(userService.findByEmail(mail));
        existingResource.getResourceLogs().add(log);

        return resourceRepository.save(existingResource);
    }

    @Override
    public Page<ResourceEntity> list(final Pageable pageable, final ResourceFilter filter, final String searchParam) {
        return resourceRepository.findAll(pageable);
    }

    private void throwExceptiontIfResourceExist(final ResourceEntity entity) {
        if (resourceRepository.existsByNameEqualsAndTypeEquals(entity.getName(), entity.getType())) {
            throw new ResourceAlreadyExistsException(entity.getName());
        }
    }

    private ResourceEntity getResource(final String name,final ResourceTypeEnum type) {
        return resourceRepository.findByNameAndType(name, type).orElseThrow(() -> new ResourceNotFoundException(name));
    }
}
