package com.contacts.agenda.integration.auth;

import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {
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
    @DisplayName("Login erroneo, con username erroneo y password correcto")
    @Test
    public void loginTest2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .content("{ \"username\" : \"usernameErroneo\" , \"password\" : \"123456789\" }")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"Bad credentials\",\"internalCode\":6}"))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    @DisplayName("Login erroneo, con username correcto y password erroneo")
    @Test
    public void loginTest3() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .content("{ \"username\" : \"SUPER\" , \"password\" : \"123456789123\" }")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"Bad credentials\",\"internalCode\":6}"))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    @DisplayName("Login erroneo, enviando SOLO username")
    @Test
    public void loginTest4() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .content("{ \"username\" : \"SUPER\" }")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"Bad credentials\",\"internalCode\":6}"))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    }



    /*
    @BeforeEach
    public void setUpTest(){
        // limpiar bd
        // inyectar clases y dependencias necesarias
        // crear usuarios y objetos que necesito
    }

    @DisplayName("Dado un usuario, intentamos loguear y esperamos que se loguee el user")
    @Test
    public void loginTest(){

        // registrar usuario
        // loguear usuario
        // verificar si el user esta logueado
        //  Assertions.assertEquals("" , "", "LOS VALORES NO SON IGUALESSSSS");
        //  Assertions.assertTrue(true);
        //  Assertions.assertFalse(false);
        //  Assertions.assertNull(null);
        //  Assertions.assertNotNull(false);
        // Assertions.assertThrows(Exception.class, ());

    }
    @DisplayName("dado algo...")
    @Test
    public void registerTest(){
    }

    @DisplayName("dado algo...")
    @Test
    public void getLoguedUserTest(){
    }

     */

}
