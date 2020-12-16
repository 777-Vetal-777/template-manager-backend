package com.itextpdf.dito.manager.service.instance.impl;

import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.TemplateEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.instance.InstanceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.instance.InstanceHasAttachedTemplateException;
import com.itextpdf.dito.manager.exception.instance.InstanceNotFoundException;
import com.itextpdf.dito.manager.exception.instance.InstanceUsedInPromotionPathException;
import com.itextpdf.dito.manager.filter.instance.InstanceFilter;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.instance.InstanceService;
import com.itextpdf.dito.manager.service.user.UserService;

import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.itextpdf.dito.manager.filter.FilterUtils.getDateRangeFromFilter;
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

        final String name = getStringFromFilter(instanceFilter.getName());
        final String socket = getStringFromFilter(instanceFilter.getSocket());
        final String createdBy = getStringFromFilter(instanceFilter.getCreatedBy());
        final List<Date> dateRange = getDateRangeFromFilter(instanceFilter.getCreatedOn());

        return StringUtils.isEmpty(searchParam)
                ? instanceRepository.filter(pageable, name, socket, createdBy, dateRange)
                : instanceRepository.search(pageable, name, socket, createdBy, dateRange, searchParam.toLowerCase());
    }

    @Override
    @Transactional
    public void forget(final String name) {
        final InstanceEntity instanceEntity = get(name);

        final StageEntity stageEntity = instanceEntity.getStage();
        if (stageEntity != null) {
            throw new InstanceUsedInPromotionPathException();
        }

        final TemplateEntity templateEntity = instanceEntity.getTemplate();
        if (templateEntity != null) {
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
}
