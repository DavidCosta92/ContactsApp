package com.contacts.agenda.auth.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    CREATE_CONTACT("CREATE_CONTACT"),
    READ_ALL("READ_ALL"),
    EDIT_ALL("EDIT_ALL"),
    DELETE_ALL("DELETE_ALL");
    @Getter
    private final String permission;
}
