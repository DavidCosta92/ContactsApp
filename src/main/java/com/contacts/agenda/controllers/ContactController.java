package com.contacts.agenda.controllers;

import com.contacts.agenda.auth.entities.AuthResponse;
import com.contacts.agenda.exceptions.ExceptionMessages;
import com.contacts.agenda.model.dtos.contact.ContactAddDTO;
import com.contacts.agenda.model.dtos.contact.ContactArrayReadDTO;
import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import com.contacts.agenda.services.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts/")
public class ContactController {

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
                                                       @RequestParam(required = false) String phone,
                                                       @RequestParam(required = false, defaultValue = "0") Integer page,
                                                       @RequestParam(required = false, defaultValue = "10") Integer size,
                                                       @RequestParam(required = false, defaultValue = "name") String sortBy) {
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
    public ResponseEntity<ContactReadDTO> add(@RequestBody ContactAddDTO contactoAddDTO) {
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
    public ResponseEntity<ContactReadDTO> updateById(@PathVariable Integer id,
                                                     @RequestBody ContactAddDTO contactAddDTO) {
        return new ResponseEntity<>(contactService.updateById(id, contactAddDTO), HttpStatus.ACCEPTED);
    }


/*

    @PostMapping("addMany")
    public ResponseEntity<List<ContactoReadDTO>> addMany(@RequestBody ContactoAddDTO contactoAddDTO[]) {
        return new ResponseEntity<>(contactoService.addMany(contactoAddDTO), HttpStatus.CREATED);
    }


    @DeleteMapping("delete/{id}")
    public ResponseEntity<ContactoReadDTO> deleteById(@PathVariable Integer id) {
        return new ResponseEntity<>(contactoService.deleteById(id), HttpStatus.OK);
    }

 */
}
