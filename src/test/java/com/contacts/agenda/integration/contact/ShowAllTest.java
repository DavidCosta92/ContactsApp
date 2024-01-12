package com.contacts.agenda.integration.contact;

import com.contacts.agenda.auth.UserRepository;
import com.contacts.agenda.auth.entities.AuthResponse;
import com.contacts.agenda.auth.entities.RegisterRequest;
import com.contacts.agenda.model.dtos.contact.ContactArrayReadDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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
public class ShowAllTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> posgres = new PostgreSQLContainer<>("postgres:16.0");
    // static, para que no levante una nueva por cada test

    @Autowired
    TestRestTemplate restTemplate;

    private RegisterRequest newUserData;
    private String token;

    @BeforeEach
    void setUp(){
        newUserData = new RegisterRequest("SUPER","123456789","123456789","david","Costa","35924410","2644647572","dsuper@gmail.com");
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity("/auth/register", newUserData,AuthResponse.class);
        token = response.getBody().getToken();
/*
        newUserData = new RegisterRequest("SUPER","123456789","123456789","david","Costa","35924410","2644647572","super@gmail.com");
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity("/auth/register", newUserData,AuthResponse.class);
        token = response.getBody().getToken();


 */

    }

    @DisplayName("Postgres y docker funcionando")
    @Test
    void conectionStablish (){
        assertThat(posgres.isCreated()).isTrue();
        assertThat(posgres.isRunning()).isTrue();
    }

    @DisplayName("Obtener todos los contactos")
    @Test
    public void showAllTest1() throws Exception {
        ResponseEntity<ContactArrayReadDTO> response = restTemplate.getForEntity("/contacts/", ContactArrayReadDTO.class);
        assertThat(response.getBody().getContacts().size()).isGreaterThanOrEqualTo(0);
        assertThat(response.getBody().getCurrent_page()).isEqualTo(0);
    }

}

