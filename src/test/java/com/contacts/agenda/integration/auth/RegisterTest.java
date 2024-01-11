package com.contacts.agenda.integration.auth;

import com.contacts.agenda.auth.UserRepository;
import com.contacts.agenda.auth.entities.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RegisterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> posgres = new PostgreSQLContainer<>("postgres:16.0");
    // static, para que no levante una nueva por cada test

    @Autowired
    UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(){
        // insertar datos en bd

    }

    @Test
    void conectionStablish (){
        assertThat(posgres.isCreated()).isTrue();
        assertThat(posgres.isRunning()).isTrue();
    }
    @DisplayName("Registro exitoso de nuevo usuario, con datos correctos!")
    @Test
    public void registerTest1() throws Exception {
        RegisterRequest newUserData = RegisterRequest
                .builder()
                .username("SUPER")
                .password1("123456789")
                .password2("123456789")
                .firstName("david")
                .lastName("Costa")
                .dni("35924410")
                .phone("2644647572")
                .email("davidcst2991@gmail.com")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .content(objectMapper.writeValueAsString(newUserData))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }


    /*
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("Login exitoso, con username y password")
    @Test
    public void loginTest1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .content("{ \"username\" : \"SUPER\" , \"password\" : \"123456789\" }")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

     */
}
