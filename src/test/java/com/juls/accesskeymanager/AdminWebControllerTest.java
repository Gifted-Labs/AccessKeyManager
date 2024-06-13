package com.juls.accesskeymanager;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.juls.accesskeymanager.data.models.*;
import com.juls.accesskeymanager.security.TestSecurityConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;

import com.juls.accesskeymanager.services.AccessKeyService;
import com.juls.accesskeymanager.web.controllers.AdminWebController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebMvcTest(AdminWebController.class)
@ContextConfiguration(classes = {AdminWebController.class, TestSecurityConfig.class})
public class AdminWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessKeyService accessKeyService;

    private List<AccessKeyDetails> keyDetailsList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        keyDetailsList = new ArrayList<>();
        AccessKeyDetails keyDetails1 = new AccessKeyDetails();
        keyDetails1.setEmail("test1@example.com");
        keyDetails1.setKeyValue("key1");
        keyDetails1.setStatus(Status.ACTIVE);

        AccessKeyDetails keyDetails2 = new AccessKeyDetails();
        keyDetails2.setEmail("test2@example.com");
        keyDetails2.setKeyValue("key2");
        keyDetails2.setStatus(Status.REVOKED);

        keyDetailsList.add(keyDetails1);
        keyDetailsList.add(keyDetails2);
    }

    @Test
    void testDashboard() throws Exception {
        when(accessKeyService.getAllAccessKeys()).thenReturn(keyDetailsList);

        mockMvc.perform(get("/web/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-board"))
                .andExpect(model().attributeExists("keys"))
                .andExpect(model().attribute("keys", keyDetailsList));
    }

    @Test
    void testRevokeKey() throws Exception {
        String email = "test1@example.com";
        Users mockUser = new Users();
        mockUser.setEmail(email);
        mockUser.setRole(Role.USER);
        AccessKeys activeKey = new AccessKeys();
        activeKey.setKeyValue("key1");
        activeKey.setStatus(Status.ACTIVE);
        activeKey.setUser(mockUser);

        when(accessKeyService.getActiveKeyByStatus(email)).thenReturn(Optional.of(activeKey));
        when(accessKeyService.revokeKey(email)).thenReturn(activeKey);

        mockMvc.perform(get("/web/admin/revoke").param("email", email))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/dashboard"));

        verify(accessKeyService, times(1)).revokeKey(email);
    }
    @Test
    void testSortKeys() throws Exception {
        when(accessKeyService.sortKeys("email")).thenReturn(keyDetailsList);

        mockMvc.perform(get("/web/admin/sort").param("sortBy", "email"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].email").value("test1@example.com"))
                .andExpect(jsonPath("$[0].keyValue").value("key1"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].email").value("test2@example.com"))
                .andExpect(jsonPath("$[1].keyValue").value("key2"))
                .andExpect(jsonPath("$[1].status").value("REVOKED"));
    }

    @Test
    void testGetActiveKey() throws Exception {
        AccessKeyDetails activeKey = new AccessKeyDetails();
        activeKey.setEmail("test1@example.com");
        activeKey.setKeyValue("key1");
        activeKey.setStatus(Status.ACTIVE);

        when(accessKeyService.getActiveKeyByEmail("test1@example.com")).thenReturn(activeKey);

        mockMvc.perform(get("/web/admin/search").param("search", "test1@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("test1@example.com"))
                .andExpect(jsonPath("$.keyValue").value("key1"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testGetActiveKeyNotFound() throws Exception {
        when(accessKeyService.getActiveKeyByEmail("test1@example.com")).thenReturn(null);

        mockMvc.perform(get("/web/admin/search").param("search", "test1@example.com"))
                .andExpect(status().isNotFound());
    }


}
