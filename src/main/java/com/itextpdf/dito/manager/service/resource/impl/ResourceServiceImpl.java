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
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class ResourceServiceImpl extends AbstractService implements ResourceService {
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

        final UserEntity userEntity = userService.findByEmail(email);

        final ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setName(name);
        resourceEntity.setType(type);
        resourceEntity.setCreatedOn(new Date());
        resourceEntity.setCreatedBy(userEntity);

        final ResourceLogEntity logEntity = new ResourceLogEntity();
        logEntity.setAuthor(userEntity);
        logEntity.setDate(new Date());
        logEntity.setResource(resourceEntity);

        final ResourceFileEntity fileEntity = new ResourceFileEntity();
        fileEntity.setResource(resourceEntity);
        fileEntity.setVersion(1L);
        fileEntity.setFile(data);
        fileEntity.setFileName(fileName);
        fileEntity.setDeployed(false);
        resourceEntity.setResourceFiles(Collections.singletonList(fileEntity));
        resourceEntity.setResourceLogs(Collections.singletonList(logEntity));
        return resourceRepository.save(resourceEntity);
    }

    @Override
    public ResourceEntity createNewVersion(final String name, final ResourceTypeEnum type, final byte[] data, final String fileName, final String email, final String comment) {
        final ResourceEntity existingResourceEntity = getResource(name, type);
        final UserEntity userEntity = userService.findByEmail(email);
        final Long oldVersion = resourceFileRepository.findFirstByResource_IdOrderByVersionDesc(existingResourceEntity.getId()).getVersion();
        //TODO CHECK IF USER HAVE PERMISSION TO UPDATE TEMPLATE
        final ResourceLogEntity logEntity = new ResourceLogEntity();
        logEntity.setAuthor(userEntity);
        logEntity.setDate(new Date());
        logEntity.setResource(existingResourceEntity);

        final ResourceFileEntity fileEntity = new ResourceFileEntity();
        fileEntity.setResource(existingResourceEntity);
        fileEntity.setVersion(oldVersion+1);
        fileEntity.setFile(data);
        fileEntity.setFileName(fileName);
        fileEntity.setDeployed(false);
        fileEntity.setComment(comment);

        existingResourceEntity.getResourceFiles().add(fileEntity);
        existingResourceEntity.getResourceLogs().add(logEntity);
        return resourceRepository.save(existingResourceEntity);
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
        if(!existingResource.getName().equals(entity.getName())){
            throwExceptionIfResourceExist(entity);
        }
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
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final Pageable pageWithSort = updateSort(pageable);
        final String name = getStringFromFilter(filter.getName());
        final List<ResourceTypeEnum> resourceTypes = filter.getType();
        final String modifiedBy = getStringFromFilter(filter.getModifiedBy());
        final String comment = getStringFromFilter(filter.getComment());

        Date modifiedOnStartDate = null;
        Date modifiedOnEndDate = null;
        final List<String> modifiedOnDateRange = filter.getModifiedOn();
        if (modifiedOnDateRange != null) {
            if (modifiedOnDateRange.size() != 2) {
                throw new IllegalArgumentException("Date range should contain two elements: start date and end date");
            }
            modifiedOnStartDate = getStartDateFromRange(modifiedOnDateRange);
            modifiedOnEndDate = getEndDateFromRange(modifiedOnDateRange);
        }
        return StringUtils.isEmpty(searchParam)
                ? resourceRepository.filter(pageWithSort, name, resourceTypes, comment, modifiedBy, modifiedOnStartDate, modifiedOnEndDate)
                : resourceRepository.search(pageWithSort, name, resourceTypes, comment, modifiedBy, modifiedOnStartDate, modifiedOnEndDate, searchParam.toLowerCase());
    }

    private Pageable updateSort(Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("type")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "type");
                    }
                    if (sortParam.getProperty().equals("modifiedBy")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "latestLogRecord.author.firstName");
                    }
                    if (sortParam.getProperty().equals("modifiedOn")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "latestLogRecord.date");
                    }
                    if (sortParam.getProperty().equals("comment")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "latestFile.comment");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    private void throwExceptionIfResourceExist(final ResourceEntity entity) {
        if (resourceRepository.existsByNameEqualsAndTypeEquals(entity.getName(), entity.getType())) {
            throw new ResourceAlreadyExistsException(entity.getName());
        }
    }

    private ResourceEntity getResource(final String name, final ResourceTypeEnum type) {
        return resourceRepository.findByNameAndType(name, type).orElseThrow(() -> new ResourceNotFoundException(name));
    }

    @Override
    public List<String> getSupportedSortFields() {
        return ResourceRepository.SUPPORTED_SORT_FIELDS;
    }
}
