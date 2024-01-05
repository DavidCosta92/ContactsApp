package com.contacts.agenda.model.dtos.address;

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
public class AddressAddDto {
    @NotNull(message = "Calle no puede ser nulo")
    @Size(min=2, max=30, message = "Calle debe tener entre 2 y 30 caracteres")
    private String street;

    @NotNull(message = "Numero no puede ser nulo")
    @Size(min=2, max=5, message = "Numero debe tener entre 2 y 5 caracteres")
    private String number;
}
