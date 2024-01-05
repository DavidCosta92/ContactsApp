package com.contacts.agenda.model.dtos.contact;

import com.contacts.agenda.model.entities.AddressEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactUpdateDTO {
    private Integer id;
    private String name;
    private String phone;
    private AddressEntity address;
}
