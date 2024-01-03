package com.contacts.agenda.model.mappers;

import com.contacts.agenda.model.dtos.address.AddressAddDto;
import com.contacts.agenda.model.dtos.address.AddressReadDTO;
import com.contacts.agenda.model.entities.AddressEntity;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {


    public AddressEntity addressAddDtoToEntity (AddressAddDto addDto){
        return AddressEntity
                .builder()
                .street(addDto.getStreet())
                .number(addDto.getNumber())
                .build();
    }
    public AddressReadDTO addressEntityToReadDto (AddressEntity addEntity){
        return AddressReadDTO
                .builder()
                .id(addEntity.getId())
                .street(addEntity.getStreet())
                .number(addEntity.getNumber())
                .build();
    }

    public AddressEntity addressReadDtoToEntity (AddressReadDTO readEntity){
        return AddressEntity
                .builder()
                .id(readEntity.getId())
                .street(readEntity.getStreet())
                .number(readEntity.getNumber())
                .build();
    }
}
