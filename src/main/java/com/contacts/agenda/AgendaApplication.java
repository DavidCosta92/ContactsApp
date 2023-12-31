package com.contacts.agenda;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
@SpringBootApplication
public class AgendaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgendaApplication.class, args);
	}
	@Bean
	public OpenAPI swaggerDocsConfig (){
		return new OpenAPI()
				.info(new Info()
						.title("Apirest ContactsApp")
						.version("1.0")
						.description("Esta API RESTful, permite manejar una agenda de contactos, se pueden agregar y obtener contactos, manejando información como Nombre, Teléfono y Dirección. También permite buscar contactos por ID, o nombre, o bien mostrar todos los contactos almacenados. Desarrolada en JAVA 17, Springboot 3, posgresql. Despliegue en Railway y FL0.")
						.summary("MODIFICAR =>  PONER URL DESPLIEGUE")
						.contact(new Contact().email("davidcst2991@gmail.com").name("Costa david").url("https://www.linkedin.com/in/david-costa-yafar/")));
	}
}
