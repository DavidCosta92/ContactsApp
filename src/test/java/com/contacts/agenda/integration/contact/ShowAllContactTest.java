package com.contacts.agenda.integration.contact;

import com.contacts.agenda.auth.AuthService;
import com.contacts.agenda.auth.entities.*;
import com.contacts.agenda.model.dtos.address.AddressAddDto;
import com.contacts.agenda.model.dtos.contact.ContactAddDTO;
import com.contacts.agenda.model.dtos.contact.ContactArrayReadDTO;
import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import com.contacts.agenda.model.mappers.AddressMapper;
import com.contacts.agenda.model.mappers.ContactMapper;
import com.contacts.agenda.services.ContactService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class ShowAllContactTest {

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
    @Autowired
    private ContactService contactService;
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
            .build();

    private ContactReadDTO contactReadDTO1;
    private ContactReadDTO contactReadDTO2;

    @BeforeEach
    void setUp(){
        setUpAdminToken();
        setUpRegularToken();
        createContacts();
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
        return new ContactAddDTO(name , phone, addressMapper.toEntity(newAddress));
    }
    public HttpEntity<ContactAddDTO> gethttpEntityForPostContact (ContactAddDTO contact, String token){
        HttpHeaders headers = new HttpHeaders();
        if(token != null) headers.setBearerAuth(token);
        return new HttpEntity<>(contact, headers);
    }
    public HttpEntity<String> gethttpEntityForGetContact(String token){
        HttpHeaders headers = new HttpHeaders();
        if(token != null) headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }
    public void createContacts (){
        try {
            ContactAddDTO newContact1 = createContact("david1" , "2644647571", "rivadavia", "542");
            contactService.validateName("david1");
            contactReadDTO1 = restTemplate.exchange("/contacts/", HttpMethod.POST , gethttpEntityForPostContact(newContact1 , adminToken) , ContactReadDTO.class).getBody();

            ContactAddDTO newContact2 = createContact("david2" , "2644647572", "rivadavia", "542");
            contactService.validateName("david2");
            contactReadDTO2 = restTemplate.exchange("/contacts/", HttpMethod.POST , gethttpEntityForPostContact(newContact2 , adminToken) , ContactReadDTO.class).getBody();
        } catch (Exception e){
            contactReadDTO1 = contactService.findByNameAndPhone("david1" , "2644647571");
            contactReadDTO2 = contactService.findByNameAndPhone("david2" , "2644647572");
        }
    }

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
        // PEDIR LISTA CONTACTOS sin enviar JWT
        ResponseEntity<ContactArrayReadDTO> responseGetAllContacts = restTemplate.getForEntity("/contacts/", ContactArrayReadDTO.class);

        // VERIFICO LOS VALORES DEVUELTOS
        assertThat(responseGetAllContacts.getBody().getCurrent_page()).isEqualTo(0);
        assertThat(responseGetAllContacts.getBody().getPages()).isEqualTo(1);
        assertThat(responseGetAllContacts.getBody().getSort_by()).isEqualTo("name");
        assertThat(responseGetAllContacts.getBody().getResults_per_page()).isEqualTo(10);
        assertThat(responseGetAllContacts.getBody().getTotal_results()).isEqualTo(2);
        assertThat(responseGetAllContacts.getBody().getContacts().size()).isEqualTo(2);
        String firstConctactobjectString = responseGetAllContacts.getBody().getContacts().get(0).toString();
        assertThat(firstConctactobjectString.contains(contactReadDTO1.getName())).isTrue();
        assertThat(firstConctactobjectString.contains(contactReadDTO1.getPhone())).isTrue();
        assertThat(firstConctactobjectString.contains(contactReadDTO1.getAddress().getNumber())).isTrue();
        assertThat(firstConctactobjectString.contains(contactReadDTO1.getAddress().getStreet())).isTrue();
    }

    @DisplayName("Obtener todos los contactos, enviando JWT")
    @Test
    @Rollback
    public void showAllTest2() throws Exception {
        ResponseEntity<ContactArrayReadDTO> responseGetAllContacts = restTemplate.exchange("/contacts/", HttpMethod.GET , gethttpEntityForGetContact(adminToken) , ContactArrayReadDTO.class);

        // VERIFICO LOS VALORES DEVUELTOS
        assertThat(responseGetAllContacts.getBody().getContacts().size()).isEqualTo(2);
        String firstConctactobjectString = responseGetAllContacts.getBody().getContacts().get(0).toString();
        assertThat(firstConctactobjectString.contains(contactReadDTO1.getName())).isTrue();
        assertThat(firstConctactobjectString.contains(contactReadDTO1.getPhone())).isTrue();
        assertThat(firstConctactobjectString.contains(contactReadDTO1.getAddress().getNumber())).isTrue();
        assertThat(firstConctactobjectString.contains(contactReadDTO1.getAddress().getStreet())).isTrue();

    }

    @DisplayName("Obtener todos los contactos, enviando opciones de busqueda por nombre")
    @Test
    @Rollback
    public void showAllTest3() throws Exception {
        String paramName = "?name="+contactReadDTO1.getName().substring(0 , contactReadDTO1.getName().length()-1);
        String paramNameJust1 = "?name="+contactReadDTO1.getName();
        String paramNameWrong = "?name=algo";
        ResponseEntity<ContactArrayReadDTO> resp = restTemplate.exchange("/contacts/"+paramName , HttpMethod.GET , gethttpEntityForGetContact(adminToken) , ContactArrayReadDTO.class);
        ResponseEntity<ContactArrayReadDTO> respJust1 = restTemplate.exchange("/contacts/"+paramNameJust1 , HttpMethod.GET , gethttpEntityForGetContact(adminToken) , ContactArrayReadDTO.class);
        ResponseEntity<ContactArrayReadDTO> respNone = restTemplate.exchange("/contacts/"+paramNameWrong , HttpMethod.GET , gethttpEntityForGetContact(adminToken) , ContactArrayReadDTO.class);

        assertThat(respNone.getBody().getTotal_results()).isEqualTo(0);
        assertThat(respJust1.getBody().getTotal_results()).isEqualTo(1);
        assertThat(respJust1.getBody().getContacts().get(0).toString().contains(contactReadDTO1.getName())).isTrue();
        assertThat(resp.getBody().getTotal_results()).isEqualTo(2);
        assertThat(resp.getBody().getContacts().get(0).toString().contains(contactReadDTO1.getName())).isTrue();
        assertThat(resp.getBody().getContacts().get(1).toString().contains(contactReadDTO2.getName())).isTrue();

    }
    @DisplayName("Obtener todos los contactos, enviando opciones de busqueda por street")
    @Test
    @Rollback
    public void showAllTest4() throws Exception {
        String paramStreet = "?street="+contactReadDTO1.getAddress().getStreet();
        String paramStreetWrong = "?street=algo";
        ResponseEntity<ContactArrayReadDTO> respAll = restTemplate.exchange("/contacts/"+paramStreet , HttpMethod.GET , gethttpEntityForGetContact(adminToken) , ContactArrayReadDTO.class);
        ResponseEntity<ContactArrayReadDTO> respNone = restTemplate.exchange("/contacts/"+paramStreetWrong , HttpMethod.GET , gethttpEntityForGetContact(adminToken) , ContactArrayReadDTO.class);

        assertThat(respAll.getBody().getTotal_results()).isEqualTo(2);
        assertThat(respAll.getBody().getContacts().get(0).toString().contains(contactReadDTO1.getAddress().getStreet())).isTrue();
        assertThat(respAll.getBody().getContacts().get(1).toString().contains(contactReadDTO1.getAddress().getStreet())).isTrue();
        assertThat(respNone.getBody().getTotal_results()).isEqualTo(0);
    }
    @DisplayName("Obtener todos los contactos, enviando opciones de busqueda por phone")
    @Test
    @Rollback
    public void showAllTest5() throws Exception {
        String paramPhoneAll = "?phone="+contactReadDTO1.getPhone().substring(0 ,contactReadDTO1.getPhone().length() -1);
        String paramPhoneJust1 = "?phone="+contactReadDTO1.getPhone();
        String paramPhoneWrong = "?phone=2644647570";
        ResponseEntity<ContactArrayReadDTO> respAll = restTemplate.exchange("/contacts/"+paramPhoneAll , HttpMethod.GET , gethttpEntityForGetContact(adminToken) , ContactArrayReadDTO.class);
        ResponseEntity<ContactArrayReadDTO> respJust1 = restTemplate.exchange("/contacts/"+paramPhoneJust1 , HttpMethod.GET , gethttpEntityForGetContact(adminToken) , ContactArrayReadDTO.class);
        ResponseEntity<ContactArrayReadDTO> respNone = restTemplate.exchange("/contacts/"+paramPhoneWrong , HttpMethod.GET , gethttpEntityForGetContact(adminToken) , ContactArrayReadDTO.class);

        assertThat(respAll.getBody().getTotal_results()).isEqualTo(2);
        assertThat(respAll.getBody().getContacts().get(0).toString().contains(contactReadDTO1.getPhone().substring(0 ,contactReadDTO1.getPhone().length() -1))).isTrue();
        assertThat(respAll.getBody().getContacts().get(1).toString().contains(contactReadDTO1.getPhone().substring(0 ,contactReadDTO1.getPhone().length() -1))).isTrue();
        assertThat(respJust1.getBody().getTotal_results()).isEqualTo(1);
        assertThat(respJust1.getBody().getContacts().get(0).toString().contains(contactReadDTO1.getPhone())).isTrue();
        assertThat(respNone.getBody().getTotal_results()).isEqualTo(0);
    }

}

