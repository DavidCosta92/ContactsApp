package com.contacts.agenda.model.dtos.contact;

import com.contacts.agenda.model.entities.AddressEntity;
import jakarta.validation.constraints.Max;
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
public class ContactAddDTO {
    // private Integer id;

    @NotNull(message = "Nombre no puede ser nulo")
    @Size(min=2, max=30, message = "Nombre debe tener entre 2 y 30 caracteres")
    private String name;

    @NotNull(message = "Telefono no puede ser nulo")
    @Size(min=9, max=14, message = "Telefono debe tener entre 9 y 14 caracteres")
    private String phone;

    @NotNull(message = "Direccion no puede ser nula")
    private AddressEntity address;
}
