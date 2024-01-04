package com.contacts.agenda.services;

import com.contacts.agenda.exceptions.customsExceptions.AlreadyExistException;
import com.contacts.agenda.exceptions.customsExceptions.NotFoundException;
import com.contacts.agenda.model.dtos.address.AddressAddDto;
import com.contacts.agenda.model.dtos.address.AddressReadDTO;
import com.contacts.agenda.model.dtos.contact.ContactAddDTO;
import com.contacts.agenda.model.dtos.contact.ContactArrayReadDTO;
import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import com.contacts.agenda.model.entities.AddressEntity;
import com.contacts.agenda.model.entities.ContactEntity;
import com.contacts.agenda.model.mappers.ContactMapper;
import com.contacts.agenda.model.repositories.ContactRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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


    public ContactArrayReadDTO findAll(String name, String phone, Integer pageNumber, Integer pageSize,
                                       String sortBy){
        Page<ContactEntity> results;
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        if (name != null & phone != null) {
            results = contactRepository.findAllByNameContainsAndPhoneContains(name, phone, pageable);
        } else if (phone != null) {
            results = contactRepository.findAllByPhoneContains(phone, pageable);
        } else if (name != null) {
            results = contactRepository.searchByNameLike(name, pageable);//.searchByNameLike(name, pageable);
        } else {
            results = contactRepository.findAll(pageable);
        }

        Page pagedResults = results.map(entity -> contactMapper.contactEntityToReadDTO(entity));

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

        //TODO validateContactAddDTO(contactAddDTO); => ESTO LO DEBERIA IMPLEMENTAR CON LAS CLASES QUE ME DA SPRINGBOOT
        //TODO validateContactAddDTO(contactAddDTO); => ESTO LO DEBERIA IMPLEMENTAR CON LAS CLASES QUE ME DA SPRINGBOOT
        //TODO validateContactAddDTO(contactAddDTO); => ESTO LO DEBERIA IMPLEMENTAR CON LAS CLASES QUE ME DA SPRINGBOOT

        String street = contactAddDTO.getAddress().getStreet();
        String number = contactAddDTO.getAddress().getNumber();

        Boolean existAddress = addressService.existAddress(contactAddDTO.getAddress());

        if(!existAddress) addressService.add(new AddressAddDto(street , number));
        AddressEntity addressEntity = addressService.getAddressEntityByStreetAndNumber(street , number);
        contactAddDTO.setAddress(addressEntity);

        return Optional
                .ofNullable(contactAddDTO)
                .map(dto -> contactMapper.contactAddDTOToEntity(dto))
                .map(entity -> contactRepository.save(entity))
                .map(entity -> contactMapper.contactEntityToReadDTO(entity))
                .orElse(new ContactReadDTO());
    }

    public ContactReadDTO findById(Integer contactId){
        Optional<ContactEntity> contact = contactRepository.findById(contactId);
        if (contact.isEmpty()) {
            throw new NotFoundException("No se encontro contacto");
        }
        return contactMapper.contactEntityToReadDTO(contact.get());
    }

    public void existsById(Integer id){
        if(!contactRepository.existsById(id)) throw new NotFoundException("No existe contacto por id");
    }
    public void validateName(String name){
        // todo validar si es un string valido
        if(contactRepository.existsByName(name)) throw new AlreadyExistException("Nombre ya existente!");
    }
    public void validatePhone(String phone){
        // todo validar si es un string valido, solo numeros y largo correcto
        if(contactRepository.existsByPhone(phone)) throw new AlreadyExistException("Telefono ya existente!");
    }


    public ContactReadDTO updateById(Integer id, ContactAddDTO contactAddDTO){
        existsById(id);
        ContactEntity contactEntity = contactRepository.getReferenceById(id);

        String name = contactAddDTO.getName();
        if (name != null) {
            validateName(name);
            contactEntity.setName(name);
            contactRepository.save(contactEntity);
        }
        String phone = contactAddDTO.getPhone();
        if (phone != null) {
            validatePhone(phone);
            contactEntity.setPhone(phone);
            contactRepository.save(contactEntity);
        }

        AddressEntity address = contactAddDTO.getAddress();
        if (address != null){
            AddressEntity newAddress = null;
            if(address.getStreet() != null && address.getNumber()!= null){
                newAddress = addressService.getOrCreateAddress(address);
            } else if (address.getStreet() != null){
                address.setNumber(contactEntity.getAddress().getNumber());
                newAddress = addressService.getOrCreateAddress(address);
            } else if (address.getNumber() != null){
                address.setStreet(contactEntity.getAddress().getStreet());
                newAddress = addressService.getOrCreateAddress(address);
            }

            contactEntity.setAddress(newAddress);
            contactRepository.save(contactEntity);
        }
        // contactRepository.save(contactEntity);
        return contactMapper.contactEntityToReadDTO(contactEntity);

    }
    /*

    public void existsById(Integer id){

    }

    public void existsByPhone(String phone){

    }
    public AddressEntity getAddress(AddressReadDTO addressReadDTO){

    }
    public ContactArrayReadDTO findAllByAddress(AddressEntity address, Integer pageNumber,
                                                Integer pageSize, String sortBy){

    }
    public ContactReadDTO deleteById(Integer contactId){

    }
    public void validateContactAddDTO(ContactAddDTO contactAddDTO){

    }

    public List<ContactReadDTO> addMany(ContactAddDTO contactAddDTO[]){

    }

     */
}
