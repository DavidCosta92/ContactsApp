package com.contacts.agenda.services;

import com.contacts.agenda.exceptions.customsExceptions.AlreadyExistException;
import com.contacts.agenda.exceptions.customsExceptions.NotFoundException;
import com.contacts.agenda.exceptions.customsExceptions.NotFoundInputException;
import com.contacts.agenda.model.dtos.contact.ContactAddDTO;
import com.contacts.agenda.model.dtos.contact.ContactArrayReadDTO;
import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import com.contacts.agenda.model.dtos.contact.ContactUpdateDTO;
import com.contacts.agenda.model.entities.AddressEntity;
import com.contacts.agenda.model.entities.ContactEntity;
import com.contacts.agenda.model.mappers.ContactMapper;
import com.contacts.agenda.model.repositories.ContactRepository;
import com.contacts.agenda.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ContactService {
    @Autowired
    ContactRepository contactRepository;
    @Autowired
    ContactMapper contactMapper;
    @Autowired
    AddressService addressService;
    @Autowired
    Validator validator;

    public ContactArrayReadDTO findAll(String name, String phone, String street, Integer pageNumber, Integer pageSize,String sortBy){
        Page<ContactEntity> results;
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        if (name != null & phone != null) {
            validPhoneValue(phone);
            results = contactRepository.findAllByNameContainsAndPhoneContains(name, phone, pageable);
        } else if (phone != null) {
            validPhoneValue(phone);
            results = contactRepository.findAllByPhoneContains(phone, pageable);
        } else if (name != null) {
            results = contactRepository.searchByNameLike(name, pageable);
        }else if (street != null) {
            results = contactRepository.searchByStreetLike(street, pageable);
        } else {
            results = contactRepository.findAll(pageable);
        }
        Page pagedResults = results.map(entity -> contactMapper.toReadDTO(entity));

        return ContactArrayReadDTO.builder()
                .contacts(pagedResults.getContent())
                .total_results(pagedResults.getTotalElements())
                .results_per_page(pageSize)
                .current_page(pageNumber)
                .pages(pagedResults.getTotalPages())
                .sort_by(sortBy)
                .build();
    }
    public ContactReadDTO add(ContactAddDTO contactAddDTO){
        validateContactAddDTO(contactAddDTO);
        AddressEntity addressToSet = addressService.getOrCreateAddress(contactAddDTO.getAddress());
        contactAddDTO.setAddress(addressToSet);
        return Optional
                .ofNullable(contactAddDTO)
                .map(dto -> contactMapper.toEntity(dto))
                .map(entity -> contactRepository.save(entity))
                .map(entity -> contactMapper.toReadDTO(entity))
                .orElse(new ContactReadDTO());
    }
    public ContactReadDTO findById(Integer contactId){
        Optional<ContactEntity> contact = contactRepository.findById(contactId);
        if (contact.isEmpty()) {
            throw new NotFoundException("No se encontro contacto");
        }
        return contactMapper.toReadDTO(contact.get());
    }
    public ContactReadDTO updateById(Integer id, ContactUpdateDTO contactUpdateDTO){
        existsById(id);
        ContactEntity contactEntity = contactRepository.getReferenceById(id);

        String name = contactUpdateDTO.getName();
        if (name != null) {
            validateName(name);
            contactEntity.setName(name);
            contactRepository.save(contactEntity);
        }
        String phone = contactUpdateDTO.getPhone();
        if (phone != null) {
            validatePhone(phone);
            contactEntity.setPhone(phone);
            contactRepository.save(contactEntity);
        }

        AddressEntity address = contactUpdateDTO.getAddress();
        if (address != null){
            if (address.getStreet() != null && address.getNumber() == null){
                address.setNumber(contactEntity.getAddress().getNumber());
            } else if (address.getNumber() != null && address.getStreet() == null){
                address.setStreet(contactEntity.getAddress().getStreet());
            }
            AddressEntity newAddress = addressService.getOrCreateAddress(address);
            contactEntity.setAddress(newAddress);
            contactRepository.save(contactEntity);
        }
        return contactMapper.toReadDTO(contactEntity);
    }
    public ContactReadDTO deleteById(Integer contactId){
        ContactReadDTO contactReadDTO = findById(contactId);
        contactRepository.deleteById(contactId);
        return contactReadDTO;
    }
    public List<ContactReadDTO> addMany(ContactAddDTO contactAddDTOList[]){
        return Arrays.stream(contactAddDTOList).map((contact) -> add(contact)).toList();
    }
    public void validateContactAddDTO(ContactAddDTO contactAddDTO) {
        String name = contactAddDTO.getName();
        String phone = contactAddDTO.getPhone();
        AddressEntity address = contactAddDTO.getAddress();
        if (name == null) {
            throw new NotFoundInputException("Debes revisar el campo NAME");
        } else if (phone == null) {
            throw new NotFoundInputException("Debes revisar el campo PHONE");
        } else if (address == null) {
            throw new NotFoundInputException("Debes revisar el campo ADDRESS");
        } else if (address.getNumber() == null) {
            throw new NotFoundInputException("Debes revisar el campo NUMBER, dentro de ADDRESS");
        } else if (address.getStreet() == null) {
            throw new NotFoundInputException("Debes revisar el campo STREET, dentro de ADDRESS");
        } else {
            validateName(name);
            validatePhone(phone);
        }
    }
    public void existsById(Integer id){
        if(!contactRepository.existsById(id)) throw new NotFoundException("No existe contacto por id");
    }
    public void validPhoneValue(String phone){
        validator.validPhoneNumber(phone);
    }
    public void validatePhone(String phone){
        validPhoneValue(phone);
        if(contactRepository.existsByPhone(phone)) throw new AlreadyExistException("Telefono ya existente!");
    }
    public void validateName(String name){
        validator.stringMinSize("Nombre", 3, name);
        if(contactRepository.existsByName(name)) throw new AlreadyExistException("Nombre ya existente!");
    }

    public ContactReadDTO findByNameAndPhone(String name , String phone){
        return  contactMapper.toReadDTO(contactRepository.findByNameAndPhone(name , phone));
    }

}
