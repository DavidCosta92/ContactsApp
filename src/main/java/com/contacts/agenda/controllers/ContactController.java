package com.contacts.agenda.controllers;

import com.contacts.agenda.model.dtos.contact.ContactAddDTO;
import com.contacts.agenda.model.dtos.contact.ContactArrayReadDTO;
import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import com.contacts.agenda.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts/")
public class ContactController {

    @Autowired
    ContactService contactService;

    @GetMapping
    public ResponseEntity<ContactArrayReadDTO> showAll(@RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String phone,
                                                       @RequestParam(required = false, defaultValue = "0") Integer page,
                                                       @RequestParam(required = false, defaultValue = "10") Integer size,
                                                       @RequestParam(required = false, defaultValue = "name") String sortBy) {
        return new ResponseEntity<>(contactService.findAll(name, phone, page, size, sortBy), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<ContactReadDTO> add(@RequestBody ContactAddDTO contactoAddDTO) {
        return new ResponseEntity<>(contactService.add(contactoAddDTO), HttpStatus.CREATED);
    }
/*


    @GetMapping("{id}")
    public ResponseEntity<ContactoAddDTO> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(contactoService.findById(id), HttpStatus.OK);
    }



    @PostMapping("addMany")
    public ResponseEntity<List<ContactoReadDTO>> addMany(@RequestBody ContactoAddDTO contactoAddDTO[]) {
        return new ResponseEntity<>(contactoService.addMany(contactoAddDTO), HttpStatus.CREATED);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<ContactoAddDTO> updateById(@PathVariable Integer id,
                                                     @RequestBody ContactoAddDTO contactoAddDTO) {
        return new ResponseEntity<>(contactoService.updateById(id, contactoAddDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<ContactoReadDTO> deleteById(@PathVariable Integer id) {
        return new ResponseEntity<>(contactoService.deleteById(id), HttpStatus.OK);
    }

 */
}
