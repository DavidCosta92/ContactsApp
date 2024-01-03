package com.contacts.agenda.model.repositories;

import com.contacts.agenda.model.entities.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity , Integer> {

    Optional<AddressEntity> findByStreetAndNumber(String street , String number);
}
