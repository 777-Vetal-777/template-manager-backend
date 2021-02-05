package com.itextpdf.dito.manager.service.template.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.component.template.CompositeTemplateBuilder;
import com.itextpdf.dito.manager.dto.template.create.TemplatePartDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;
import com.itextpdf.dito.manager.entity.template.TemplateLogEntity;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionNotFoundException;
import com.itextpdf.dito.manager.exception.date.InvalidDateRangeException;
import com.itextpdf.dito.manager.exception.role.RoleNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplateAlreadyExistsException;
import com.itextpdf.dito.manager.exception.template.TemplateBlockedByOtherUserException;
import com.itextpdf.dito.manager.exception.template.TemplateHasWrongStructureException;
import com.itextpdf.dito.manager.exception.template.TemplateDeleteException;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.filter.template.TemplateFilter;
import com.itextpdf.dito.manager.filter.template.TemplateListFilter;
import com.itextpdf.dito.manager.filter.template.TemplatePermissionFilter;
import com.itextpdf.dito.manager.model.template.part.PartSettings;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateLogRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.permission.PermissionService;
import com.itextpdf.dito.manager.service.role.RoleService;
import com.itextpdf.dito.manager.service.template.TemplateDeploymentService;
import com.itextpdf.dito.manager.service.template.TemplateLoader;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class TemplateServiceImpl extends AbstractService implements TemplateService {
    private final TemplateFileRepository templateFileRepository;
    private final TemplateRepository templateRepository;
    private final InstanceRepository instanceRepository;
    private final UserService userService;
    private final TemplateLoader templateLoader;
    private final DataCollectionRepository dataCollectionRepository;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final TemplateDeploymentService templateDeploymentService;
    private final CompositeTemplateBuilder compositeTemplateConstructor;
    private final TemplateLogRepository templateLogRepository;

    private final Logger log = LogManager.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TemplateServiceImpl(final TemplateFileRepository templateFileRepository,
                               final TemplateRepository templateRepository,
                               final UserService userService,
                               final TemplateLoader templateLoader,
                               final DataCollectionRepository dataCollectionRepository,
                               final RoleService roleService,
                               final PermissionService permissionService,
                               final InstanceRepository instanceRepository,
                               final TemplateDeploymentService templateDeploymentService,
                               final CompositeTemplateBuilder compositeTemplateConstructor,
                               final TemplateLogRepository templateLogRepository) {
        this.templateFileRepository = templateFileRepository;
        this.templateRepository = templateRepository;
        this.userService = userService;
        this.templateLoader = templateLoader;
        this.dataCollectionRepository = dataCollectionRepository;
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.instanceRepository = instanceRepository;
        this.templateDeploymentService = templateDeploymentService;
        this.compositeTemplateConstructor = compositeTemplateConstructor;
        this.templateLogRepository = templateLogRepository;
    }

    @Override
    @Transactional
    public TemplateEntity create(final String templateName, final TemplateTypeEnum templateTypeEnum,
                                 final String dataCollectionName, final String email) {
        return create(templateName, templateTypeEnum, dataCollectionName, email, null);
    }

    @Override
    @Transactional
    public TemplateEntity create(final String templateName, final TemplateTypeEnum templateTypeEnum,
                                 final String dataCollectionName, final String email, List<TemplatePartDTO> templatePartDTOs) {
        throwExceptionIfTemplateNameAlreadyIsRegistered(templateName);

        final TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setName(templateName);
        templateEntity.setType(templateTypeEnum);

        final UserEntity author = userService.findByEmail(email);

        final TemplateLogEntity logEntity = createLogEntity(templateEntity, author);

        final TemplateFileEntity templateFileEntity = new TemplateFileEntity();
        templateFileEntity.setAuthor(author);
        templateFileEntity.setVersion(1L);
        templateFileEntity.setDeployed(false);
        templateFileEntity.setData(templateLoader.load());
        templateFileEntity.setTemplate(templateEntity);
        templateFileEntity.setAuthor(author);
        templateFileEntity.setCreatedOn(new Date());
        templateFileEntity.setModifiedOn(new Date());

        if (!StringUtils.isEmpty(dataCollectionName)) {
            final DataCollectionEntity dataCollectionEntity = dataCollectionRepository.findByName(dataCollectionName).orElseThrow(() -> new DataCollectionNotFoundException(dataCollectionName));
            templateFileEntity.setDataCollectionFile(dataCollectionEntity.getLatestVersion());
        }

        if (TemplateTypeEnum.COMPOSITION.equals(templateTypeEnum)) {
            if (Objects.nonNull(templatePartDTOs)) {
                createTemplatePartsForTemplateFileEntity(dataCollectionName, templatePartDTOs, templateFileEntity);
            }
            templateFileEntity.setData(compositeTemplateConstructor.build(templateFileEntity));
        }

        templateEntity.setFiles(Collections.singletonList(templateFileEntity));
        templateEntity.setLatestFile(templateFileEntity);
        templateEntity.setTemplateLogs(Collections.singletonList(logEntity));

        final List<InstanceEntity> developerStageInstances = instanceRepository.getInstancesOnDevStage();
        templateFileEntity.getInstance().addAll(developerStageInstances);

        final TemplateEntity savedTemplate = templateRepository.save(templateEntity);
        templateDeploymentService.promoteOnDefaultStage(savedTemplate.getName());
        return savedTemplate;
    }

    private void createTemplatePartsForTemplateFileEntity(final String dataCollectionName,
                                                          final List<TemplatePartDTO> templatePartDTOs,
                                                          final TemplateFileEntity templateFileEntity) {
        final List<TemplateEntity> templatePartList = templateRepository.getTemplatesWithLatestFileByName(templatePartDTOs.stream().map(TemplatePartDTO::getTemplateName).collect(Collectors.toList()));
        final Map<String, TemplateFileEntity> templateFilePartMap = templatePartList.stream().collect(Collectors.toMap(TemplateEntity::getName, TemplateEntity::getLatestFile, (templateFileEntity1, templateFileEntity2) -> templateFileEntity1));

        //checks that all templates exists
        throwExceptionIfSomeTemplatesAreNotFound(templatePartDTOs, templateFilePartMap);

        //checks that all templates has the same (or absent dataCollection)
        throwExceptionIfTemplatesHaveAnotherDataCollections(templatePartList, dataCollectionName);

        //checks that exists at most one HEADER and FOOTER
        throwExceptionIfTooMuchType(templatePartList, TemplateTypeEnum.HEADER);
        throwExceptionIfTooMuchType(templatePartList, TemplateTypeEnum.FOOTER);

        final List<TemplateFilePartEntity> parts = templateFileEntity.getParts();
        for (final TemplatePartDTO templatePart : templatePartDTOs) {
            final TemplateFileEntity partTemplateFileEntity = templateFilePartMap.get(templatePart.getTemplateName());
            final TemplateFilePartEntity templateFilePartEntity = createTemplateFilePartEntity(templateFileEntity, partTemplateFileEntity, templatePart);

            parts.add(templateFilePartEntity);

            partTemplateFileEntity.getCompositions().add(templateFilePartEntity);
        }
    }

    private TemplateFilePartEntity createTemplateFilePartEntity(final TemplateFileEntity compositionTemplateFileEntity,
                                                                final TemplateFileEntity partTemplateFileEntity,
                                                                final TemplatePartDTO templatePart) {
        final TemplateFilePartEntity templateFilePartEntity = new TemplateFilePartEntity();
        templateFilePartEntity.setComposition(compositionTemplateFileEntity);
        templateFilePartEntity.setPart(partTemplateFileEntity);
        templateFilePartEntity.setCondition(templatePart.getCondition());

        final PartSettings partSettings = new PartSettings();
        Optional.ofNullable(templatePart.getStartOnNewPage()).ifPresent(partSettings::setStartOnNewPage);
        try {
            templateFilePartEntity.setSettings(objectMapper.writeValueAsString(partSettings));
        } catch (JsonProcessingException e) {
            log.error(e);
        }
        return templateFilePartEntity;
    }

    private TemplateLogEntity createLogEntity(final TemplateEntity templateEntity, final UserEntity author) {
        final TemplateLogEntity logEntity = new TemplateLogEntity();
        logEntity.setAuthor(author);
        logEntity.setDate(new Date());
        logEntity.setTemplate(templateEntity);
        return logEntity;
    }

    @Override
    public Page<TemplateEntity> getAll(final Pageable pageable, final TemplateFilter templateFilter, final String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final Pageable pageWithSort = updateSort(pageable);
        final String name = getStringFromFilter(templateFilter.getName());
        final String modifiedBy = getStringFromFilter(templateFilter.getModifiedBy());
        final List<TemplateTypeEnum> types = templateFilter.getType();
        final String dataCollectionName = getStringFromFilter(templateFilter.getDataCollection());

        Date editedOnStartDate = null;
        Date editedOnEndDate = null;
        final List<String> editedOnDateRange = templateFilter.getModifiedOn();
        if (editedOnDateRange != null) {
            if (editedOnDateRange.size() != 2) {
                throw new InvalidDateRangeException();
            }
            editedOnStartDate = getStartDateFromRange(editedOnDateRange);
            editedOnEndDate = getEndDateFromRange(editedOnDateRange);
        }

        return StringUtils.isEmpty(searchParam)
                ? templateRepository
                .filter(pageWithSort, name, modifiedBy, types, dataCollectionName, editedOnStartDate, editedOnEndDate)
                : templateRepository
                .search(pageWithSort, name, modifiedBy, types, dataCollectionName, editedOnStartDate,
                        editedOnEndDate, searchParam.toLowerCase());
    }

    @Override
    public List<TemplateEntity> getAll() {
        return templateRepository.findAll();
    }

    @Override
    public List<TemplateEntity> getAll(final TemplateListFilter templateListFilter) {
        return templateRepository.getListTemplates(templateListFilter.getType(), templateListFilter.getDataCollection());
    }

    @Override
    public List<TemplateEntity> getAll(final String templateName) {
        final TemplateEntity templateEntity = findByName(templateName);
        return templateRepository.getTemplatesPartsByTemplateId(templateEntity.getId());
    }

    @Override
    public TemplateEntity get(final String name) {
        return findByName(name);
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return TemplateRepository.SUPPORTED_SORT_FIELDS;
    }

    @Override
    public TemplateEntity update(final String name, final TemplateEntity updatedTemplateEntity, final String userEmail) {
        final TemplateEntity existingTemplate = findByName(name);
        final UserEntity userEntity = userService.findByEmail(userEmail);

        if (!existingTemplate.getName().equals(updatedTemplateEntity.getName())) {
            existingTemplate.setName(updatedTemplateEntity.getName());
            throwExceptionIfTemplateNameAlreadyIsRegistered(updatedTemplateEntity.getName());
        }
        existingTemplate.setDescription(updatedTemplateEntity.getDescription());

        final TemplateLogEntity logEntity = createLogEntity(existingTemplate, userEntity);
        final Collection<TemplateLogEntity> templateLogs = existingTemplate.getTemplateLogs();
        templateLogs.add(logEntity);
        existingTemplate.setTemplateLogs(templateLogs);

        return templateRepository.save(existingTemplate);
    }

    @Override
    public TemplateEntity createNewVersion(final String name, final byte[] data, final String email, final String comment, final String newTemplateName) {
        final TemplateEntity existingTemplateEntity = findByName(name);
        if (newTemplateName != null) {
            existingTemplateEntity.setName(newTemplateName);
        }
        final UserEntity userEntity = userService.findByEmail(email);

        final TemplateFileEntity oldTemplateFileVersion = templateFileRepository.findFirstByTemplate_IdOrderByVersionDesc(existingTemplateEntity.getId());
        final Long oldVersion = oldTemplateFileVersion.getVersion();

        return createNewVersion(existingTemplateEntity, oldTemplateFileVersion, userEntity, data, comment, oldVersion + 1);
    }

    @Override
    public TemplateEntity createNewVersionAsCopy(final TemplateFileEntity fileEntityToCopy,
                                                 final UserEntity userEntity,
                                                 final String comment) {
        final TemplateEntity existingTemplateEntity = fileEntityToCopy.getTemplate();

        final TemplateFileEntity oldTemplateFileVersion = templateFileRepository.findFirstByTemplate_IdOrderByVersionDesc(existingTemplateEntity.getId());
        final Long oldVersion = oldTemplateFileVersion.getVersion();

        return createNewVersion(existingTemplateEntity, fileEntityToCopy, userEntity, fileEntityToCopy.getData(), comment, oldVersion + 1);
    }

    @Override
    public TemplateEntity rollbackTemplate(final TemplateEntity existingTemplateEntity,
                                           final TemplateFileEntity templateVersionToBeRevertedTo,
                                           final UserEntity userEntity) {
        final TemplateFileEntity currentTemplateFile = existingTemplateEntity.getLatestFile();
        final String comment = new StringBuilder().append("Rollback to version: ").append(templateVersionToBeRevertedTo.getVersion()).toString();
        return createNewVersion(existingTemplateEntity, currentTemplateFile, userEntity, templateVersionToBeRevertedTo.getData(), comment, currentTemplateFile.getVersion() + 1);
    }

    private TemplateEntity createNewVersion(final TemplateEntity existingTemplateEntity,
                                            final TemplateFileEntity fileEntityToCopyDependencies,
                                            final UserEntity userEntity,
                                            final byte[] data,
                                            final String comment,
                                            final Long newVersion) {
        final TemplateEntity result;
        final TemplateLogEntity logEntity = createLogEntity(existingTemplateEntity, userEntity);

        final TemplateFileEntity fileEntity = createTemplateFileEntity(data, comment, existingTemplateEntity, userEntity, newVersion, fileEntityToCopyDependencies);
        existingTemplateEntity.getFiles().add(fileEntity);
        existingTemplateEntity.getTemplateLogs().add(logEntity);
        existingTemplateEntity.setLatestLogRecord(logEntity);
        existingTemplateEntity.setLatestFile(fileEntity);

        result = templateRepository.save(existingTemplateEntity);
        templateDeploymentService.promoteOnDefaultStage(existingTemplateEntity.getName());
        return result;
    }

    private TemplateFileEntity createTemplateFileEntity(final byte[] data,
                                                        final String comment,
                                                        final TemplateEntity existingTemplateEntity,
                                                        final UserEntity userEntity,
                                                        final Long versionNumber,
                                                        final TemplateFileEntity entityToCopyDependencies) {
        //Imitation of new file upload (which will be performed from Editor)
        final TemplateFileEntity fileEntity = new TemplateFileEntity();
        fileEntity.setTemplate(existingTemplateEntity);
        fileEntity.setVersion(versionNumber);
        fileEntity.setComment(comment);
        fileEntity.setAuthor(userEntity);
        fileEntity.setCreatedOn(new Date());
        fileEntity.setDeployed(false);
        fileEntity.setModifiedOn(new Date());
        fileEntity.setDataCollectionFile(entityToCopyDependencies.getDataCollectionFile());
        fileEntity.getResourceFiles().addAll(entityToCopyDependencies.getResourceFiles());
        if (data != null) {
            fileEntity.setData(data);
        } else {
            fileEntity.setData(templateLoader.load());
        }
        final List<InstanceEntity> developerStageInstances = instanceRepository.getInstancesOnDevStage();
        fileEntity.getInstance().addAll(developerStageInstances);
        return fileEntity;
    }

    @Override
    public Page<RoleEntity> getRoles(final Pageable pageable, final String name, final TemplatePermissionFilter filter) {
        final TemplateEntity templateEntity = findByName(name);
        return roleService.getSlaveRolesByTemplate(pageable, filter, templateEntity);
    }

    @Override
    public TemplateEntity applyRole(final String templateName, final String roleName, final List<String> permissions, final String email) {
        final TemplateEntity templateEntity = findByName(templateName);

        RoleEntity slaveRoleEntity = roleService.getSlaveRole(roleName, templateEntity);
        if (slaveRoleEntity == null) {
            // line below will throw not found exception in case if user tries to create slave role which doesn't have master role.
            final RoleEntity masterRoleEntity = roleService.getMasterRole(roleName);
            checkNotAdminRole(masterRoleEntity);

            slaveRoleEntity = new RoleEntity();
            slaveRoleEntity.setName(masterRoleEntity.getName());
            slaveRoleEntity.setType(masterRoleEntity.getType());
            slaveRoleEntity.setMaster(Boolean.FALSE);
        } else {
            slaveRoleEntity.getPermissions().clear();
            templateEntity.getAppliedRoles().remove(slaveRoleEntity);
        }

        for (final String permission : permissions) {
            final PermissionEntity permissionEntity = permissionService.get(permission);
            slaveRoleEntity.getPermissions().add(permissionEntity);
        }
        slaveRoleEntity.getTemplates().add(templateEntity);

        templateEntity.getAppliedRoles().add(slaveRoleEntity);
        return templateRepository.save(templateEntity);
    }

    @Override
    public TemplateEntity detachRole(final String templateName, final String roleName, final String email) {
        final TemplateEntity templateEntity = findByName(templateName);

        final RoleEntity roleEntity = roleService.getSlaveRole(roleName, templateEntity);

        if (roleEntity == null) {
            throw new RoleNotFoundException(roleName);
        }

        templateEntity.getAppliedRoles().remove(roleEntity);
        roleService.delete(roleEntity);
        return templateRepository.save(templateEntity);
    }

    @Override
    public TemplateEntity delete(final String templateName) {
        final TemplateEntity templateEntity = findByName(templateName);
        final Optional<TemplateFileEntity> deployedTemplateVersion = templateEntity.getFiles().stream()
                .filter(TemplateFileEntity::getDeployed)
                .findAny();
        if (deployedTemplateVersion.isPresent()){
            throw new TemplateDeleteException("Template has deployed versions that should be un-deployed first");
        }
        //TODO implement check for outbound dependencies when template composition is completed DTM-1614
        final boolean hasOutBoundDependencies = false;
        if (hasOutBoundDependencies){
            throw new TemplateDeleteException("Template has outbound dependencies");
        }
        templateDeploymentService.removeAllVersionsFromDefaultStage(templateName);
        templateLogRepository.deleteAll(templateEntity.getTemplateLogs());
        templateFileRepository.deleteAll(templateEntity.getFiles());
        templateRepository.delete(templateEntity);
        return templateEntity;
    }

    @Override
    public TemplateEntity block(final String userEmail, final String templateName) {
        final TemplateEntity templateEntity = findByName(templateName);
        final UserEntity currentUser = userService.findByEmail(userEmail);
        if (templateEntity.getBlockedAt() != null && currentUser != templateEntity.getBlockedBy()) {
            throw new TemplateBlockedByOtherUserException(templateEntity.getName(), templateEntity.getBlockedBy().getEmail());
        }
        templateEntity.setBlockedAt(new Date());
        templateEntity.setBlockedBy(currentUser);
        return templateRepository.save(templateEntity);
    }

    @Override
    public TemplateEntity unblock(final String userEmail, final String templateName) {
        final TemplateEntity templateEntity = findByName(templateName);
        final UserEntity currentUser = userService.findByEmail(userEmail);
        if (currentUser != templateEntity.getBlockedBy()) {
            throw new TemplateBlockedByOtherUserException(templateName, templateEntity.getBlockedBy().getEmail());
        }
        templateEntity.setBlockedBy(null);
        templateEntity.setBlockedAt(null);
        return templateRepository.save(templateEntity);
    }

    private void throwExceptionIfTemplateNameAlreadyIsRegistered(final String templateName) {
        if (templateRepository.findByName(templateName).isPresent()) {
            throw new TemplateAlreadyExistsException(templateName);
        }
    }

    private void throwExceptionIfTooMuchType(final List<TemplateEntity> templatePartList, final TemplateTypeEnum checkedType) {
        if (templatePartList.stream().filter(entity -> checkedType.equals(entity.getType())).count() > 1) {
            throw new TemplateHasWrongStructureException(new StringBuilder("Template parts have more than one ").append(checkedType).toString());
        }
    }

    private void throwExceptionIfTemplatesHaveAnotherDataCollections(final List<TemplateEntity> templatePartList, final String dataCollectionName) {
        if (!templatePartList.stream().allMatch(entity -> {
            final String dataCollection = Optional.ofNullable(entity.getLatestFile().getDataCollectionFile()).map(DataCollectionFileEntity::getDataCollection).map(DataCollectionEntity::getName).orElse(null);
            return Objects.equals(dataCollectionName, dataCollection) || Objects.equals(null, dataCollection);
        })) {
            throw new TemplateHasWrongStructureException("Template parts belongs to different Data Collections");
        }
    }

    private void throwExceptionIfSomeTemplatesAreNotFound(final List<TemplatePartDTO> templatePartDTOs, final Map<String, TemplateFileEntity> templateFilePartMap) {
        final List<String> missedTemplateNames = templatePartDTOs.stream().map(TemplatePartDTO::getTemplateName).filter(partName -> !templateFilePartMap.containsKey(partName)).collect(Collectors.toList());
        if (!missedTemplateNames.isEmpty()) {
            throw new TemplateNotFoundException(missedTemplateNames.get(0));
        }
    }

    private TemplateEntity findByName(final String name) {
        return templateRepository.findByName(name).orElseThrow(() -> new TemplateNotFoundException(name));
    }

    private Pageable updateSort(final Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("dataCollection")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "dataCollection.name");
                    }
                    if (sortParam.getProperty().equals("modifiedBy")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "latestLogRecord.author.firstName");
                    }
                    if (sortParam.getProperty().equals("modifiedOn")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "latestLogRecord.date");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }
}