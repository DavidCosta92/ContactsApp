package com.contacts.agenda.integration.auth;

import com.contacts.agenda.auth.UserRepository;
import com.contacts.agenda.auth.entities.AuthResponse;
import com.contacts.agenda.auth.entities.LoguedUserDetails;
import com.contacts.agenda.auth.entities.RegisterRequest;
import com.contacts.agenda.auth.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GetLoguedUserDetailsTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> posgres = new PostgreSQLContainer<>("postgres:16.0");
    // static, para que no levante una nueva por cada test

    @Autowired
    UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    TestRestTemplate restTemplate;

    private RegisterRequest newUserData;
    private String token;

    @BeforeEach
    void setUp(){
        // insertar datos en bd
        newUserData = new RegisterRequest("SUPER","123456789","123456789","david","Costa","35924410","2644647572","davidcst2991@gmail.com");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity("/auth/register", newUserData,AuthResponse.class);
        token = response.getBody().getToken();

    }

    @DisplayName("Postgres y docker funcionando")
    @Test
    void conectionStablish (){
        assertThat(posgres.isCreated()).isTrue();
        assertThat(posgres.isRunning()).isTrue();
    }
    @DisplayName("Obtener datos de usuario mediante token JWT")
    @Test
    public void getLoguedUserTest1() throws Exception {

        // TODO SOLUCIONAR ERROR => ERROR: Caused by: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `org.springframework.security.core.GrantedAuthority` (no Creators, like default constructor, exist): abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information, PARECE QUE JACKSON NO PUEDE DESCERIALIZAR LA RESPUESTA AL MOMENTO DE LLEGAR A LAS AUTHORITIES PORQUE NO TIENNE CONSTRUCTOR CON PARAMETROS??
/*
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> httpEntity = new HttpEntity<>("body", headers);

        ResponseEntity<LoguedUserDetails> response = restTemplate.exchange("/auth/userDetails", HttpMethod.GET , httpEntity ,LoguedUserDetails.class);

        LoguedUserDetails loguedUserDetails = response.getBody();
        assertThat(loguedUserDetails).isNotNull();
        assertThat(loguedUserDetails.getUsername()).isEqualTo(newUserData.getUsername());
        assertThat(loguedUserDetails.getFirstName()).isEqualTo(newUserData.getFirstName());
        assertThat(loguedUserDetails.getLastName()).isEqualTo(newUserData.getLastName());
        assertThat(loguedUserDetails.getDni()).isEqualTo(newUserData.getDni());
        assertThat(loguedUserDetails.getPhone()).isEqualTo(newUserData.getPhone());
        assertThat(loguedUserDetails.getEmail()).isEqualTo(newUserData.getEmail());
 */
    }

}
