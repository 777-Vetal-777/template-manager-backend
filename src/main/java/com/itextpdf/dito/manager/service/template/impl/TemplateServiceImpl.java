package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.component.template.CompositeTemplateBuilder;
import com.itextpdf.dito.manager.dto.permission.PermissionDTO;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;
import com.itextpdf.dito.manager.entity.template.TemplateLogEntity;
import com.itextpdf.dito.manager.exception.AliasConstants;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionNotFoundException;
import com.itextpdf.dito.manager.exception.date.InvalidDateRangeException;
import com.itextpdf.dito.manager.exception.instance.deployment.SdkInstanceException;
import com.itextpdf.dito.manager.exception.role.RoleNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplateAlreadyExistsException;
import com.itextpdf.dito.manager.exception.template.TemplateBlockedByOtherUserException;
import com.itextpdf.dito.manager.exception.template.TemplateCannotBeBlockedException;
import com.itextpdf.dito.manager.exception.template.TemplateCannotBePromotedException;
import com.itextpdf.dito.manager.exception.template.TemplateDeleteException;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplateUuidNotFoundException;
import com.itextpdf.dito.manager.filter.template.TemplateFilter;
import com.itextpdf.dito.manager.filter.template.TemplateListFilter;
import com.itextpdf.dito.manager.model.template.TemplateModelWithRoles;
import com.itextpdf.dito.manager.model.template.TemplateRoleModel;
import com.itextpdf.dito.manager.model.template.part.TemplatePartModel;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.permission.PermissionService;
import com.itextpdf.dito.manager.service.role.RoleService;
import com.itextpdf.dito.manager.service.template.TemplateDeploymentService;
import com.itextpdf.dito.manager.service.template.TemplateFilePartService;
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
import com.itextpdf.dito.manager.model.template.TemplateModel;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class TemplateServiceImpl extends AbstractService implements TemplateService {
    private static final Logger log = LogManager.getLogger(TemplateServiceImpl.class);
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
    private final TemplateFilePartService templateFilePartService;

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
                               final TemplateFilePartService templateFilePartService) {
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
        this.templateFilePartService = templateFilePartService;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TemplateEntity create(final String templateName, final TemplateTypeEnum templateTypeEnum,
                                 final String dataCollectionName, final String email) {
        log.info("Create template with templateName: {} and type: {} and dataCollectionName: {} and email: {} was started", templateName, templateTypeEnum, dataCollectionName, email);
        final TemplateEntity templateEntity = create(templateName, templateTypeEnum, dataCollectionName, email, null);
        log.info("Create template with templateName: {} and type: {} and dataCollectionName: {} and email: {} was finished successfully", templateName, templateTypeEnum, dataCollectionName, email);

        return templateEntity;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TemplateEntity create(final String templateName, final TemplateTypeEnum templateTypeEnum,
                                 final String dataCollectionName, final String email, final List<TemplatePartModel> templateParts) {
        return create(templateName, templateTypeEnum, dataCollectionName, email, null, templateParts);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TemplateEntity create(final String templateName, final TemplateTypeEnum templateTypeEnum,
                                 final String dataCollectionName, final String email, final byte[] data, final List<TemplatePartModel> templateParts) {
        log.info("Create template with templateName: {} and type: {} and dataCollectionName: {}  and email: {} and parts: {} was started",
                templateName, templateTypeEnum, dataCollectionName, email, templateParts);
        throwExceptionIfTemplateNameNotMatchesPattern(templateName, AliasConstants.TEMPLATE);
        throwExceptionIfTemplateNameAlreadyIsRegistered(templateName);

        final TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setName(templateName);
        templateEntity.setType(templateTypeEnum);

        final UserEntity author = userService.findActiveUserByEmail(email);

        final TemplateLogEntity logEntity = createLogEntity(templateEntity, author);

        final TemplateFileEntity templateFileEntity = new TemplateFileEntity();
        templateFileEntity.setAuthor(author);
        templateFileEntity.setVersion(1L);
        templateFileEntity.setDeployed(false);
        templateFileEntity.setData(Optional.ofNullable(data).orElseGet(templateLoader::load));
        templateFileEntity.setTemplate(templateEntity);
        templateFileEntity.setAuthor(author);
        templateFileEntity.setCreatedOn(new Date());
        templateFileEntity.setModifiedOn(new Date());

        if (!StringUtils.isEmpty(dataCollectionName)) {
            final DataCollectionEntity dataCollectionEntity = dataCollectionRepository.findByName(dataCollectionName).orElseThrow(() -> new DataCollectionNotFoundException(dataCollectionName));
            templateFileEntity.setDataCollectionFile(dataCollectionEntity.getLatestVersion());
        }

        if (TemplateTypeEnum.COMPOSITION.equals(templateTypeEnum)) {
            if (Objects.nonNull(templateParts)) {
                fillTemplatePartsForTemplateFileEntity(dataCollectionName, templateParts, templateFileEntity);
            }
            templateFileEntity.setData(compositeTemplateConstructor.build(templateFileEntity));
        }

        templateEntity.setFiles(Stream.of(templateFileEntity).collect(Collectors.toList()));
        templateEntity.setLatestFile(templateFileEntity);
        templateEntity.setTemplateLogs(Stream.of(logEntity).collect(Collectors.toList()));

        final List<InstanceEntity> developerStageInstances = instanceRepository.getInstancesOnDevStage();
        templateFileEntity.getInstance().addAll(developerStageInstances);

        final TemplateEntity savedTemplateEntity = templateRepository.save(templateEntity);
        try{
            templateDeploymentService.promoteOnDefaultStage(templateFileEntity);
        } catch (SdkInstanceException exception){
            throw new TemplateCannotBePromotedException(templateName, exception.getMessage());
        }
        log.info("Create template with templateName: {} and type: {} and dataCollectionName: {}  and email: {} and parts: {} was finished successfully",
                templateName, templateTypeEnum, dataCollectionName, email, templateParts);
        return savedTemplateEntity;
    }

    private void fillTemplatePartsForTemplateFileEntity(final String dataCollectionName,
                                                        final List<TemplatePartModel> templateParts,
                                                        final TemplateFileEntity templateFileEntity) {
        final List<TemplateFilePartEntity> parts = templateFilePartService.createTemplatePartEntities(dataCollectionName, templateParts);
        final List<TemplateFilePartEntity> templateFileParts = templateFileEntity.getParts();
        for (final TemplateFilePartEntity templatePart : parts) {
            templatePart.setComposition(templateFileEntity);
            templateFileParts.add(templatePart);
        }

    }

    private TemplateLogEntity createLogEntity(final TemplateEntity templateEntity, final UserEntity author) {
        final TemplateLogEntity logEntity = new TemplateLogEntity();
        logEntity.setAuthor(author);
        logEntity.setDate(new Date());
        logEntity.setTemplate(templateEntity);
        return logEntity;
    }

    @Override
    public Page<TemplateModelWithRoles> getAll(final Pageable pageable, final TemplateFilter templateFilter, final String searchParam) {
        log.info("Get all templates by filter: {} and search: {} was started", templateFilter, searchParam);
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

        final Page<TemplateModel> templateEntities = StringUtils.isEmpty(searchParam)
                ? templateRepository
                .filter(pageWithSort, name, modifiedBy, types, dataCollectionName, editedOnStartDate, editedOnEndDate)
                : templateRepository
                .search(pageWithSort, name, modifiedBy, types, dataCollectionName, editedOnStartDate,
                        editedOnEndDate, searchParam.toLowerCase());
        final List<Long> listId = templateEntities.stream().map(TemplateModel::getId).collect(Collectors.toList());
        final List<TemplateRoleModel> roles = templateRepository.getRoles(listId);
        final Page<TemplateModelWithRoles> templateModelWithRoles = getListTemplatesWithRoles(roles, templateEntities);
        log.info("Get all templates by filter: {} and search: {} was finished successfully", templateFilter, searchParam);
        return templateModelWithRoles;
    }

    private Page<TemplateModelWithRoles> getListTemplatesWithRoles(final List<TemplateRoleModel> listRoles, final Page<TemplateModel> collections) {
        final Map<Long, List<TemplateRoleModel>> map = listRoles.stream().collect(Collectors.groupingBy(TemplateRoleModel::getTemplateId));
        return collections.map(template -> createTemplatesWithRoles(template, map));
    }

    private TemplateModelWithRoles createTemplatesWithRoles(final TemplateModel template, final Map<Long, List<TemplateRoleModel>> listRoles) {
        final TemplateModelWithRoles model = new TemplateModelWithRoles();
        model.setName(template.getTemplateName());
        model.setType(template.getType());
        model.setUuid(template.getUuid());
        model.setVersion(template.getVersion());
        model.setAuthor(template.getAuthor());
        model.setComment(template.getComment());
        model.setCreatedBy(template.getCreatedBy());
        model.setDataCollection(template.getDataCollection());
        model.setCreatedOn(template.getCreatedOn());
        model.setLastUpdate(template.getLastUpdate());
        model.setAppliedRoles(addListRolesToModel(listRoles, template.getId()));
        return model;
    }

    private Set<RoleDTO> addListRolesToModel(final Map<Long, List<TemplateRoleModel>> map, final Long templateId) {
        final List<TemplateRoleModel> roles = new ArrayList<>();
        if (map.get(templateId) != null) {
            roles.addAll(map.get(templateId));
        }
        final Set<RoleDTO> roleDTOS = new HashSet<>();
        for (final TemplateRoleModel role : roles) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getRoleName());
            roleDTO.setType(role.getType());
            roleDTO.setMaster(false);
            roleDTO.setPermissions(createListPermissions(role));
            roleDTOS.add(roleDTO);
        }
        return roleDTOS;
    }

    private List<PermissionDTO> createListPermissions(final TemplateRoleModel role) {
        final List<PermissionDTO> list = new ArrayList<>();
        if (Boolean.TRUE.equals(role.getE9_US75_EDIT_TEMPLATE_METADATA_STANDARD())) {
            final PermissionDTO permissionDTO = createPermission("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD");
            list.add(permissionDTO);
        }
        if (Boolean.TRUE.equals(role.getE9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD())) {
            final PermissionDTO permissionDTO = createPermission("E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD");
            list.add(permissionDTO);
        }
        if (Boolean.TRUE.equals(role.getE9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE())) {
            final PermissionDTO permissionDTO = createPermission("E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE");
            list.add(permissionDTO);
        }
        if (Boolean.TRUE.equals(role.getE9_US81_PREVIEW_TEMPLATE_STANDARD())) {
            final PermissionDTO permissionDTO = createPermission("E9_US81_PREVIEW_TEMPLATE_STANDARD");
            list.add(permissionDTO);
        }
        if (Boolean.TRUE.equals(role.getE9_US24_EXPORT_TEMPLATE_DATA())) {
            final PermissionDTO permissionDTO = createPermission("E9_US24_EXPORT_TEMPLATE_DATA");
            list.add(permissionDTO);
        }
        if (Boolean.TRUE.equals(role.getE9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE())) {
            final PermissionDTO permissionDTO = createPermission("E9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE");
            list.add(permissionDTO);
        }
        if (Boolean.TRUE.equals(role.getE9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED())) {
            final PermissionDTO permissionDTO = createPermission("E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED");
            list.add(permissionDTO);
        }
        return list;
    }

    private PermissionDTO createPermission(final String name) {
        final PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setOptionalForCustomRole(true);
        permissionDTO.setName(name);
        return permissionDTO;
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
    public List<TemplateEntity> getAllParts(final String templateName) {
        log.info("Get all parts by templateName: {} was started", templateName);
        final TemplateEntity templateEntity = findByName(templateName);
        final List<TemplateEntity> templateEntities = templateRepository.getTemplatesPartsByTemplateId(templateEntity.getId());
        log.info("Get all parts by templateName: {} was finished successfully", templateName);
        return templateEntities;
    }

    @Override
    public List<TemplateEntity> getAll(List<TemplateTypeEnum> filter) {
        return templateRepository.getListTemplates(filter);
    }

    @Override
    public TemplateEntity get(final String name) {
        return findByName(name);
    }

    @Override
    public TemplateEntity getByUuid(final String uuid) {
        return templateRepository.findByUuid(uuid).orElseThrow(() -> new TemplateUuidNotFoundException(uuid));
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return TemplateRepository.SUPPORTED_SORT_FIELDS;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TemplateEntity update(final String name, final TemplateEntity updatedTemplateEntity, final String userEmail) {
        log.info("Update template by name: {} and template: {} and email: {} was started", name, updatedTemplateEntity, userEmail);
        final TemplateEntity existingTemplate = findByName(name);
        final UserEntity userEntity = userService.findActiveUserByEmail(userEmail);

        if (!existingTemplate.getName().equals(updatedTemplateEntity.getName())) {
            throwExceptionIfTemplateNameNotMatchesPattern(updatedTemplateEntity.getName(), AliasConstants.TEMPLATE);
            throwExceptionIfTemplateNameAlreadyIsRegistered(updatedTemplateEntity.getName());
            existingTemplate.setName(updatedTemplateEntity.getName());
        }
        existingTemplate.setDescription(updatedTemplateEntity.getDescription());

        final TemplateLogEntity logEntity = createLogEntity(existingTemplate, userEntity);
        final Collection<TemplateLogEntity> templateLogs = existingTemplate.getTemplateLogs();
        templateLogs.add(logEntity);
        existingTemplate.setTemplateLogs(templateLogs);

        final TemplateEntity savedTemplateEntity = templateRepository.save(existingTemplate);
        log.info("Update template by name: {} and template: {} and email: {} was finished successfully", name, updatedTemplateEntity, userEmail);
        return savedTemplateEntity;
    }

    @Override
    @Transactional
    public TemplateEntity createNewVersion(final String name, final byte[] data, final String email, final String comment, final String newTemplateName, List<TemplatePartModel> templateParts) {
        log.info("Create new version of template by name: {} and email: {} and comment: {} and new name: {} and parts: {} was started",
                name, email, comment, newTemplateName, templateParts);
        final TemplateEntity existingTemplateEntity = findByName(name);
        if (newTemplateName != null) {
            existingTemplateEntity.setName(newTemplateName);
        }
        final UserEntity userEntity = userService.findActiveUserByEmail(email);

        final TemplateFileEntity oldTemplateFileVersion = templateFileRepository.findFirstByTemplate_IdOrderByVersionDesc(existingTemplateEntity.getId());
        final Long oldVersion = oldTemplateFileVersion.getVersion();

        final TemplateEntity templateEntity = createNewVersion(existingTemplateEntity, oldTemplateFileVersion, userEntity, data, templateParts, comment,
                oldVersion + 1);
        log.info("Create new version of template by name: {} and email: {} and comment: {} and new name: {} and parts: {} was finished successfully",
                name, email, comment, newTemplateName, templateParts);
        return templateEntity;
    }

    @Override
    @Transactional
    public TemplateEntity createNewVersionAsCopy(final TemplateFileEntity fileEntityToCopy,
                                                 final UserEntity userEntity,
                                                 final String comment) {
        log.info("Create new version as copy with templateFile: {} and user: {} and comment: {} was started", fileEntityToCopy, userEntity, comment);
        final TemplateEntity existingTemplateEntity = fileEntityToCopy.getTemplate();

        final TemplateFileEntity oldTemplateFileVersion = templateFileRepository.findFirstByTemplate_IdOrderByVersionDesc(existingTemplateEntity.getId());
        final Long oldVersion = oldTemplateFileVersion.getVersion();

        final TemplateEntity templateEntity = createNewVersion(existingTemplateEntity, fileEntityToCopy, userEntity, fileEntityToCopy.getData(), getTemplatePartsList(fileEntityToCopy), comment, oldVersion + 1);
        log.info("Create new version as copy with templateFile: {} and user: {} and comment: {} was finished successfully", fileEntityToCopy, userEntity, comment);
        return templateEntity;
    }

    @Override
    public TemplateEntity rollbackTemplate(final TemplateEntity existingTemplateEntity,
                                           final TemplateFileEntity templateVersionToBeRevertedTo,
                                           final UserEntity userEntity) {
        log.info("Rollback template with exist template: {} and templateVersionToBeRevertedTo: {} and user: {} was started", existingTemplateEntity, templateVersionToBeRevertedTo, userEntity);
        final TemplateFileEntity currentTemplateFile = existingTemplateEntity.getLatestFile();
        final String comment = new StringBuilder().append("Rollback to version: ").append(templateVersionToBeRevertedTo.getVersion()).toString();

        final TemplateEntity templateEntity = createVersionForTemplateEntity(existingTemplateEntity, templateVersionToBeRevertedTo, userEntity, templateVersionToBeRevertedTo.getData(), templateVersionToBeRevertedTo.getParts(), comment, currentTemplateFile.getVersion() + 1);
        log.info("Rollback template with exist template: {} and templateVersionToBeRevertedTo: {} and user: {} was finished successfully", existingTemplateEntity, templateVersionToBeRevertedTo, userEntity);
        return templateEntity;
    }

    @Transactional
    private TemplateEntity createVersionForTemplateEntity(final TemplateEntity existingTemplateEntity,
                                                          final TemplateFileEntity fileEntityToCopyDependencies,
                                                          final UserEntity userEntity,
                                                          final byte[] data,
                                                          final List<TemplateFilePartEntity> templateParts,
                                                          final String comment,
                                                          final Long newVersion) {
        log.info("Create version for template with template: {}, version: {}, user: {}, parts: {}, comment: {}, new version: {} was started",
                existingTemplateEntity, fileEntityToCopyDependencies, userEntity, templateParts, comment, newVersion);
        final TemplateEntity result;
        final TemplateLogEntity logEntity = createLogEntity(existingTemplateEntity, userEntity);
        final TemplateFileEntity fileEntity = createTemplateFileEntity((TemplateTypeEnum.COMPOSITION.equals(existingTemplateEntity.getType()) ? compositeTemplateConstructor.build(templateParts) : data),
                comment, existingTemplateEntity, userEntity,
                fileEntityToCopyDependencies.getDataCollectionFile(),
                fileEntityToCopyDependencies.getResourceFiles(),
                templateParts,
                newVersion);

        existingTemplateEntity.getFiles().add(fileEntity);
        existingTemplateEntity.getTemplateLogs().add(logEntity);
        existingTemplateEntity.setLatestLogRecord(logEntity);
        existingTemplateEntity.setLatestFile(fileEntity);

        result = templateRepository.save(existingTemplateEntity);
        templateDeploymentService.promoteOnDefaultStage(result.getLatestFile());

        final List<TemplateFileEntity> updatedStandardTemplateFileEntities = templateFileRepository.saveAll(createNewVersionForDependentCompositions(result, fileEntityToCopyDependencies, userEntity));
        updatedStandardTemplateFileEntities.forEach(templateDeploymentService::promoteOnDefaultStage);
        log.info("Create version for template with template: {}, version: {}, user: {}, parts: {}, comment: {}, new version: {} was finished successfully",
                existingTemplateEntity, fileEntityToCopyDependencies, userEntity, templateParts, comment, newVersion);
        return result;
    }

    private List<TemplateFileEntity> createNewVersionForDependentCompositions(final TemplateEntity templateEntity,
                                                                              final TemplateFileEntity previousVersionFileEntity,
                                                                              final UserEntity userEntity) {
        final TemplateFileEntity templateEntityLatestFile = templateEntity.getLatestFile();
        final String updatedDependenciesComment = new StringBuilder(templateEntity.getName()).append(" was updated to version ").append(templateEntityLatestFile.getVersion().toString()).toString();
        final List<TemplateEntity> compositions = templateFileRepository.getTemplateCompositionsByTemplateFileId(previousVersionFileEntity.getId());

        return compositions.stream().map(composition -> {
            final TemplateFileEntity latestFile = composition.getLatestFile();
            final TemplateEntity updatedComposition = createVersionForTemplateEntity(composition, latestFile, userEntity, null, latestFile.getParts(), updatedDependenciesComment, latestFile.getVersion() + 1);
            final TemplateFileEntity updatedLatestFile = updatedComposition.getLatestFile();
            updatedLatestFile.getParts().stream()
                    .filter(part -> part.getPart().equals(previousVersionFileEntity))
                    .forEach(part -> part.setPart(templateEntityLatestFile));
            return updatedLatestFile;
        }).collect(Collectors.toList());
    }

    private TemplateEntity createNewVersion(final TemplateEntity existingTemplateEntity,
                                            final TemplateFileEntity fileEntityToCopyDependencies,
                                            final UserEntity userEntity,
                                            final byte[] data,
                                            final List<TemplatePartModel> templateParts,
                                            final String comment,
                                            final Long newVersion) {
        final List<TemplateFilePartEntity> templatePartsForTemplateFileEntity;

        if (TemplateTypeEnum.COMPOSITION.equals(existingTemplateEntity.getType()) && Objects.nonNull(templateParts)) {
            final String dataCollectionName = Optional.ofNullable(fileEntityToCopyDependencies.getDataCollectionFile()).map(DataCollectionFileEntity::getDataCollection).map(DataCollectionEntity::getName).orElse(null);
            templatePartsForTemplateFileEntity = templateFilePartService.createTemplatePartEntities(dataCollectionName, templateParts);
        } else {
            templatePartsForTemplateFileEntity = Collections.emptyList();
        }

        return createVersionForTemplateEntity(existingTemplateEntity,
                fileEntityToCopyDependencies,
                userEntity,
                data,
                templatePartsForTemplateFileEntity,
                comment,
                newVersion);
    }

    private TemplateFileEntity createTemplateFileEntity(final byte[] data,
                                                        final String comment,
                                                        final TemplateEntity existingTemplateEntity,
                                                        final UserEntity userEntity,
                                                        final DataCollectionFileEntity dataCollectionFileEntity,
                                                        final Set<ResourceFileEntity> resourceFileEntities,
                                                        final List<TemplateFilePartEntity> templateFilePartEntities,
                                                        final Long versionNumber) {
        //Imitation of new file upload (which will be performed from Editor)
        final TemplateFileEntity fileEntity = new TemplateFileEntity();
        fileEntity.setTemplate(existingTemplateEntity);
        fileEntity.setVersion(versionNumber);
        fileEntity.setComment(comment);
        fileEntity.setAuthor(userEntity);
        fileEntity.setCreatedOn(new Date());
        fileEntity.setDeployed(false);
        fileEntity.setModifiedOn(new Date());
        fileEntity.setDataCollectionFile(dataCollectionFileEntity);
        fileEntity.getResourceFiles().addAll(resourceFileEntities.stream().map(ResourceFileEntity::getResource).map(ResourceEntity::getLatestFile).flatMap(Collection::stream).collect(Collectors.toList())); //update dependent resources to latest versions
        Optional.ofNullable(templateFilePartEntities).ifPresent(filePartEntities -> fileEntity.getParts().addAll(filePartEntities.stream().map(part -> templateFilePartService.updateComposition(part, fileEntity)).collect(Collectors.toList()))); //update dependent template parts to latest versions
        fileEntity.setData(Optional.ofNullable(data).orElseGet(templateLoader::load));
        final List<InstanceEntity> developerStageInstances = instanceRepository.getInstancesOnDevStage();
        fileEntity.getInstance().addAll(developerStageInstances);
        return fileEntity;
    }

    @Override
    public TemplateEntity applyRole(final String templateName, final String roleName, final List<String> permissions, final String email) {
        log.info("Apply role for template name: {} with roleName: {} , permissions: {} and email: {} was started", templateName, roleName, permissions, email);
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
        final TemplateEntity updatedTemplateEntity = templateRepository.save(templateEntity);
        log.info("Apply role for template name: {} with roleName: {} , permissions: {} and email: {} was finished successfully", templateName, roleName, permissions, email);
        return updatedTemplateEntity;
    }

    @Override
    public TemplateEntity detachRole(final String templateName, final String roleName, final String email) {
        log.info("Detach role by templateName: {} and roleName: {} and email: {} was started", templateName, roleName, email);
        final TemplateEntity templateEntity = findByName(templateName);

        final RoleEntity roleEntity = roleService.getSlaveRole(roleName, templateEntity);

        if (roleEntity == null) {
            throw new RoleNotFoundException(roleName);
        }

        templateEntity.getAppliedRoles().remove(roleEntity);
        roleService.delete(roleEntity);
        final TemplateEntity updatedTemplateEntity = templateRepository.save(templateEntity);
        log.info("Detach role by templateName: {} and roleName: {} and email: {} was finished successfully", templateName, roleName, email);
        return updatedTemplateEntity;
    }

    @Override
    public TemplateEntity delete(final String templateName) {
        log.info("Delete template by name: {} was started", templateName);
        final TemplateEntity templateEntity = findByName(templateName);
        final Optional<TemplateFileEntity> deployedTemplateVersion = templateEntity.getFiles().stream()
                .filter(TemplateFileEntity::getDeployed)
                .findAny();
        if (deployedTemplateVersion.isPresent()) {
            throw new TemplateDeleteException("Template has deployed versions that should be un-deployed first");
        }
        final boolean hasOutBoundDependencies = templateFileRepository.countTemplateVersionsUsedInCompositions(templateName) > 0;
        if (hasOutBoundDependencies) {
            throw new TemplateDeleteException("Template has outbound dependencies");
        }
        final List<TemplateFileEntity> templateFileEntities = templateEntity.getFiles();
        templateRepository.delete(templateEntity);
        templateDeploymentService.removeAllVersionsFromDefaultStage(templateFileEntities);
        log.info("Delete template by name: {} was finished successfully", templateName);
        return templateEntity;
    }

    @Override
    public TemplateEntity block(final String userEmail, final String templateName) {
        log.info("Block template with name: {} by user email: {} was started", templateName, userEmail);
        final TemplateEntity templateEntity = findByName(templateName);
        final UserEntity currentUser = userService.findActiveUserByEmail(userEmail);
        if (TemplateTypeEnum.COMPOSITION == templateEntity.getType()) {
            throw new TemplateCannotBeBlockedException("Composition templates cannot be blocked");
        }
        if (Objects.nonNull(templateEntity.getBlockedAt()) && !Objects.equals(currentUser, templateEntity.getBlockedBy())) {
            throw new TemplateBlockedByOtherUserException(templateEntity.getName(), templateEntity.getBlockedBy().getEmail());
        }
        templateEntity.setBlockedAt(new Date());
        templateEntity.setBlockedBy(currentUser);
        final TemplateEntity updatedTemplateEntity = templateRepository.save(templateEntity);
        log.info("Block template with name: {} by user email: {} was finished successfully", templateName, userEmail);
        return updatedTemplateEntity;
    }

    @Override
    public TemplateEntity unblock(final String userEmail, final String templateName) {
        log.info("Unblock template by userEmail: {} and templateName: {} was started", userEmail, templateName);
        final TemplateEntity templateEntity = findByName(templateName);
        final UserEntity currentUser = userService.findActiveUserByEmail(userEmail);
        if (Objects.nonNull(templateEntity.getBlockedAt()) && !Objects.equals(currentUser, templateEntity.getBlockedBy())) {
            throw new TemplateBlockedByOtherUserException(templateName, templateEntity.getBlockedBy().getEmail());
        }
        templateEntity.setBlockedBy(null);
        templateEntity.setBlockedAt(null);
        final TemplateEntity updatedTemplateEntity = templateRepository.save(templateEntity);
        log.info("Unblock template by userEmail: {} and templateName: {} was finished successfully", userEmail, templateName);
        return updatedTemplateEntity;
    }

    private void throwExceptionIfTemplateNameAlreadyIsRegistered(final String templateName) {
        if (templateRepository.findByName(templateName).isPresent()) {
            throw new TemplateAlreadyExistsException(templateName);
        }
    }

    private TemplateEntity findByName(final String name) {
        return templateRepository.findByName(name).orElseThrow(() -> new TemplateNotFoundException(name));
    }

    private Pageable updateSort(final Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("dataCollection")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "data.name");
                    }
                    if (sortParam.getProperty().equals("modifiedBy")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "lastAuthorLog.firstName");
                    }
                    if (sortParam.getProperty().equals("modifiedOn")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "latestLogRecord.date");
                    }
                    return sortParam.getProperty().equals("latestLogRecord.date") ? sortParam : sortParam.ignoreCase();
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    private List<TemplatePartModel> getTemplatePartsList(final TemplateFileEntity fileEntityToCopy) {
        return fileEntityToCopy.getParts().stream().map(templateFilePartService::mapFromEntity).collect(Collectors.toList());
    }
}