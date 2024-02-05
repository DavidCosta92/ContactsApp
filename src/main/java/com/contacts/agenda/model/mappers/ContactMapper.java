package com.contacts.agenda.model.mappers;

import com.contacts.agenda.model.dtos.contact.ContactAddDTO;
import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import com.contacts.agenda.model.entities.ContactEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ContactMapper {

    public ContactReadDTO toReadDTO(ContactEntity entity){
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

    public ContactEntity toEntity(ContactAddDTO dto) {
        return ContactEntity.builder()
                            .name(dto.getName())
                            .phone(dto.getPhone())
                            .address(dto.getAddress())
                            .build();
    }
}
