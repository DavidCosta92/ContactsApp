package com.contacts.agenda.services;

import com.contacts.agenda.model.dtos.address.AddressAddDto;
import com.contacts.agenda.model.dtos.address.AddressReadDTO;
import com.contacts.agenda.model.dtos.contact.ContactAddDTO;
import com.contacts.agenda.model.dtos.contact.ContactArrayReadDTO;
import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import com.contacts.agenda.model.entities.AddressEntity;
import com.contacts.agenda.model.entities.ContactEntity;
import com.contacts.agenda.model.mappers.ContactMapper;
import com.contacts.agenda.model.repositories.ContactRepository;
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
    public ContactAddDTO findById(Integer contactId){

    }
    public ContactReadDTO deleteById(Integer contactId){

    }
    public void validateContactAddDTO(ContactAddDTO contactAddDTO){

    }

    public List<ContactReadDTO> addMany(ContactAddDTO contactAddDTO[]){

    }
    public ContactAddDTO updateById(Integer id, ContactAddDTO contactAddDTO){

    }

     */
}
