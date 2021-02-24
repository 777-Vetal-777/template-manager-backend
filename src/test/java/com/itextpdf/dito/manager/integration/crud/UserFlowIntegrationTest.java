package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.component.mail.MailClient;
import com.itextpdf.dito.manager.controller.user.UserController;
import com.itextpdf.dito.manager.dto.token.reset.ResetPasswordDTO;
import com.itextpdf.dito.manager.dto.user.EmailDTO;
import com.itextpdf.dito.manager.dto.user.UserDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.unblock.UsersUnblockRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.PasswordChangeRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UpdatePasswordRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UpdateUsersRolesActionEnum;
import com.itextpdf.dito.manager.dto.user.update.UserRolesUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UserUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UsersActivateRequestDTO;
import com.itextpdf.dito.manager.entity.FailedLoginAttemptEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.login.FailedLoginRepository;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.itextpdf.kernel.xmp.impl.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static com.itextpdf.dito.manager.controller.user.UserController.CHANGE_PASSWORD;
import static com.itextpdf.dito.manager.controller.user.UserController.CURRENT_USER;
import static com.itextpdf.dito.manager.controller.user.UserController.CURRENT_USER_CHANGE_PASSWORD_ENDPOINT;
import static com.itextpdf.dito.manager.controller.user.UserController.CURRENT_USER_INFO_ENDPOINT;
import static com.itextpdf.dito.manager.controller.user.UserController.FORGOT_PASSWORD;
import static com.itextpdf.dito.manager.controller.user.UserController.RESET_PASSWORD;
import static com.itextpdf.dito.manager.controller.user.UserController.UPDATE_USERS_ROLES_ENDPOINT;
import static com.itextpdf.dito.manager.controller.user.UserController.USERS_ACTIVATION_ENDPOINT;
import static com.itextpdf.dito.manager.controller.user.UserController.USER_UPDATE_PASSWORD_ENDPOINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserFlowIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FailedLoginRepository failedLoginRepository;

    @Autowired
    private RoleRepository roleRepository;

    @MockBean
    private MailClient mailClient;

    private UserEntity user1;
    private UserEntity user2;
    private String password1;
    private String password2;

    @BeforeEach
    public void setup() {
        password2 = "password2";
        password1 = "password1";
        RoleEntity role = roleRepository.findByNameAndMasterTrue("GLOBAL_ADMINISTRATOR").orElseThrow();
        user1 = new UserEntity();
        user1.setEmail("user1@email.com");
        user1.setFirstName("Harry");
        user1.setLastName("Kane");
        user1.setPassword(password1);
        user1.setRoles(Set.of(role));
        user1.setActive(Boolean.TRUE);
        user1.setPasswordUpdatedByAdmin(Boolean.FALSE);

        user2 = new UserEntity();
        user2.setEmail("user2@email.com");
        user2.setFirstName("Geoffrey");
        user2.setLastName("Grant");
        user2.setPassword("password2");
        user2.setRoles(Set.of(role));
        user2.setActive(Boolean.TRUE);
        user2.setPasswordUpdatedByAdmin(Boolean.FALSE);

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
    }

    @AfterEach
    public void teardown() {
        failedLoginRepository.deleteAll();
        user1.setRoles(Collections.emptySet());
        user2.setRoles(Collections.emptySet());
        userRepository.delete(user1);
        userRepository.delete(user2);
    }

    @Test
    public void testCreateUser() throws Exception {
        UserCreateRequestDTO request = objectMapper
                .readValue(new File("src/test/resources/test-data/users/user-create-request.json"),
                        UserCreateRequestDTO.class);
        mockMvc.perform(post(UserController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("firstName").value("Harry"))
                .andExpect(jsonPath("email").value("user@email.com"))
                .andExpect(jsonPath("lastName").value("Kane"))
                .andExpect(jsonPath("blocked").value("false"))
                .andExpect(jsonPath("authorities").isNotEmpty())
                .andExpect(jsonPath("active").value("true"));

        assertTrue(userRepository.findByEmailAndActiveTrue("user@email.com").isPresent());

        mockMvc.perform(post(UserController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get(UserController.BASE_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deactivateUsers() throws Exception {
        UsersActivateRequestDTO activateRequestDTO = new UsersActivateRequestDTO();
        activateRequestDTO.setActivate(false);
        activateRequestDTO.setEmails(List.of(user1.getEmail(), user2.getEmail()));
        mockMvc.perform(patch(UserController.BASE_NAME + USERS_ACTIVATION_ENDPOINT)
                .content(objectMapper.writeValueAsString(activateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<UserEntity> persisted1 = userRepository.findByEmail(user1.getEmail());
        Optional<UserEntity> persisted2 = userRepository.findByEmail(user2.getEmail());

        assertTrue(persisted1.isPresent());
        assertTrue(persisted2.isPresent());
        assertFalse(persisted1.get().getActive());
        assertFalse(persisted2.get().getActive());
    }

    @Test
    public void activateUsers() throws Exception {
        user1.setActive(Boolean.FALSE);
        user1.setPasswordUpdatedByAdmin(false);
        user2.setActive(Boolean.FALSE);
        user2.setPasswordUpdatedByAdmin(false);

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        UsersActivateRequestDTO activateRequestDTO = new UsersActivateRequestDTO();
        activateRequestDTO.setEmails(List.of(user1.getEmail(), user2.getEmail()));
        activateRequestDTO.setActivate(true);
        mockMvc.perform(patch(UserController.BASE_NAME + USERS_ACTIVATION_ENDPOINT)
                .content(objectMapper.writeValueAsString(activateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<UserEntity> persisted1 = userRepository.findByEmail(user1.getEmail());
        Optional<UserEntity> persisted2 = userRepository.findByEmail(user2.getEmail());

        assertTrue(persisted1.isPresent());
        assertTrue(persisted2.isPresent());
        assertTrue(persisted1.get().getActive());
        assertTrue(persisted2.get().getActive());
    }

    @Test
    public void deactivateUsersWhenUserNotFound() throws Exception {
        UsersActivateRequestDTO deleteRequest = new UsersActivateRequestDTO();
        deleteRequest.setEmails(List.of("unknown@email.com"));
        mockMvc.perform(patch(UserController.BASE_NAME + USERS_ACTIVATION_ENDPOINT)
                .content(objectMapper.writeValueAsString(deleteRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void currentUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(UserController.BASE_NAME + CURRENT_USER_INFO_ENDPOINT)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDTO result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDTO.class);
        assertNotNull(result);
    }

    @Test
    void updateCurrentUser() throws Exception {
        UserUpdateRequestDTO request = objectMapper
                .readValue(new File("src/test/resources/test-data/users/user-update-request.json"),
                        UserUpdateRequestDTO.class);
        MvcResult mvcResult = mockMvc.perform(patch(UserController.BASE_NAME + CURRENT_USER)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDTO.class);

        assertEquals(request.getFirstName(), response.getFirstName());
        assertEquals(request.getLastName(), response.getLastName());

        //to revert default admin name and not affect other tests
        request = objectMapper
                .readValue(new File("src/test/resources/test-data/users/user-update-restore-default-request.json"),
                        UserUpdateRequestDTO.class);
        mvcResult = mockMvc.perform(patch(UserController.BASE_NAME + CURRENT_USER)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDTO.class);

        assertEquals(request.getFirstName(), response.getFirstName());
        assertEquals(request.getLastName(), response.getLastName());
    }

    @Test
    public void testUnblockUser() throws Exception {
        user1.setLocked(Boolean.TRUE);

        FailedLoginAttemptEntity failedLoginAttemptEntity = new FailedLoginAttemptEntity();
        failedLoginAttemptEntity.setUser(user1);
        failedLoginAttemptEntity.setVersion(new Date());
        user1.setPasswordUpdatedByAdmin(false);
        userRepository.save(user1);
        failedLoginRepository.save(failedLoginAttemptEntity);

        UsersUnblockRequestDTO request = objectMapper
                .readValue(new File("src/test/resources/test-data/users/user-unblock-request.json"),
                        UsersUnblockRequestDTO.class);
        mockMvc.perform(patch(UserController.BASE_NAME + UserController.USERS_UNBLOCK_ENDPOINT)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].blocked").value("false"));
        UserEntity user = userRepository.findByEmail(user1.getEmail()).orElseThrow();
        assertTrue(failedLoginRepository.findByUser(user1).isEmpty());
        assertFalse(user.getLocked());
    }

    @Test
    public void updateUserRoles_AddAndRemoveRole() throws Exception {
        UserRolesUpdateRequestDTO request = new UserRolesUpdateRequestDTO();
        request.setEmails(List.of(user1.getEmail()));
        request.setRoles(List.of("ADMINISTRATOR"));
        request.setUpdateUsersRolesActionEnum(UpdateUsersRolesActionEnum.ADD);

        mockMvc.perform(patch(UserController.BASE_NAME + UPDATE_USERS_ROLES_ENDPOINT)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        UserEntity updated = userRepository.findByEmailAndActiveTrue(user1.getEmail()).orElseThrow();
        assertEquals(2, updated.getRoles().size());

        request.setUpdateUsersRolesActionEnum(UpdateUsersRolesActionEnum.REMOVE);
        mockMvc.perform(patch(UserController.BASE_NAME + UPDATE_USERS_ROLES_ENDPOINT)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        updated = userRepository.findByEmailAndActiveTrue(user1.getEmail()).orElseThrow();
        assertEquals(1, updated.getRoles().size());
    }

    @Test
    public void shouldResetPasswordByForgetPasswordFunction() throws Exception {
        final String newPassword = "SpecialNewPassword123!";

        final EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail(user2.getEmail());
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<UserEntity> captor2 = ArgumentCaptor.forClass(UserEntity.class);
        doNothing().when(mailClient).sendResetMessage(captor2.capture(), captor.capture());
        mockMvc.perform(patch(UserController.BASE_NAME + FORGOT_PASSWORD)
                .content(objectMapper.writeValueAsString(emailDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(mailClient, times(1)).sendResetMessage(captor2.capture(), captor.capture());
        assertNotNull(captor2.getValue());

        final UserEntity userEntity = userRepository.findByEmail(user2.getEmail()).get();
        assertNotNull(userEntity.getResetPasswordTokenDate());
        assertEquals(password2, userEntity.getPassword());

        final ResetPasswordDTO request = new ResetPasswordDTO();
        request.setPassword(newPassword);
        request.setToken(captor.getValue());

        mockMvc.perform(patch(UserController.BASE_NAME + RESET_PASSWORD)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        final Optional<UserEntity> userAfterUpdatePassword = userRepository.findByEmail(user2.getEmail());
        assertTrue(userAfterUpdatePassword.isPresent());
        final UserEntity userWhichUpdatedPasswordEntity = userAfterUpdatePassword.get();
        assertFalse(userWhichUpdatedPasswordEntity.getPassword().equals(password2));
    }

    @Test
    public void updatePassword() throws Exception {
        final UpdatePasswordRequestDTO updatePasswordRequestDTO = new UpdatePasswordRequestDTO();
        updatePasswordRequestDTO.setPassword("NewPassword12345");
        user1.setPasswordUpdatedByAdmin(false);
        mockMvc.perform(patch(UserController.BASE_NAME + "/" + Base64.encode("user1@email.com") + CHANGE_PASSWORD)
                .content(objectMapper.writeValueAsString(updatePasswordRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("passwordUpdatedByAdmin").value(true));
    }

    @Test
    public void updateUser() throws Exception {
        final UserUpdateRequestDTO updatePasswordRequestDTO = new UserUpdateRequestDTO();
        updatePasswordRequestDTO.setFirstName("newFirstName");
        updatePasswordRequestDTO.setLastName("newLastName");
        user1.setPasswordUpdatedByAdmin(true);
        mockMvc.perform(patch(UserController.BASE_NAME + "/" + Base64.encode("user1@email.com"))
                .content(objectMapper.writeValueAsString(updatePasswordRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("passwordUpdatedByAdmin").value(false))
                .andExpect(jsonPath("firstName").value("newFirstName"))
                .andExpect(jsonPath("lastName").value("newLastName"));
    }

    @Test
    public void getUser() throws Exception {
        mockMvc.perform(get(UserController.BASE_NAME + "/" + Base64.encode("user1@email.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("passwordUpdatedByAdmin").value(false))
                .andExpect(jsonPath("firstName").value("Harry"))
                .andExpect(jsonPath("lastName").value("Kane"));
    }

    @Test
    public void shouldDropInvalidPasswordExceptionWhenUpdatePasswordWithWrongOldPassword() throws Exception {
        final PasswordChangeRequestDTO passwordChangeRequestDTO = new PasswordChangeRequestDTO();
        passwordChangeRequestDTO.setOldPassword("WRONG_OLD_PASSWORD");
        passwordChangeRequestDTO.setNewPassword("NEW_PASSWORD");
        mockMvc.perform(patch(UserController.BASE_NAME + CURRENT_USER_CHANGE_PASSWORD_ENDPOINT)
                .content(objectMapper.writeValueAsString(passwordChangeRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldThrowNoSuchUserException() throws Exception {
        mockMvc.perform(get(UserController.BASE_NAME + "/" + Base64.encode("bad_user@email.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldThrowPasswordIsNotSpecifiedByAdmin() throws Exception {
        final UpdatePasswordRequestDTO updatePasswordRequestDTO = new UpdatePasswordRequestDTO();
        updatePasswordRequestDTO.setPassword("test");
        mockMvc.perform(patch(UserController.BASE_NAME + USER_UPDATE_PASSWORD_ENDPOINT)
                .content(objectMapper.writeValueAsString(updatePasswordRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldThrowInvalidResetPasswordTokenException() throws Exception {
        final String newPassword = "SpecialNewPassword123!";

        final EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail(user2.getEmail());

        doNothing().when(mailClient).sendResetMessage(any(), any());
        mockMvc.perform(patch(UserController.BASE_NAME + FORGOT_PASSWORD)
                .content(objectMapper.writeValueAsString(emailDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        final UserEntity userEntity = userRepository.findByEmail(user2.getEmail()).get();
        assertNotNull(userEntity.getResetPasswordTokenDate());
        assertEquals(password2, userEntity.getPassword());

        final ResetPasswordDTO request = new ResetPasswordDTO();
        request.setPassword(newPassword);
        request.setToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcm8xMDB2ZXRhbDA3MDMwNDAxQGdtYWlsLmNvbSIsInR5cGUiOiJyZXNldFBhc3N3b3JkIiwiZXhwIjoxNjE0MjUxMDU4LCJpYXQiOjE2MTQxNjQ2NTh9.-EUUeyz3i9oSQSBT2RKOXFkFKJVTb1s458RxoAXl4CpVILB7ehXsxiD6bMrguGMZSCxZ0L-W-nk_PNE_sZFFvg");

        mockMvc.perform(patch(UserController.BASE_NAME + RESET_PASSWORD)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}