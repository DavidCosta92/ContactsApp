package com.contacts.agenda.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "contact")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ContactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String phone;

    @ManyToOne (cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "Addresses_id")
    private AddressEntity address;

}
