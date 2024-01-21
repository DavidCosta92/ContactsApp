package com.contacts.agenda.integration.auth;

import com.contacts.agenda.auth.AuthService;
import com.contacts.agenda.auth.entities.RestorePassRequest;
import com.contacts.agenda.exceptions.customsExceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WebAppConfiguration
class RestorePasswordTest {
    @Autowired
    private AuthService authService;

    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext applicationContext;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
    }

    @DisplayName("Solicitud de restauracion de password enviando como email correcto")
    @Test
    void sendEmail1() throws Exception {
        MvcResult response = mockMvc.perform( MockMvcRequestBuilders.get("/auth/restorePassword")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("email", "davidcst2991@gmail.com")
        ).andReturn();
        assertEquals(202 , response.getResponse().getStatus());
    }

    @DisplayName("Solicitud de restauracion de password enviando como parametro un email erroneo")
    @Test
    void sendEmail2() throws Exception {
        MvcResult response = mockMvc.perform( MockMvcRequestBuilders.get("/auth/restorePassword")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("email", "erroneo@gmail.com")
        ).andReturn();
        assertEquals(404 , response.getResponse().getStatus());
    }
    @DisplayName("Solicitud de restauracion de password sin enviar parametro email")
    @Test
    void sendEmail3() throws Exception {
        MvcResult response = mockMvc.perform( MockMvcRequestBuilders.get("/auth/restorePassword")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andReturn();
        assertEquals(400, response.getResponse().getStatus());
    }
    @DisplayName("Test unitario de metodo restorePassword, con email correcto")
    @Test
    void restorePassword1() throws Exception {
        assertEquals("Se envio un email con mas instrucciones", authService.restorePassword("davidcst2991@gmail.com"));
    }
    @Test
    void RestorePassRequest() {
        RestorePassRequest restorePassRequest = new RestorePassRequest();
        restorePassRequest.setPassword1("123456789");
        restorePassRequest.setPassword2("123456789");
        restorePassRequest.setToken("soy el token");

        assertEquals("123456789", restorePassRequest.getPassword1());
        assertEquals("123456789", restorePassRequest.getPassword2());
        assertEquals("soy el token", restorePassRequest.getToken());
    }
}