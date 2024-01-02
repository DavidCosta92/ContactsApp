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
    @Query (value = "FROM contact c WHERE (:name IS NULL OR c.name LIKE %:name% )")
    Page<ContactEntity> searchByNameLike (String name, Pageable pageable);
}
