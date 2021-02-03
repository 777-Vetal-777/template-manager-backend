package com.itextpdf.dito.manager.component.security.impl;

import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.RoleTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.permission.PermissionService;
import com.itextpdf.dito.manager.service.role.RoleService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("integration-test")
public class EntityRolesUnitTest {

    private static final String CUSTOM_ROLE = "custom_role";
    private static final String CUSTOM_PERMISSION = "E6_US34_EDIT_DATA_COLLECTION_METADATA";
    private static final String CUSTOM_USER_EMAIL = "user1@email.com";

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PermissionHandlerImpl permissionHandler;

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

        masterRole = roleService.create(CUSTOM_ROLE, permissions, true);

        user = new UserEntity();
        user.setEmail(CUSTOM_USER_EMAIL);
        user.setFirstName("Barry");
        user.setLastName("Lane");
        user.setPassword("password1");
        user.setRoles(Set.of(masterRole));
        user.setActive(Boolean.TRUE);

        userService.create(user, Collections.singletonList(masterRole.getName()), user);
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
        assertTrue(permissionHandler.checkUserPermissions(checkingUser, Collections.singleton(slaveRole), CUSTOM_PERMISSION));

        roleService.update(CUSTOM_ROLE, masterRole, Collections.emptyList());
        checkingUser = userRepository.findByEmail(CUSTOM_USER_EMAIL).get();

        assertTrue(permissionHandler.checkUserPermissions(checkingUser, Collections.singleton(slaveRole), CUSTOM_PERMISSION));

        roleService.delete(slaveRole);
    }

    @Test
    void testPermissionCustomEntityWithoutCustomRole() {
        final UserEntity checkingUser = userRepository.findByEmail(CUSTOM_USER_EMAIL).get();
        assertTrue(permissionHandler.checkUserPermissions(checkingUser, Collections.emptySet(), CUSTOM_PERMISSION));
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

        assertFalse(permissionHandler.checkUserPermissions(checkingUser, Collections.singleton(slaveRole), CUSTOM_PERMISSION));

        roleService.update(CUSTOM_ROLE, masterRole, Collections.emptyList());
        final UserEntity checkingUserWithUpdatedRole = userRepository.findByEmail(CUSTOM_USER_EMAIL).get();

        assertFalse(permissionHandler.checkUserPermissions(checkingUserWithUpdatedRole, Collections.singleton(slaveRole), CUSTOM_PERMISSION));
        roleService.delete(slaveRole);
    }

    @Test
    void testPermissionCustomEntityWithoutCustomRoleNoPermission() {
        final RoleEntity masterRole = roleService.getMasterRole(CUSTOM_ROLE);
        roleService.update(CUSTOM_ROLE, masterRole, Collections.emptyList());
        final UserEntity checkingUser = userRepository.findByEmail(CUSTOM_USER_EMAIL).get();

        assertFalse(permissionHandler.checkUserPermissions(checkingUser, Collections.emptySet(), CUSTOM_PERMISSION));

    }

}

