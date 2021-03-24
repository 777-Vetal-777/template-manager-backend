package com.itextpdf.dito.manager.service.instance.impl;

import com.itextpdf.dito.manager.component.client.instance.InstanceClient;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.date.InvalidDateRangeException;
import com.itextpdf.dito.manager.exception.instance.InstanceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.instance.InstanceCustomHeaderValidationException;
import com.itextpdf.dito.manager.exception.instance.InstanceHasAttachedTemplateException;
import com.itextpdf.dito.manager.exception.instance.InstanceNotFoundException;
import com.itextpdf.dito.manager.exception.instance.InstanceUsedInPromotionPathException;
import com.itextpdf.dito.manager.exception.instance.deployment.SdkInstanceException;
import com.itextpdf.dito.manager.filter.instance.InstanceFilter;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.instance.InstanceService;
import com.itextpdf.dito.manager.service.template.TemplateDeploymentService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.Objects;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getListLowerStringsFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class InstanceServiceImpl extends AbstractService implements InstanceService {
    private static final Logger log = LogManager.getLogger(InstanceServiceImpl.class);
    private final UserService userService;
    private final InstanceRepository instanceRepository;
    private final TemplateDeploymentService templateDeploymentService;
    private final InstanceClient instanceClient;

    public InstanceServiceImpl(final UserService userService,
                               final InstanceRepository instanceRepository,
                               final InstanceClient instanceClient,
                               final TemplateDeploymentService templateDeploymentService) {
        this.userService = userService;
        this.instanceRepository = instanceRepository;
        this.instanceClient = instanceClient;
        this.templateDeploymentService = templateDeploymentService;
    }


    @Override
    public InstanceEntity save(final InstanceEntity instance, final String creatorEmail) {
        log.info("Save instance: {} by email: {} was started", instance, creatorEmail);
        final UserEntity userEntity = userService.findActiveUserByEmail(creatorEmail);
        if (instanceRepository.findByName(instance.getName()).isPresent()) {
            throw new InstanceAlreadyExistsException(instance.getName());
        }
        if (instanceRepository.findBySocket(instance.getSocket()).isPresent()) {
            throw new InstanceAlreadyExistsException(instance.getSocket());
        }
        if (StringUtils.isEmpty(instance.getHeaderName()) ^ StringUtils.isEmpty(instance.getHeaderValue())) {
            throw new InstanceCustomHeaderValidationException(instance.getName());
        }
        final String instanceToken = instanceClient.register(instance.getSocket(), instance.getHeaderName(), instance.getHeaderValue()).getToken();
        instance.setCreatedBy(userEntity);
        instance.setRegisterToken(instanceToken);
        final InstanceEntity instanceEntity = instanceRepository.save(instance);
        log.info("Save instance: {} by email: {} was finished successfully", instance, creatorEmail);
        return instanceEntity;
    }

    @Override
    public List<InstanceEntity> save(final List<InstanceEntity> instances, final String creatorEmail) {
        log.info("Save list instances: {} was started", instances);
        final List<InstanceEntity> instanceEntityList = instances.stream()
                .map(instance -> save(instance, creatorEmail))
                .collect(Collectors.toList());
        log.info("Save list instances: {} was finished successfully", instances);
        return instanceEntityList;
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
        log.info("Get all instanceEntities by filter: {} and searchParam: {} was started", instanceFilter, searchParam);
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final Pageable pageWithSort = updateSort(pageable);
        final String name = getStringFromFilter(instanceFilter.getName());
        final String socket = getStringFromFilter(instanceFilter.getSocket());
        final String createdBy = getStringFromFilter(instanceFilter.getCreatedBy());
        final List<String> stages = getListLowerStringsFromFilter(instanceFilter.getStage());
        Date createdOnStartDate = null;
        Date createdOnEndDate = null;
        final List<String> createdOnDateRange = instanceFilter.getCreatedOn();
        if (createdOnDateRange != null) {
            if (createdOnDateRange.size() != 2) {
                throw new InvalidDateRangeException();
            }
            createdOnStartDate = getStartDateFromRange(createdOnDateRange);
            createdOnEndDate = getEndDateFromRange(createdOnDateRange);
        }
        final Page<InstanceEntity> instanceEntities = StringUtils.isEmpty(searchParam)
                ? instanceRepository.filter(pageWithSort, name, socket, createdBy, createdOnStartDate, createdOnEndDate, stages)
                : instanceRepository.search(pageWithSort, name, socket, createdBy, createdOnStartDate, createdOnEndDate, stages, searchParam.toLowerCase());
        log.info("Get all instanceEntities by filter: {} and searchParam: {} was finished successfully", instanceFilter, searchParam);
        return instanceEntities;
    }

    @Override
    @Transactional
    public void forget(final String name) {
        log.info("Delete instance by name: {} was started", name);
        final InstanceEntity instanceEntity = get(name);

        final StageEntity stageEntity = instanceEntity.getStage();
        if (stageEntity != null) {
            throw new InstanceUsedInPromotionPathException();
        }

        final List<TemplateFileEntity> templateFileEntities = instanceEntity.getTemplateFile();
        if (templateFileEntities != null && !templateFileEntities.isEmpty()) {
            throw new InstanceHasAttachedTemplateException();
        }
        instanceClient.unregister(instanceEntity.getSocket(), instanceEntity.getRegisterToken());
        instanceRepository.deleteByName(name);
        log.info("Delete instance by name: {} was finished", name);
    }

    @Override
    public InstanceEntity update(final String name, final InstanceEntity instanceEntity) {
        log.info("Update instance by name: {} and params: {} was started", name, instanceEntity);
        final InstanceEntity oldInstanceEntity = get(name);

        if (instanceRepository.findByName(instanceEntity.getName()).isPresent()) {
            throw new InstanceAlreadyExistsException(instanceEntity.getName());
        }
        if (!Objects.equals(instanceEntity.getSocket(), oldInstanceEntity.getSocket())
                && instanceRepository.findBySocket(instanceEntity.getSocket()).isPresent()) {
            throw new InstanceAlreadyExistsException(instanceEntity.getSocket());
        }

        final String instanceToken = instanceClient.register(instanceEntity.getSocket(), oldInstanceEntity.getHeaderName(), oldInstanceEntity.getHeaderValue()).getToken();

        try {
            instanceClient.unregister(oldInstanceEntity.getSocket(), oldInstanceEntity.getRegisterToken());
        } catch (SdkInstanceException e) {
            log.warn("An error occurred during unregister instance {}: {}", oldInstanceEntity.getSocket(), e.getMessage());
        }

        oldInstanceEntity.setName(instanceEntity.getName());
        oldInstanceEntity.setSocket(instanceEntity.getSocket());

        oldInstanceEntity.setRegisterToken(instanceToken);

        final InstanceEntity savedInstance = instanceRepository.save(oldInstanceEntity);

        oldInstanceEntity.getTemplateFile().forEach(templateFile -> templateDeploymentService.promoteTemplateToInstance(savedInstance, templateFile, false));

        log.info("Update instance by name: {} and params: {} was finished successfully", name, instanceEntity);
        return savedInstance;
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
                    return sortParam.getProperty().equals("createdOn") ? sortParam : sortParam.ignoreCase();
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }
}
