package com.juls.accesskeymanager;


import com.juls.accesskeymanager.security.TestSecurityConfig;
import com.juls.accesskeymanager.services.AccessKeyService;
import com.juls.accesskeymanager.services.UserServiceImpl;
import com.juls.accesskeymanager.web.controllers.WebController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.AuthenticationManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@RunWith(SpringRunner.class)
@WebMvcTest(WebController.class)
@ContextConfiguration(classes = {WebController.class})
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private AccessKeyService accessKeyService;

    @MockBean
    private ApplicationEventPublisher publisher;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    public void testGetLoginPage() throws Exception {
        mockMvc.perform(get("/public/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void testRegisterPage() throws Exception {
        mockMvc.perform(get("/public/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }



    @Test
    public void testResetPassword() throws Exception {
        mockMvc.perform(get("/public/reset"))
                .andExpect(status().isOk())
                .andExpect(view().name("resetpassword"));
    }

    @Test
    public void testProcessResetPassword() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(post("/public/reset")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-success"))
                .andExpect(model().attribute("message", "Verification has been resent to your email successfully"));

    }

    @Test
    public void testRegistrationSuccess() throws Exception {
        mockMvc.perform(get("/public/registration-success"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-success"));
    }


}
