package com.itextpdf.dito.manager.integration.security;

import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.RoleTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.resource.ForbiddenOperationException;
import com.itextpdf.dito.manager.exception.role.RoleAlreadyExistsException;
import com.itextpdf.dito.manager.exception.user.UserAlreadyExistsException;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.permission.PermissionService;
import com.itextpdf.dito.manager.service.role.RoleService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class EntityRolesUnitTest extends AbstractService {

    private static final String CUSTOM_ROLE = "custom_role";
    private static final String CUSTOM_PERMISSION = "E6_US34_EDIT_DATA_COLLECTION_METADATA";
    private static final String CUSTOM_USER_EMAIL = "user1@email.com";

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void init() {
        final List<String> permissions = Collections.singletonList(CUSTOM_PERMISSION);
        RoleEntity masterRole;
        UserEntity user;

        try {
            masterRole = roleService.create(CUSTOM_ROLE, permissions, true);
        } catch (RoleAlreadyExistsException e) {
            masterRole = roleService.getMasterRole(CUSTOM_ROLE);
        }
        try {

        user = new UserEntity();
        user.setEmail(CUSTOM_USER_EMAIL);
        user.setFirstName("Barry");
        user.setLastName("Lane");
        user.setPassword("password1");
        user.setRoles(Set.of(masterRole));
        user.setActive(Boolean.TRUE);

        userService.create(user, Collections.singletonList(masterRole.getName()));
        } catch (UserAlreadyExistsException e) {
            user = userService.findByEmail(CUSTOM_USER_EMAIL);
            user.setRoles(Set.of(masterRole));
            userService.updateUser(user, CUSTOM_USER_EMAIL);
        }
    }

    @AfterEach
    void destroy() {
        userRepository.delete(userService.findByEmail(CUSTOM_USER_EMAIL));
        roleService.delete(roleService.getMasterRole(CUSTOM_ROLE));
    }

    @Test
    void testPermissionCustomEntityWithCustomRole() {
        final RoleEntity masterRole = roleService.getMasterRole(CUSTOM_ROLE);
        final Set<PermissionEntity> permissions = Collections.singleton(permissionService.get(CUSTOM_PERMISSION));

        RoleEntity slaveRole = new RoleEntity();
        slaveRole.setMaster(false);
        slaveRole.setName(CUSTOM_ROLE);
        slaveRole.setPermissions(permissions);
        slaveRole.setType(RoleTypeEnum.CUSTOM);
        slaveRole = roleRepository.save(slaveRole);

        roleService.update(CUSTOM_ROLE, masterRole, Collections.singletonList(CUSTOM_PERMISSION));
        UserEntity checkingUser = userRepository.findByEmail(CUSTOM_USER_EMAIL).get();

        //we have no real entity for this check, so we use Collections.singleton as parameter for retrieveEntityAppliedRoles
        checkUserPermissions(retrieveSetOfRoleNames(checkingUser.getRoles()),
                retrieveEntityAppliedRoles(Collections.singleton(slaveRole), checkingUser.getRoles()), CUSTOM_PERMISSION);

        roleService.update(CUSTOM_ROLE, masterRole, Collections.emptyList());
        checkingUser = userRepository.findByEmail(CUSTOM_USER_EMAIL).get();

        checkUserPermissions(retrieveSetOfRoleNames(checkingUser.getRoles()),
                retrieveEntityAppliedRoles(Collections.singleton(slaveRole), checkingUser.getRoles()), CUSTOM_PERMISSION);

        roleService.delete(slaveRole);
    }

    @Test
    void testPermissionCustomEntityWithoutCustomRole() {
        final UserEntity checkingUser = userRepository.findByEmail(CUSTOM_USER_EMAIL).get();
        checkUserPermissions(retrieveSetOfRoleNames(checkingUser.getRoles()),
                retrieveEntityAppliedRoles(Collections.emptySet(), checkingUser.getRoles()), CUSTOM_PERMISSION);
    }

    @Test
    void testPermissionCustomEntityWithCustomRoleNoPermission() {
        final RoleEntity masterRole = roleService.getMasterRole(CUSTOM_ROLE);

        final RoleEntity slaveRole = new RoleEntity();
        slaveRole.setMaster(false);
        slaveRole.setName(CUSTOM_ROLE);
        slaveRole.setPermissions(Collections.emptySet());
        slaveRole.setType(RoleTypeEnum.CUSTOM);
        roleRepository.save(slaveRole);

        roleService.update(CUSTOM_ROLE, masterRole, Collections.singletonList(CUSTOM_PERMISSION));
        final UserEntity checkingUser = userRepository.findByEmail(CUSTOM_USER_EMAIL).get();

        Assertions.assertThrows(ForbiddenOperationException.class,
                () -> checkUserPermissions(retrieveSetOfRoleNames(checkingUser.getRoles()),
                retrieveEntityAppliedRoles(Collections.singleton(slaveRole), checkingUser.getRoles()), CUSTOM_PERMISSION));

        roleService.update(CUSTOM_ROLE, masterRole, Collections.emptyList());
        final UserEntity checkingUserWithUpdatedRole = userRepository.findByEmail(CUSTOM_USER_EMAIL).get();

        Assertions.assertThrows(ForbiddenOperationException.class,
                () -> checkUserPermissions(retrieveSetOfRoleNames(checkingUserWithUpdatedRole.getRoles()),
                        retrieveEntityAppliedRoles(Collections.singleton(slaveRole), checkingUserWithUpdatedRole.getRoles()), CUSTOM_PERMISSION));
        roleService.delete(slaveRole);
    }

    @Test
    void testPermissionCustomEntityWithoutCustomRoleNoPermission() {
        final RoleEntity masterRole = roleService.getMasterRole(CUSTOM_ROLE);
        roleService.update(CUSTOM_ROLE, masterRole, Collections.emptyList());
        final UserEntity checkingUser = userRepository.findByEmail(CUSTOM_USER_EMAIL).get();

        Assertions.assertThrows(ForbiddenOperationException.class,
                () -> checkUserPermissions(retrieveSetOfRoleNames(checkingUser.getRoles()),
                retrieveEntityAppliedRoles(Collections.emptySet(), checkingUser.getRoles()), CUSTOM_PERMISSION));

    }

    @Override
    protected List<String> getSupportedSortFields() {
        return null;
    }
}

