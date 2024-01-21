package com.contacts.agenda.integration.auth;

import com.contacts.agenda.auth.UserRepository;
import com.contacts.agenda.auth.entities.AuthResponse;
import com.contacts.agenda.auth.entities.RegisterRequest;
import com.contacts.agenda.exceptions.ExceptionMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
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
public class RegisterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> posgres = new PostgreSQLContainer<>("postgres:16.0");
    // static, para que no levante una nueva por cada test

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void setUp(){
        // insertar datos en bd

    }

    @DisplayName("Postgres y docker funcionando")
    @Test
    void conectionStablish (){
        assertThat(posgres.isCreated()).isTrue();
        assertThat(posgres.isRunning()).isTrue();
    }
    @DisplayName("Registro exitoso de nuevo usuario, con datos correctos!")
    @Test
    public void registerTest1() throws Exception {
        RegisterRequest newUserData = new RegisterRequest("SUPER","123456789","123456789","david","Costa","35924410","2644647572","davidcst2991@gmail.com");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity("/auth/register", newUserData,AuthResponse.class);
        assertThat(response.getBody().getToken()).isNotNull();
        /*
       MockMvc=> No se utilizan conexiones de red reales, ya que MockMvc envuelve un TestDispatcherServlet internamente. Por lo tanto, no probamos toda la pila de red mientras usamos MockMvc. Por esta razon es recomendable usar => TestRestTemplate <=
         */
    }
    @DisplayName("Registro erroneo de nuevo usuario, envio de username existente!")
    @Test
    public void registerTest2() throws Exception {
        RegisterRequest newUserData = new RegisterRequest("SUPER","123456789","123456789","david","Costa","35924410","2644647572","davidcst2991@gmail.com");
        restTemplate.postForEntity("/auth/register", newUserData,AuthResponse.class);

        RegisterRequest newUserData2 = new RegisterRequest("SUPER","123456789","123456789","david2","Costa2","35924412","2644647571","davidcst2992@gmail.com");
        ResponseEntity<ExceptionMessages> response2 = restTemplate.postForEntity("/auth/register", newUserData2, ExceptionMessages.class);
        assertThat(response2.getBody().getInternalCode()).isEqualTo(2);
        assertThat(response2.getBody().getMessage()).isEqualTo("Username ya en uso!");
    }
    @DisplayName("Registro erroneo de nuevo usuario, envio de dni existente!")
    @Test
    public void registerTest3() throws Exception {
        RegisterRequest newUserData = new RegisterRequest("SUPER","123456789","123456789","david","Costa","35924410","2644647572","davidcst2991@gmail.com");
        restTemplate.postForEntity("/auth/register", newUserData,AuthResponse.class);

        RegisterRequest newUserData2 = new RegisterRequest("SUPER2","123456789","123456789","david","Costa","35924410","2644647572","davidcst2992@gmail.com");
        ResponseEntity<ExceptionMessages> response2 = restTemplate.postForEntity("/auth/register", newUserData2, ExceptionMessages.class);
        assertThat(response2.getBody().getInternalCode()).isEqualTo(2);
        assertThat(response2.getBody().getMessage()).isEqualTo("Dni ya en uso!");
    }
    @DisplayName("Registro erroneo de nuevo usuario, envio de email existente!")
    @Test
    public void registerTest4() throws Exception {
        RegisterRequest newUserData = new RegisterRequest("SUPER","123456789","123456789","david","Costa","35924410","2644647572","davidcst2991@gmail.com");
        restTemplate.postForEntity("/auth/register", newUserData,AuthResponse.class);

        RegisterRequest newUserData2 = new RegisterRequest("SUPER2","123456789","123456789","david","Costa","35924412","2644647572","davidcst2991@gmail.com");
        ResponseEntity<ExceptionMessages> response2 = restTemplate.postForEntity("/auth/register", newUserData2, ExceptionMessages.class);
        assertThat(response2.getBody().getInternalCode()).isEqualTo(2);
        assertThat(response2.getBody().getMessage()).isEqualTo("Email ya en uso!");
    }

}
