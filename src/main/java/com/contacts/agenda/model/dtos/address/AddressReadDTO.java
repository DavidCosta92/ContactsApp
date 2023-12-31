package com.contacts.agenda.model.dtos.address;

import com.contacts.agenda.model.entities.AddressEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressReadDTO {
    private Integer id;
    private String street;
    private String number;
}
