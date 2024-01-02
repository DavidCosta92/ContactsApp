package com.contacts.agenda.model.dtos.contact;

import com.contacts.agenda.model.entities.AddressEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactReadDTO {
    private Integer id;
    private String name;
    private String phone;
    private AddressEntity address;
}
