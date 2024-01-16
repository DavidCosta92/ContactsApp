package com.contacts.agenda.integration.contact;

import com.contacts.agenda.auth.AuthService;
import com.contacts.agenda.auth.entities.*;
import com.contacts.agenda.exceptions.ExceptionMessages;
import com.contacts.agenda.exceptions.customsExceptions.AlreadyExistException;
import com.contacts.agenda.model.dtos.address.AddressAddDto;
import com.contacts.agenda.model.dtos.contact.ContactAddDTO;
import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import com.contacts.agenda.model.mappers.AddressMapper;
import com.contacts.agenda.services.ContactService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AddContactTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> posgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private AuthService authService;
    @Autowired
    private ContactService contactService;
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
            .build();;

    private RegisterRequest newRegularUserData;
    private String regularToken;
    User regular = User.builder()
            .username("regular")
            .password("123456789")
            .firstName("davidReg")
            .lastName("CostaReg")
            .phone("2644647573")
            .dni("35924419")
            .email("regularUser@gmail2.com")
            .role(Role.USER)
            .build();;


    @BeforeEach
    void setUp(){
        setUpAdminToken();
        setUpRegularToken();
    }

    // EXTRA METHODS
    public void setUpAdminToken(){
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
    public void setUpRegularToken(){
        // CREAR NUEVO REGULAR USER, SINO OBTENER EL QUE YA ESTA EN BD
        try {
            authService.validateNewEmail(regular.getEmail());
            newRegularUserData = new RegisterRequest(regular.getUsername(),regular.getPassword(),regular.getPassword(),regular.getFirstName(),regular.getLastName(),regular.getDni(),regular.getPhone(),regular.getEmail());
            ResponseEntity<AuthResponse> responseAuthRegular = restTemplate.postForEntity("/auth/register", newRegularUserData,AuthResponse.class);
            regularToken = responseAuthRegular.getBody().getToken();
        } catch (Exception e){
            LoginRequest loginRequest = new LoginRequest(regular.getUsername() , regular.getPassword());
            regularToken = authService.login(loginRequest).getToken();
        }
    }

    public ContactAddDTO createContact(String name, String phone, String street, String number){
        AddressAddDto newAddress = new AddressAddDto(street, number);
        return new ContactAddDTO(name , phone, addressMapper.addressAddDtoToEntity(newAddress));
    }
    public HttpEntity<ContactAddDTO> gethttpEntityForPostContact (ContactAddDTO contact, String token){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(contact, headers);
    }
    public HttpEntity<ContactAddDTO> gethttpEntityForPostContact (ContactAddDTO contact){
        return new HttpEntity<>(contact, new HttpHeaders());
    }


    @DisplayName("Postgres y docker funcionando")
    @Test
    void conectionStablish (){
        assertThat(posgres.isCreated()).isTrue();
        assertThat(posgres.isRunning()).isTrue();
    }

    @DisplayName("Agregar un contacto, con JWT e info Correcta")
    @Test
    public void addContactTest1() throws Exception {
        ContactAddDTO newContact = createContact("david" , "2644647572", "rivadavia", "542");
        ResponseEntity<ContactReadDTO> responseAddContact = restTemplate.exchange(
                "/contacts/",
                HttpMethod.POST ,
                gethttpEntityForPostContact(newContact , adminToken) ,
                ContactReadDTO.class
        );
        assertThat(responseAddContact.getBody().getName()).isEqualTo(newContact.getName());
        assertThat(responseAddContact.getBody().getPhone()).isEqualTo(newContact.getPhone());
        assertThat(responseAddContact.getBody().getAddress().getNumber()).isEqualTo(newContact.getAddress().getNumber());
        assertThat(responseAddContact.getBody().getAddress().getStreet()).isEqualTo(newContact.getAddress().getStreet());
    }

    @DisplayName("Agregar un contacto, sin JWT.")
    @Test
    public void addContactTest2() throws Exception {
        ContactAddDTO newContact = createContact("david" , "2644647572", "rivadavia", "542");
        ResponseEntity<ExceptionMessages> responseAddContact = restTemplate.exchange(
                "/contacts/", HttpMethod.POST ,
                gethttpEntityForPostContact(newContact) ,
                ExceptionMessages.class
        );
        assertThat(responseAddContact.getStatusCode().is4xxClientError()).isTrue();
        assertThat(responseAddContact.getBody().getInternalCode()).isEqualTo(6);
        assertThat(responseAddContact.getBody().getMessage()).isEqualTo("Access Denied");
    }

    @DisplayName("Agregar un contacto, con JWT pero permisos insuficientes")
    @Test
    public void addContactTest3() throws Exception {
        ContactAddDTO newContact = createContact("david" , "2644647572", "rivadavia", "542");
        ResponseEntity<ExceptionMessages> responseAddContact = restTemplate.exchange(
                "/contacts/",
                HttpMethod.POST ,
                gethttpEntityForPostContact(newContact , regularToken) ,
                ExceptionMessages.class
        );
        assertThat(responseAddContact.getStatusCode().is4xxClientError()).isTrue();
        assertThat(responseAddContact.getBody().getInternalCode()).isEqualTo(6);
        assertThat(responseAddContact.getBody().getMessage()).isEqualTo("Access Denied");
    }

    @DisplayName("Agregar un contacto con informacion duplicada, con JWT correcto")
    @Test
    public void addContactTest4() throws Exception {
        // Contacto 1
        ContactAddDTO newContact1 = createContact("david" , "2644647572", "rivadavia", "542");
        ResponseEntity<ContactReadDTO> responseAddContact = restTemplate.exchange(
                "/contacts/",
                HttpMethod.POST ,
                gethttpEntityForPostContact(newContact1 , adminToken) ,
                ContactReadDTO.class
        );

        // Contacto 2, duplicado
        ContactAddDTO newContact2 = createContact("david" , "264461172", "rivadavia", "542");
        ResponseEntity<AlreadyExistException> responseAddContactDuplicated = restTemplate.exchange(
                "/contacts/",
                HttpMethod.POST ,
                gethttpEntityForPostContact(newContact2 , adminToken) ,
                AlreadyExistException.class);

        assertThat(responseAddContactDuplicated.getStatusCode().isSameCodeAs(HttpStatus.CONFLICT)).isTrue();
    }

    // TODO CASO DE PRUEBA: AGREGAR CONTANCTO CON INFO INVALIDA
    // TODO CASO DE PRUEBA: AGREGAR CONTANCTO CON INFO INVALIDA
    // TODO CASO DE PRUEBA: AGREGAR CONTANCTO CON INFO INVALIDA
    // TODO CASO DE PRUEBA: AGREGAR CONTANCTO CON INFO INVALIDA
    // TODO CASO DE PRUEBA: AGREGAR CONTANCTO CON INFO INVALIDA
    // TODO CASO DE PRUEBA: AGREGAR CONTANCTO CON INFO INVALIDA

    @DisplayName("Agregar un contacto, con info invalida.")
    @Test
    public void addContactTest5() throws Exception {
        ContactAddDTO newContact = createContact("david" , "2644647572", "rivadavia", "542");
        ResponseEntity<ExceptionMessages> responseAddContact = restTemplate.exchange(
                "/contacts/",
                HttpMethod.POST ,
                gethttpEntityForPostContact(newContact , regularToken) ,
                ExceptionMessages.class
        );
        assertThat(responseAddContact.getStatusCode().is4xxClientError()).isTrue();
        assertThat(responseAddContact.getBody().getInternalCode()).isEqualTo(6);
        assertThat(responseAddContact.getBody().getMessage()).isEqualTo("Access Denied");
    }

    // TODO CASO DE PRUEBA: AGREGAR CONTANCTO CON INFO INCOMPLETA
    // TODO CASO DE PRUEBA: AGREGAR CONTANCTO CON INFO INCOMPLETA
    // TODO CASO DE PRUEBA: AGREGAR CONTANCTO CON INFO INCOMPLETA
    // TODO CASO DE PRUEBA: AGREGAR CONTANCTO CON INFO INCOMPLETA
    // TODO CASO DE PRUEBA: AGREGAR CONTANCTO CON INFO INCOMPLETA


}
