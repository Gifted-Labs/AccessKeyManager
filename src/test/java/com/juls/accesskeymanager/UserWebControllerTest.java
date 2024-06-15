package com.juls.accesskeymanager;


import com.juls.accesskeymanager.data.models.AccessKeyDetails;
import com.juls.accesskeymanager.data.models.Status;
import com.juls.accesskeymanager.services.AccessKeyService;
import com.juls.accesskeymanager.web.controllers.UserWebController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserWebControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccessKeyService accessKeyService;

    @InjectMocks
    private UserWebController userWebController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userWebController).build();
    }

    @Test
    public void testAllKeys_Success() throws Exception {
        String userEmail = "test@example.com";
        List<AccessKeyDetails> mockKeys = new ArrayList<>();
        AccessKeyDetails keyDetails1 = new AccessKeyDetails();
        keyDetails1.setKeyValue("key1");
        keyDetails1.setProcured_date(Date.valueOf("2024-06-01"));
        keyDetails1.setExpiry_date(Date.valueOf("2024-07-01"));
        keyDetails1.setStatus(Status.ACTIVE);
        mockKeys.add(keyDetails1);
        AccessKeyDetails keyDetails2 = new AccessKeyDetails();
        keyDetails2.setKeyValue("key2");
        keyDetails2.setProcured_date(Date.valueOf("2024-06-02"));
        keyDetails2.setExpiry_date(Date.valueOf("2024-07-02"));
        keyDetails2.setStatus(Status.EXPIRED);
        mockKeys.add(keyDetails2);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, "password");

        when(accessKeyService.getAllKeysByEmail(userEmail)).thenReturn(mockKeys);

        mockMvc.perform(get("/web/users").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("user-dashboard"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attribute("email", userEmail))
                .andExpect(model().attributeExists("keys"))
                .andExpect(model().attribute("keys", mockKeys));
    }

    @Test
    public void testAllKeys_Exception() throws Exception {
        String userEmail = "test@example.com";
        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, "password");
        String errorMessage = "Failed to fetch keys";

        when(accessKeyService.getAllKeysByEmail(userEmail)).thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(get("/web/users").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", errorMessage));
    }

    @Test
    public void testGenerate_Success() throws Exception {
        String userEmail = "test@example.com";
        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, "password");

        mockMvc.perform(post("/web/users/generate").principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/users"));

        verify(accessKeyService, times(1)).generateKey(userEmail);
    }

    @Test
    public void testGenerate_Exception() throws Exception {
        String userEmail = "test@example.com";
        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, "password");
        String errorMessage = "Failed to generate key";

        doThrow(new RuntimeException(errorMessage)).when(accessKeyService).generateKey(userEmail);

        mockMvc.perform(post("/web/users/generate").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", errorMessage));
    }
}

