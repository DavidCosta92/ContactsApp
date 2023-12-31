package com.contacts.agenda.auth.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class RegisterRequest {
    String username;
    String password1;
    String password2;
    String firstName;
    String lastName;
    String dni;
    String phone;
    String email;
}
