package com.contacts.agenda.services;

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
    public ContactReadDTO add(ContactAddDTO contactAddDTO){

    }
    public List<ContactReadDTO> addMany(ContactAddDTO contactAddDTO[]){

    }
    public ContactAddDTO updateById(Integer id, ContactAddDTO contactAddDTO){

    }

     */
}
