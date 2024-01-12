package com.contacts.agenda.integration.contact;

import com.contacts.agenda.auth.entities.AuthResponse;
import com.contacts.agenda.auth.entities.LoguedUserDetails;
import com.contacts.agenda.auth.entities.RegisterRequest;
import com.contacts.agenda.model.dtos.address.AddressAddDto;
import com.contacts.agenda.model.dtos.contact.ContactAddDTO;
import com.contacts.agenda.model.dtos.contact.ContactArrayReadDTO;
import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import com.contacts.agenda.model.mappers.AddressMapper;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AddTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> posgres = new PostgreSQLContainer<>("postgres:16.0");
    // static, para que no levante una nueva por cada test

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    private AddressMapper addressMapper;

    private RegisterRequest newUserData;
    private String token;

    @BeforeEach
    void setUp(){



    }

    @DisplayName("Postgres y docker funcionando")
    @Test
    void conectionStablish (){
        assertThat(posgres.isCreated()).isTrue();
        assertThat(posgres.isRunning()).isTrue();
    }

    @DisplayName("Agregar un contacto")
    @Test
    public void addContactTest1() throws Exception {

        newUserData = new RegisterRequest("SUPER","123456789","123456789","david","Costa","35924410","2644647572","super@gmail.com");
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity("/auth/register", newUserData,AuthResponse.class);
        token = response.getBody().getToken();



        AddressAddDto newAddress = new AddressAddDto("rivadavia", "542");
        ContactAddDTO newContact = new ContactAddDTO("david" , "2644647572", addressMapper.addressAddDtoToEntity(newAddress));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> httpEntity = new HttpEntity<>("body", headers);
        ResponseEntity<ContactReadDTO> responseAddContact = restTemplate.exchange("/contacts/", HttpMethod.POST , httpEntity ,ContactReadDTO.class);

        System.out.print("············ "+responseAddContact+" ···················");
        // todo ME SALTA EL SIGUIENTE ERROR => ············ <403 FORBIDDEN Forbidden,[Vary:"Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers", Accept:"application/json, application/*+json", Content-Length:"0", Date:"Fri, 12 Jan 2024 07:17:53 GMT", Keep-Alive:"timeout=60", Connection:"keep-alive"]> ···················

        System.out.print(" ----------- "+responseAddContact.getBody()+" ----------- ");


        assertThat(responseAddContact.getBody().getName()).isEqualTo(newContact.getName());
        assertThat(responseAddContact.getBody().getPhone()).isEqualTo(newContact.getAddress());
        assertThat(responseAddContact.getBody().getAddress().getNumber()).isEqualTo(newContact.getAddress().getNumber());
        assertThat(responseAddContact.getBody().getAddress().getStreet()).isEqualTo(newContact.getAddress().getStreet());


    }


}
