package com.contacts.agenda.exceptions.customsExceptions;

public class InvalidValueException extends RuntimeException{
    public InvalidValueException(String message) {
        super(message);
    }
}
