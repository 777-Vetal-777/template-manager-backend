package com.itextpdf.dito.manager.service.role.impl;

import com.itextpdf.dito.manager.dto.permission.PermissionDTO;
import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.RoleTypeEnum;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.permission.PermissionCantBeAttachedToCustomRoleException;
import com.itextpdf.dito.manager.exception.permission.PermissionNotFoundException;
import com.itextpdf.dito.manager.exception.role.AttemptToDeleteSystemRoleException;
import com.itextpdf.dito.manager.exception.role.RoleAlreadyExistsException;
import com.itextpdf.dito.manager.exception.role.RoleNotFoundException;
import com.itextpdf.dito.manager.exception.role.UnableToDeleteSingularRoleException;
import com.itextpdf.dito.manager.exception.role.UnableToUpdateSystemRoleException;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionPermissionFilter;
import com.itextpdf.dito.manager.filter.role.RoleFilter;
import com.itextpdf.dito.manager.filter.role.RoleUserFilter;
import com.itextpdf.dito.manager.filter.template.TemplatePermissionFilter;
import com.itextpdf.dito.manager.model.role.RoleModel;
import com.itextpdf.dito.manager.model.role.RolePermissionsModel;
import com.itextpdf.dito.manager.model.role.RoleUsersModel;
import com.itextpdf.dito.manager.model.role.RoleWithUsersModel;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.permission.PermissionService;
import com.itextpdf.dito.manager.service.role.RoleService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getBooleanMultiselectFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Component
public class RoleServiceImpl extends AbstractService implements RoleService {
    private static final Logger log = LogManager.getLogger(RoleServiceImpl.class);
    // Repositories
    private final RoleRepository roleRepository;
    // Services
    private final UserService userService;
    private final PermissionService permissionService;

    public RoleServiceImpl(final RoleRepository roleRepository,
                           final UserService userService,
                           final PermissionService permissionService) {
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @Override
    public RoleEntity getMasterRole(final String name) {
        return roleRepository.findByNameAndMasterTrue(name).orElseThrow(() -> new RoleNotFoundException(name));
    }

    @Override
    public RoleEntity getSlaveRole(final String name, final ResourceEntity resourceEntity) {
        return roleRepository.findByNameAndMasterFalseAndResources(name, resourceEntity);
    }

    @Override
    public RoleEntity getSlaveRole(final String name, final DataCollectionEntity dataCollectionEntity) {
        return roleRepository.findByNameAndMasterFalseAndDataCollections(name, dataCollectionEntity);
    }

    @Override
    public Page<RoleEntity> getSlaveRolesByResource(final Pageable pageable, final RoleFilter roleFilter, final ResourceEntity resource) {
        log.info("Get slave roles by resource: {} and filter: {} was started", resource, roleFilter);
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final String name = getStringFromFilter(roleFilter.getName());
        final List<RoleTypeEnum> roleTypeEnums = roleFilter.getType();

        final Pageable pageWithSort = updateSort(pageable);
        final Page<RoleEntity> roleEntities = roleRepository.findAllByResourcesAndMasterFalse(pageWithSort, resource, name, roleTypeEnums);
        log.info("Get slave roles by resource: {} and filter: {} was finished successfully", resource, roleFilter);
        return roleEntities;
    }

    @Override
    public Page<RoleEntity> getSlaveRolesByDataCollection(final Pageable pageable, final DataCollectionPermissionFilter filter, final DataCollectionEntity dataCollection) {
        log.info("Get slave roles by dataCollection: {} and filter: {} was started", dataCollection, filter);
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final String name = filter.getName() != null ? filter.getName().get(0) : null;
        final Pageable pageWithSort = updateSort(pageable);
        final Page<RoleEntity> roleEntities = roleRepository.findAllByDataCollectionsAndMasterFalse(pageWithSort, dataCollection, name);
        log.info("Get slave roles by dataCollection: {} and filter: {} was finished successfully", dataCollection, filter);
        return roleEntities;
    }

    @Override
    public RoleEntity create(final String name, final List<String> permissions, final Boolean master) {
        log.info("Create role with name: {} and permissions: {} and master: {} was started", name, permissions, master);
        checkSystemRole(name);
        if (roleRepository.countByNameAndMasterTrue(name) > 0) {
            throw new RoleAlreadyExistsException(name);
        }
        final RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(name);
        setPermissions(roleEntity, permissions);
        setDefaultPermissions(roleEntity);
        roleEntity.setType(RoleTypeEnum.CUSTOM);
        roleEntity.setMaster(master);
        final RoleEntity savedRole = roleRepository.save(roleEntity);
        log.info("Create role with name: {} and permissions: {} and master: {} was finished successfully", name, permissions, master);
        return savedRole;
    }

    @Override
    public Page<RoleModel> getRolesByUserSearch(final Pageable pageable, RoleUserFilter filter, final String search) {
        final String email = getStringFromFilter(filter.getEmail());
        final String firstName = getStringFromFilter(filter.getFirstName());
        final String lastName = getStringFromFilter(filter.getLastName());
        final Boolean active = getBooleanMultiselectFromFilter(filter.getActive());
        final String searchRoleName = getStringFromFilter(filter.getRoleName());
        final String searchParam = getStringFromFilter(search);

        return StringUtils.isEmpty(searchParam)
                ? roleRepository.getRolesFilter(pageable, email, firstName, lastName, searchRoleName, active)
                : roleRepository.getRolesSearch(pageable,email, firstName, lastName, active, searchRoleName, searchParam);
    }

    @Override
    public Page<RoleWithUsersModel> list(final Pageable pageable, final RoleFilter roleFilter, final String searchParam) {
        log.info("Get list roles by roleFilter: {} and searchParam: {} was started", roleFilter, searchParam);
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final Pageable pageWithSort = updateSort(pageable);
        final String name = getStringFromFilter(roleFilter.getName());
        final List<RoleTypeEnum> roleTypeEnums = roleFilter.getType();

        final Page<RoleModel> roleModels = StringUtils.isEmpty(searchParam)
                ? roleRepository.filterModel(pageWithSort, name, roleTypeEnums)
                : roleRepository.searchModel(pageWithSort, name, roleTypeEnums, searchParam.toLowerCase());
        final List<Long> listId = roleModels.stream().map(role -> role.getId()).collect(Collectors.toList());
        final List<RoleUsersModel> users = roleRepository.getUsers(listId);
        final List<RolePermissionsModel> permissions = roleRepository.getPermissions(listId);
        final Map<Long, List<RolePermissionsModel>> mapPermissions = permissions.stream().collect(Collectors.groupingBy(RolePermissionsModel::getId));
        final Map<Long, List<RoleUsersModel>> mapUsers = users.stream().collect(Collectors.groupingBy(RoleUsersModel::getId));
        final Page<RoleWithUsersModel> page = initRoleWithUsersModel(roleModels);
        addListUsers(mapUsers, mapPermissions, page);
        log.info("Get list roles by roleFilter: {} and searchParam: {} was finished successfully", roleFilter, searchParam);
        return page;
    }

    private Page<RoleWithUsersModel> initRoleWithUsersModel(final Page<RoleModel> roleModels) {
        return roleModels.map(this::init);
    }

    private RoleWithUsersModel init(final RoleModel roleModel) {
        final RoleWithUsersModel role = new RoleWithUsersModel();
        role.setId(roleModel.getId());
        role.setMaster(roleModel.getMaster());
        role.setName(roleModel.getRoleName());
        role.setType(roleModel.getType());
        return role;
    }

    private Page<RoleWithUsersModel> addListUsers(final Map<Long, List<RoleUsersModel>> mapUsers, final Map<Long, List<RolePermissionsModel>> mapPermissions,
                                                  final Page<RoleWithUsersModel> roleModels) {
        for (final RoleWithUsersModel role : roleModels) {
            final List<String> list = mapUsers.get(role.getId()).stream().map(roleUsersModel -> roleUsersModel.getUserEmail()).collect(Collectors.toList());
            role.setUsersEmails(list);
            getPermissions(mapPermissions, role);
        }
        return roleModels;
    }

    private void getPermissions(final Map<Long, List<RolePermissionsModel>> map, final RoleWithUsersModel roleWithUsersModel) {
        final List<RolePermissionsModel> roles = map.get(roleWithUsersModel.getId());
        for (final RolePermissionsModel rolePermissionsModel : roles) {
            final PermissionDTO permission = new PermissionDTO();
            permission.setName(rolePermissionsModel.getName());
            permission.setOptionalForCustomRole(rolePermissionsModel.getOptionalForCustomRole());
            roleWithUsersModel.getPermissions().add(permission);
        }
    }


    @Override
    public RoleEntity update(final String name, final RoleEntity updatedRole, final List<String> permissions) {
        log.info("Update role by name: {} and new role: {} and new permissions: {} was started", name, updatedRole, permissions);
        RoleEntity existingRole = roleRepository.findByNameAndMasterTrue(name).orElseThrow(() -> new RoleNotFoundException(name));

        if (existingRole.getType() == RoleTypeEnum.SYSTEM) {
            throw new UnableToUpdateSystemRoleException();
        }
        if (!name.equals(updatedRole.getName()) && roleRepository.findByNameAndMasterTrue(updatedRole.getName()).isPresent()) {
            throw new RoleAlreadyExistsException(updatedRole.getName());
        }

        existingRole.setName(updatedRole.getName());
        setPermissions(existingRole, permissions);
        setDefaultPermissions(existingRole);
        final RoleEntity savedRoleEntity = roleRepository.save(existingRole);
        log.info("Update role by name: {} and new role: {} and new permissions: {} was finished successfully", name, updatedRole, permissions);
        return savedRoleEntity;
    }

    @Override
    public void deleteMasterRole(final String name) {
        log.info("Delete master role with name: {} was started", name);
        final RoleEntity masterRole = roleRepository.findByNameAndMasterTrue(name).orElseThrow(() -> new RoleNotFoundException(name));

        if (masterRole.getType() == RoleTypeEnum.SYSTEM) {
            throw new AttemptToDeleteSystemRoleException();
        }
        if (userService.calculateCountOfUsersWithOnlyOneRole(name) > 0) {
            throw new UnableToDeleteSingularRoleException();
        }

        final List<RoleEntity> slaveRoles = roleRepository.findByNameAndMasterFalse(name);
        slaveRoles.add(masterRole);

        roleRepository.deleteAll(slaveRoles);
        log.info("Delete master role with name: {} was finished successfully", name);
    }

    @Override
    public RoleEntity getSlaveRole(final String name, final TemplateEntity templateEntity) {
        return roleRepository.findByNameAndMasterFalseAndTemplates(name, templateEntity);
    }

    @Override
    public Page<RoleEntity> getSlaveRolesByTemplate(final Pageable pageable, final TemplatePermissionFilter filter, final TemplateEntity templateEntity) {
        log.info("Get slave roles by template with filter: {} and template: {} was started", filter, templateEntity);
        //TODO: method is not used in project. Should be it removed later?
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final String name = getStringFromFilter(filter.getName().get(0));
        final Pageable pageWithSort = updateSort(pageable);
        final Page<RoleEntity> roleEntities = roleRepository.findAllByTemplatesAndMasterFalse(pageWithSort, templateEntity, name);
        log.info("Get slave roles by template with filter: {} and template: {} was finished successfully", filter, templateEntity);
        return roleEntities;
    }

    @Override
    public void delete(final RoleEntity roleEntity) {
        roleRepository.delete(roleEntity);
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return roleRepository.SUPPORTED_SORT_FIELDS;
    }

    private RoleEntity setPermissions(final RoleEntity role, List<String> permissionsName) {
        role.getPermissions().clear();
        for (final String permissionName : permissionsName) {
            final PermissionEntity permissionEntity = permissionService.get(permissionName);
            if (permissionEntity == null) {
                throw new PermissionNotFoundException(permissionName);
            } else if (!permissionEntity.getOptionalForCustomRole()) {
                throw new PermissionCantBeAttachedToCustomRoleException(permissionEntity.getName());
            } else {
                role.getPermissions().add(permissionEntity);
            }
        }
        return role;
    }

    private RoleEntity setDefaultPermissions(final RoleEntity role) {
        final List<PermissionEntity> defaultPermissions = permissionService.defaultPermissions();
        role.getPermissions().addAll(defaultPermissions);
        return role;
    }

    /**
     * W/A for sorting roles by number of users (as sort param cannot be changed on FE side).
     *
     * @param pageable from request
     * @return pageable with updated sort params
     */
    private Pageable updateSort(Pageable pageable) {
        Sort newSort;
        if (pageable.getSort().isSorted()) {
            newSort = Sort.by(pageable.getSort().stream()
                    .map(sortParam -> {
                        if ("users".equals(sortParam.getProperty())) {
                            sortParam = new Sort.Order(sortParam.getDirection(), "users.size");
                        }
                        if ("permissions".equals(sortParam.getProperty())) {
                            sortParam = new Sort.Order(sortParam.getDirection(), "permissions.size");
                        }
                        if ("name".equals(sortParam.getProperty())) {
                            sortParam = sortParam.ignoreCase();
                        }
                        if ("type".equals(sortParam.getProperty())) {
                            sortParam = sortParam.ignoreCase();
                        }
                        return sortParam;
                    })
                    .collect(Collectors.toList()));
        } else {
            newSort = Sort.by(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "type").ignoreCase(),
                    new Sort.Order(Sort.Direction.ASC, "name").ignoreCase()));
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    private void checkSystemRole(final String roleName) {
        final String role = roleName.toLowerCase().replaceAll("\\s+", "_");
        if ("administrator".equals(role) ||
                "global_administrator".equals(role) ||
                "template_designer".equals(role)) {
            throw new RoleAlreadyExistsException(role);
        }
    }
}
