package com.contacts.agenda.services;

import com.contacts.agenda.exceptions.customsExceptions.NotFoundException;
import com.contacts.agenda.model.dtos.address.AddressAddDto;
import com.contacts.agenda.model.dtos.address.AddressReadDTO;
import com.contacts.agenda.model.entities.AddressEntity;
import com.contacts.agenda.model.mappers.AddressMapper;
import com.contacts.agenda.model.repositories.AddressRepository;
import com.contacts.agenda.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressService {
    @Autowired
    AddressMapper addressMapper;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    Validator validator;


    public Boolean existAddress(AddressEntity address) {
       return addressRepository.findByStreetAndNumber(address.getStreet() , address.getNumber()).isPresent();
    }
    public void validateStreet(String street){
        validator.stringMinSize("Street", 3, street);
    }
    public void validateStreetNumber(String streetNumber){
        validator.stringMinSize("Street number",2, streetNumber);
        validator.stringOnlyNumbers("Street number", streetNumber);
    }
    public AddressReadDTO add(AddressAddDto addressAddDto) {
        validateStreet(addressAddDto.getStreet());
        validateStreetNumber(addressAddDto.getNumber());
        return Optional
                .ofNullable(addressAddDto)
                .map(ent -> addressMapper.toEntity(ent))
                .map(ent -> addressRepository.save(ent))
                .map(ent -> addressMapper.toReadDto(ent))
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
            return addressMapper.toEntity(readDTO);
        }
    }
}
