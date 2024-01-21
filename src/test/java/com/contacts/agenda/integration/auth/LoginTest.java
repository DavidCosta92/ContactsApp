package com.contacts.agenda.integration.auth;

import com.contacts.agenda.auth.AuthService;
import com.contacts.agenda.auth.entities.*;
import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LoginTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> posgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    private AuthService authService;

    private RegisterRequest newAdminUserData;
    private String adminToken;
    User admin = User.builder()
            .username("SUPER")
            .password("123456789")
            .firstName("david")
            .lastName("Costa")
            .phone("2644647572")
            .dni("35924410")
            .email("super@gmail.com")
            .role(Role.USER)
            .build();
    @BeforeEach
    void setUp(){
        // CREAR NUEVO SUPER USER, SINO OBTENER EL QUE YA ESTA EN BD
            try {
            authService.validateNewEmail(admin.getEmail());
            newAdminUserData = new RegisterRequest(admin.getUsername(),admin.getPassword(),admin.getPassword(),admin.getFirstName(),admin.getLastName(),admin.getDni(),admin.getPhone(),admin.getEmail());
            ResponseEntity<AuthResponse> responseAuthAdmin = restTemplate.postForEntity("/auth/register", newAdminUserData,AuthResponse.class);
            adminToken = responseAuthAdmin.getBody().getToken();
        } catch (Exception e){
            LoginRequest loginRequest = new LoginRequest(admin.getUsername() , admin.getPassword());
            adminToken = authService.login(loginRequest).getToken();
        }
    }
    @DisplayName("Postgres y docker funcionando")
    @Test
    void conectionStablish (){
        assertThat(posgres.isCreated()).isTrue();
        assertThat(posgres.isRunning()).isTrue();
    }

    @DisplayName("Login exitoso, con username y password")
    @Test
    public void loginTest1() throws Exception {
        LoginRequest newUserData = new LoginRequest("SUPER","123456789");
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity("/auth/login", newUserData,AuthResponse.class);
        assertThat(response.getBody().getToken()).isEqualTo(adminToken);
        assertThat(response.getStatusCode().isSameCodeAs(HttpStatus.OK)).isTrue();
    }
    @DisplayName("Login erroneo, con username erroneo y password correcto")
    @Test
    public void loginTest2() throws Exception {
        LoginRequest newUserData = new LoginRequest("SUPERRRR","123456789");
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity("/auth/login", newUserData,AuthResponse.class);
        assertThat(response.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody().getToken()).isNull();
    }

    @DisplayName("Login erroneo, con username correcto y password erroneo")
    @Test
    public void loginTest3() throws Exception {
        LoginRequest newUserData = new LoginRequest("SUPER","99999999");
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity("/auth/login", newUserData,AuthResponse.class);
        assertThat(response.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody().getToken()).isNull();
    }
}
