package com.contacts.agenda.controllers;

import com.contacts.agenda.auth.entities.AuthResponse;
import com.contacts.agenda.exceptions.ExceptionMessages;
import com.contacts.agenda.model.dtos.contact.ContactAddDTO;
import com.contacts.agenda.model.dtos.contact.ContactArrayReadDTO;
import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import com.contacts.agenda.model.dtos.contact.ContactUpdateDTO;
import com.contacts.agenda.services.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts/")
@Log4j2 // => generará un registrador para la clase => private static final Logger log = LoggerFactory.getLogger(MiClase.class) y crea variable estática llamada log que ofrece las utilidades del registrado
public class ContactController {
    // private static final Logger log = LoggerFactory.getLogger(ContactController.class);
    @Autowired
    ContactService contactService;

    @Operation(summary = "Shows all contacts, requires a valid JWT with READ_ALL permission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List with all the contacts",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContactReadDTO.class)) }),
            @ApiResponse(responseCode = "403", description = "Forbidden, Access Denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionMessages.class)) }) })
    @GetMapping
    @PreAuthorize("hasAuthority('READ_ALL')")
    public ResponseEntity<ContactArrayReadDTO> showAll(@RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String phone, // TODO VALIDAR QUE ES UN STRING CASTEABLE A INTEGER
                                                       @RequestParam(required = false, defaultValue = "0") Integer page,
                                                       @RequestParam(required = false, defaultValue = "10") Integer size,
                                                       @RequestParam(required = false, defaultValue = "name") String sortBy) {
        log.trace("Viendo todos los contactos");
        log.debug("Viendo todos los contactos");
        log.info(" info Viendo todos los contactos");
        log.warn(" warn Viendo todos los contactos");
        log.error(" error Viendo todos los contactos");
        return new ResponseEntity<>(contactService.findAll(name, phone, page, size, sortBy), HttpStatus.OK);
    }
    @Operation(summary = "Creates contact, requires a valid JWT with CREATE_CONTACT permission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContactReadDTO.class)) }),
            @ApiResponse(responseCode = "406", description = "Not Acceptable, Error as result of sending invalid data",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionMessages.class)) }),
            @ApiResponse(responseCode = "403", description = "Forbidden, Access Denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionMessages.class)) }) })
    @PreAuthorize("hasAuthority('CREATE_CONTACT')")
    @PostMapping
    public ResponseEntity<ContactReadDTO> add (@Valid @RequestBody ContactAddDTO contactoAddDTO) {
        return new ResponseEntity<>(contactService.add(contactoAddDTO), HttpStatus.CREATED);
    }
    @Operation(summary = "Shows contact by ID , requires a valid JWT with READ_ALL permission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact by ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContactReadDTO.class)) }),
            @ApiResponse(responseCode = "403", description = "Forbidden, Access Denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionMessages.class)) }) ,
            @ApiResponse(responseCode = "404", description = "Not found by ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionMessages.class)) })  })
    @PreAuthorize("hasAuthority('READ_ALL')")
    @GetMapping("{id}")
    public ResponseEntity<ContactReadDTO> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(contactService.findById(id), HttpStatus.OK);
    }
    @Operation(summary = "Edit contact by ID , requires a valid JWT with EDIT_ALL permission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Returns edited contact",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContactReadDTO.class)) }),
            @ApiResponse(responseCode = "403", description = "Forbidden, Access Denied",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionMessages.class)) }) ,
            @ApiResponse(responseCode = "404", description = "Not found by ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionMessages.class)) }),
            @ApiResponse(responseCode = "409", description = "Conflict, Error as result of sending invalid data, Ex: 'Nombre ya existe'",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionMessages.class)) })   })
    @PreAuthorize("hasAuthority('EDIT_ALL')")
    @PutMapping("update/{id}")
    public ResponseEntity<ContactReadDTO> updateById(@PathVariable Integer id, @RequestBody ContactUpdateDTO contactUpdateDTO) {
        return new ResponseEntity<>(contactService.updateById(id, contactUpdateDTO), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<ContactReadDTO> deleteById(@PathVariable Integer id) {
        return new ResponseEntity<>(contactService.deleteById(id), HttpStatus.OK);
    }

/*

    @PostMapping("addMany")
    public ResponseEntity<List<ContactoReadDTO>> addMany(@RequestBody ContactoAddDTO contactoAddDTO[]) {
        return new ResponseEntity<>(contactoService.addMany(contactoAddDTO), HttpStatus.CREATED);
    }

 */
}
