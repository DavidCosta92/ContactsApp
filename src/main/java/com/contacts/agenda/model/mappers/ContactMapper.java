package com.contacts.agenda.model.mappers;

import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import com.contacts.agenda.model.entities.ContactEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ContactMapper {

    public ContactReadDTO contactEntityToReadDTO (ContactEntity entity){
        return Optional
                .ofNullable(entity)
                .map(entity1 -> ContactReadDTO
                        .builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .phone(entity.getPhone())
                        .address(entity.getAddress())
                        .build())
                .orElseThrow();
                //.orElse(new ContactReadDTO());
    }
}
