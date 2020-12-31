package com.itextpdf.dito.manager.service.instance.impl;

import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.date.InvalidDateException;
import com.itextpdf.dito.manager.exception.instance.InstanceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.instance.InstanceHasAttachedTemplateException;
import com.itextpdf.dito.manager.exception.instance.InstanceNotFoundException;
import com.itextpdf.dito.manager.exception.instance.InstanceUsedInPromotionPathException;
import com.itextpdf.dito.manager.filter.instance.InstanceFilter;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.instance.InstanceService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class InstanceServiceImpl extends AbstractService implements InstanceService {
    private final UserService userService;
    private final InstanceRepository instanceRepository;

    public InstanceServiceImpl(final UserService userService, final InstanceRepository instanceRepository) {
        this.userService = userService;
        this.instanceRepository = instanceRepository;
    }


    @Override
    public List<InstanceEntity> save(final List<InstanceEntity> instances, final String creatorEmail) {
        final UserEntity userEntity = userService.findByEmail(creatorEmail);

        instances.forEach(instanceEntity ->
        {
            if (instanceRepository.findByName(instanceEntity.getName()).isPresent()) {
                throw new InstanceAlreadyExistsException(instanceEntity.getName());
            }
            instanceEntity.setCreatedBy(userEntity);
        });

        return instanceRepository.saveAll(instances);
    }

    @Override
    public InstanceEntity get(final String name) {
        return instanceRepository.findByName(name).orElseThrow(() -> new InstanceNotFoundException(name));
    }

    @Override
    public List<InstanceEntity> getAll() {
        return instanceRepository.findAll();
    }

    @Override
    public Page<InstanceEntity> getAll(final InstanceFilter instanceFilter, final Pageable pageable,
                                       final String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final Pageable pageWithSort = updateSort(pageable);
        final String name = getStringFromFilter(instanceFilter.getName());
        final String socket = getStringFromFilter(instanceFilter.getSocket());
        final String createdBy = getStringFromFilter(instanceFilter.getCreatedBy());
        final List<String> stagesFromFilter = instanceFilter.getStage();
        final List<String> stages = !CollectionUtils.isEmpty(stagesFromFilter)
                ? stagesFromFilter.stream().map(String::toLowerCase).collect(Collectors.toList())
                : null;
        Date createdOnStartDate = null;
        Date createdOnEndDate = null;
        final List<String> createdOnDateRange = instanceFilter.getCreatedOn();
        if (createdOnDateRange != null) {
            if (createdOnDateRange.size() != 2) {
                throw new InvalidDateException("Date range should contain two elements: start date and end date");
            }
            createdOnStartDate = getStartDateFromRange(createdOnDateRange);
            createdOnEndDate = getEndDateFromRange(createdOnDateRange);
        }

        return StringUtils.isEmpty(searchParam)
                ? instanceRepository.filter(pageWithSort, name, socket, createdBy, createdOnStartDate, createdOnEndDate, stages)
                : instanceRepository.search(pageWithSort, name, socket, createdBy, createdOnStartDate, createdOnEndDate, stages, searchParam.toLowerCase());
    }

    @Override
    @Transactional
    public void forget(final String name) {
        final InstanceEntity instanceEntity = get(name);

        final StageEntity stageEntity = instanceEntity.getStage();
        if (stageEntity != null) {
            throw new InstanceUsedInPromotionPathException();
        }

        final List<TemplateEntity> templateEntities = instanceEntity.getTemplates();
        if (templateEntities != null && !templateEntities.isEmpty()) {
            throw new InstanceHasAttachedTemplateException();
        }

        instanceRepository.deleteByName(name);
    }

    @Override
    public InstanceEntity update(final String name, final InstanceEntity instanceEntity) {
        final InstanceEntity oldInstanceEntity = get(name);

        if (instanceRepository.findByName(instanceEntity.getName()).isPresent()) {
            throw new InstanceAlreadyExistsException(instanceEntity.getName());
        }

        oldInstanceEntity.setName(instanceEntity.getName());
        oldInstanceEntity.setSocket(instanceEntity.getSocket());

        return instanceRepository.save(oldInstanceEntity);
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return InstanceRepository.SUPPORTED_SORT_FIELDS;
    }

    private Pageable updateSort(final Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("stage")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "stage.name");
                    }
                    if (sortParam.getProperty().equals("createdBy")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "createdBy.firstName");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }
}
