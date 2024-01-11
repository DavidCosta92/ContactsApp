package com.contacts.agenda.services;

import com.contacts.agenda.exceptions.customsExceptions.NotFoundException;
import com.contacts.agenda.model.dtos.address.AddressAddDto;
import com.contacts.agenda.model.dtos.address.AddressReadDTO;
import com.contacts.agenda.model.entities.AddressEntity;
import com.contacts.agenda.model.mappers.AddressMapper;
import com.contacts.agenda.model.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressService {
    @Autowired
    AddressMapper addressMapper;
    @Autowired
    AddressRepository addressRepository;


    public Boolean existAddress(AddressEntity address) {
       return addressRepository.findByStreetAndNumber(address.getStreet() , address.getNumber()).isPresent();
    }

    public AddressReadDTO add(AddressAddDto addressAddDto) {
        // TODO VALIDAR ADDRESS A NIVEL DE CONTROLLER CON ANOTACIONES DE SPRING?? O MAS ABAJO? deberia ser a nivel service, para que valide las cosas solicitadas por contactService
        return Optional
                .ofNullable(addressAddDto)
                .map(ent -> addressMapper.addressAddDtoToEntity(ent))
                .map(ent -> addressRepository.save(ent))
                .map(ent -> addressMapper.addressEntityToReadDto(ent))
                .orElse(new AddressReadDTO());

    }


    public AddressEntity getAddressEntityByStreetAndNumber (String street, String number){

        Optional<AddressEntity> ent = addressRepository.findByStreetAndNumber(street , number);

        if (ent.isEmpty()) throw new NotFoundException("Direccion no encontrada!");

        return ent.get();
    }

    public AddressEntity getOrCreateAddress(AddressEntity address) {
        if(existAddress(address)){
            return getAddressEntityByStreetAndNumber(address.getStreet() , address.getNumber());
        } else {
            AddressReadDTO readDTO = add(new AddressAddDto(address.getStreet() , address.getNumber()));
            return addressMapper.addressReadDtoToEntity(readDTO);
        }
    }
}
