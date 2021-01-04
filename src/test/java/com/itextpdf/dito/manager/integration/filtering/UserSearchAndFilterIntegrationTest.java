package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.user.UserController;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.login.FailedLoginRepository;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for filtering and search in {@link UserEntity} table.
 */
public class UserSearchAndFilterIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FailedLoginRepository failedLoginRepository;

    @Autowired
    private RoleRepository roleRepository;

    private UserEntity user1;
    private UserEntity user2;

    @BeforeEach
    public void setup() {
        RoleEntity role = roleRepository.findByNameAndMasterTrue("GLOBAL_ADMINISTRATOR").orElseThrow();
        RoleEntity role2 = roleRepository.findByNameAndMasterTrue("TEMPLATE_DESIGNER").orElseThrow();
        user1 = new UserEntity();
        user1.setEmail("user1@email.com");
        user1.setFirstName("Barry");
        user1.setLastName("Lane");
        user1.setPassword("password1");
        user1.setRoles(Set.of(role));
        user1.setActive(Boolean.TRUE);

        user2 = new UserEntity();
        user2.setEmail("user2@email.com");
        user2.setFirstName("Geoffrey");
        user2.setLastName("Grant");
        user2.setPassword("password2");
        user2.setRoles(Set.of(role2));
        user2.setActive(Boolean.FALSE);

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
    public void getAll_WhenUnsupportedSortField_ThenResponseIsBadRequest() throws Exception {
        mockMvc.perform(get(UserController.BASE_NAME)
                .param("sort", "unknownField")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Override
    @Test
    public void test_filtering() throws Exception {
        mockMvc.perform(get(UserController.BASE_NAME)
                .param("email", user1.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email", is(user1.getEmail())));
        mockMvc.perform(get(UserController.BASE_NAME)
                .param("firstName", user1.getFirstName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email", is(user1.getEmail())));
        mockMvc.perform(get(UserController.BASE_NAME)
                .param("lastName", user1.getLastName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email", is(user1.getEmail())));
    }

    @Override
    @Test
    public void test_searchAndFiltering() throws Exception {
        mockMvc.perform(get(UserController.BASE_NAME)
                .param("search", user1.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email", is(user1.getEmail())));

        mockMvc.perform(get(UserController.BASE_NAME)
                .param("search", "StringThatDoesntMatchAnything")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Override
    @Test
    public void test_sortWithSearch() throws Exception {
        for (String field : UserRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(UserController.BASE_NAME)
                    .param("sort", field)
                    .param("search", "test")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Override
    @Test
    public void test_sortWithFiltering() throws Exception {
        for (String field : UserRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(UserController.BASE_NAME)
                    .param("sort", field)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }
}
