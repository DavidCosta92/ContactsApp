package com.contacts.agenda.model.repositories;

import com.contacts.agenda.model.entities.ContactEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository <ContactEntity, Integer> {

    Page<ContactEntity> findAllByNameContainsAndPhoneContains (String name,String phone, Pageable pageable);
    Page<ContactEntity> findAllByPhoneContains (String phone, Pageable pageable);
    @Query (value = "FROM contact c WHERE (:name IS NULL OR lower(c.name) LIKE lower('%' || :name || '%'))")  // lower es para que no distinga mayusculas
    Page<ContactEntity> searchByNameLike (String name, Pageable pageable);
    @Query (value = "FROM contact c JOIN Addresses a ON c.address.id = a.id WHERE (:street IS NULL OR lower(a.street) LIKE lower('%' || :street || '%'))") // lower es para que no distinga mayusculas
    Page<ContactEntity> searchByStreetLike(String street, Pageable pageable);

    Boolean existsByName(String name);
    Boolean existsByPhone(String phone);

    ContactEntity findByNameAndPhone (String name,String phone);

}
