package com.contacts.agenda.integration.contact;

import com.contacts.agenda.auth.UserRepository;
import com.contacts.agenda.auth.entities.AuthResponse;
import com.contacts.agenda.auth.entities.RegisterRequest;
import com.contacts.agenda.exceptions.ExceptionMessages;
import com.contacts.agenda.model.dtos.address.AddressAddDto;
import com.contacts.agenda.model.dtos.contact.ContactAddDTO;
import com.contacts.agenda.model.dtos.contact.ContactArrayReadDTO;
import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import com.contacts.agenda.model.entities.AddressEntity;
import com.contacts.agenda.model.mappers.AddressMapper;
import com.contacts.agenda.model.mappers.ContactMapper;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class ShowAllTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> posgres = new PostgreSQLContainer<>("postgres:16.0");
    // static, para que no levante una nueva por cada test
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private ContactMapper contactMapper;

    @DisplayName("Postgres y docker funcionando")
    @Test
    void conectionStablish (){
        assertThat(posgres.isCreated()).isTrue();
        assertThat(posgres.isRunning()).isTrue();
    }

    @DisplayName("Obtener todos los contactos, sin enviar auth")
    @Test
    @Rollback(value = true)
    public void showAllTest1() throws Exception {
        // CREAR SUPER USUARIO
        RegisterRequest newUserDataAdmin = new RegisterRequest("SUPER","123456789","123456789","david","Costa","35924410","2644647572","super@gmail.com");
        ResponseEntity<AuthResponse> responseAuthAdmin = restTemplate.postForEntity("/auth/register", newUserDataAdmin,AuthResponse.class);
        String adminToken = responseAuthAdmin.getBody().getToken();

        // CREAR DOS CONTACTOS
        AddressEntity newAddress = addressMapper.addressAddDtoToEntity(new AddressAddDto("rivadavia", "542"));
        ContactAddDTO newContact = new ContactAddDTO("david1" , "2644647572", newAddress);
        ContactAddDTO newContact2 = new ContactAddDTO("david2" , "2644647571", newAddress);

        HttpHeaders headersAddContact = new HttpHeaders();
        headersAddContact.setBearerAuth(adminToken);
        HttpEntity<ContactAddDTO> httpEntityAddContact = new HttpEntity<>(newContact, headersAddContact);
        HttpEntity<ContactAddDTO> httpEntityAddContact2 = new HttpEntity<>(newContact2, headersAddContact);
        restTemplate.exchange("/contacts/", HttpMethod.POST , httpEntityAddContact , ContactReadDTO.class);
        restTemplate.exchange("/contacts/", HttpMethod.POST , httpEntityAddContact2 , ContactReadDTO.class);
        // PEDIR LISTA CONTACTOS sin enviar JWT
        ResponseEntity<ContactArrayReadDTO> responseGetAllContacts = restTemplate.getForEntity("/contacts/", ContactArrayReadDTO.class);

        // VERIFICO LOS VALORES DEVUELTOS
        assertThat(responseGetAllContacts.getBody().getContacts().size()).isEqualTo(2);
        String firstConctactobjectString = responseGetAllContacts.getBody().getContacts().get(0).toString();
        assertThat(firstConctactobjectString.contains(newContact.getName())).isTrue();
        assertThat(firstConctactobjectString.contains(newContact.getPhone())).isTrue();
        assertThat(firstConctactobjectString.contains(newContact.getAddress().getNumber())).isTrue();
        assertThat(firstConctactobjectString.contains(newContact.getAddress().getStreet())).isTrue();
    }


    @DisplayName("Obtener todos los contactos, enviando JWT")
    @Test
    @Rollback
    public void showAllTest2() throws Exception {
        // CREAR SUPER USUARIO
        RegisterRequest newUserDataAdmin = new RegisterRequest("SUPER2","123456789","123456789","david2","Costa2","3592442","2644647572","super@gmail2.com");
        ResponseEntity<AuthResponse> responseAuthAdmin = restTemplate.postForEntity("/auth/register", newUserDataAdmin,AuthResponse.class);
        String adminToken = responseAuthAdmin.getBody().getToken();

        // CREAR DOS CONTACTOS
        AddressEntity newAddress = addressMapper.addressAddDtoToEntity(new AddressAddDto("rivadavia", "542"));
        ContactAddDTO newContact = new ContactAddDTO("david1" , "2644647572", newAddress);
        ContactAddDTO newContact2 = new ContactAddDTO("david2" , "2644647571", newAddress);

        HttpHeaders headersAddContact = new HttpHeaders();
        headersAddContact.setBearerAuth(adminToken);
        HttpEntity<ContactAddDTO> httpEntityAddContact = new HttpEntity<>(newContact, headersAddContact);
        HttpEntity<ContactAddDTO> httpEntityAddContact2 = new HttpEntity<>(newContact2, headersAddContact);
        restTemplate.exchange("/contacts/", HttpMethod.POST , httpEntityAddContact , ContactReadDTO.class);
        restTemplate.exchange("/contacts/", HttpMethod.POST , httpEntityAddContact2 , ContactReadDTO.class);

        // PEDIR LISTA CONTACTOS
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<ContactArrayReadDTO> responseGetAllContacts = restTemplate.exchange("/contacts/", HttpMethod.GET , httpEntity , ContactArrayReadDTO.class);

        // VERIFICO LOS VALORES DEVUELTOS
        assertThat(responseGetAllContacts.getBody().getContacts().size()).isEqualTo(2);
        String firstConctactobjectString = responseGetAllContacts.getBody().getContacts().get(0).toString();
        assertThat(firstConctactobjectString.contains(newContact.getName())).isTrue();
        assertThat(firstConctactobjectString.contains(newContact.getPhone())).isTrue();
        assertThat(firstConctactobjectString.contains(newContact.getAddress().getNumber())).isTrue();
        assertThat(firstConctactobjectString.contains(newContact.getAddress().getStreet())).isTrue();

    }

/*
    @DisplayName("Obtener todos los contactos, enviando JWT sin permisos suficientes")
    @Test
    @Rollback
    public void showAllTest3() throws Exception {
        // CREAR SUPER USUARIO
        RegisterRequest newUserDataAdmin = new RegisterRequest("SUPER","123456789","123456789","david","Costa","35924410","2644647572","super@gmail2.com");
        ResponseEntity<AuthResponse> responseAuthAdmin = restTemplate.postForEntity("/auth/register", newUserDataAdmin,AuthResponse.class);
        String adminToken = responseAuthAdmin.getBody().getToken();
        // CREAR DOS CONTACTOS
        AddressEntity newAddress = addressMapper.addressAddDtoToEntity(new AddressAddDto("rivadavia", "542"));
        ContactAddDTO newContact = new ContactAddDTO("david1" , "2644647572", newAddress);
        ContactAddDTO newContact2 = new ContactAddDTO("david2" , "2644647571", newAddress);

        // HACER POST DE CONTACTOS
        HttpHeaders headersAddContact = new HttpHeaders();
        headersAddContact.setBearerAuth(adminToken);
        HttpEntity<ContactAddDTO> httpEntityAddContact = new HttpEntity<>(newContact, headersAddContact);
        HttpEntity<ContactAddDTO> httpEntityAddContact2 = new HttpEntity<>(newContact2, headersAddContact);
        restTemplate.exchange("/contacts/", HttpMethod.POST , httpEntityAddContact , ContactReadDTO.class);
        restTemplate.exchange("/contacts/", HttpMethod.POST , httpEntityAddContact2 , ContactReadDTO.class);

        // CREAR USUARIO CON PERMISOS LIMITADOS
        RegisterRequest newUserDataRegular = new RegisterRequest("SUPER","123456789","123456789","david","Costa","35924410","2644647572","regularUser@gmail2.com");
        ResponseEntity<AuthResponse> responseAuthRegularUser = restTemplate.postForEntity("/auth/register", newUserDataRegular,AuthResponse.class);
        String regularToken = responseAuthRegularUser.getBody().getToken();


        // PEDIR LISTA CONTACTOS CON USUARIO REGULAR
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(regularToken);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<ContactArrayReadDTO> responseGetAllContacts = restTemplate.exchange("/contacts/", HttpMethod.GET , httpEntity , ContactArrayReadDTO.class);

        System.out.println("------------ "+responseGetAllContacts+" ***");

        // VERIFICO LOS VALORES DEVUELTOS
        assertThat(responseGetAllContacts.getBody().getContacts().size()).isEqualTo(2);
        String firstConctactobjectString = responseGetAllContacts.getBody().getContacts().get(0).toString();
        assertThat(firstConctactobjectString.contains(newContact.getName())).isTrue();
        assertThat(firstConctactobjectString.contains(newContact.getPhone())).isTrue();
        assertThat(firstConctactobjectString.contains(newContact.getAddress().getNumber())).isTrue();
        assertThat(firstConctactobjectString.contains(newContact.getAddress().getStreet())).isTrue();

    }
*/

}

