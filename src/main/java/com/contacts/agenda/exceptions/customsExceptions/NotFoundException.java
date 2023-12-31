package com.contacts.agenda.exceptions.customsExceptions;

public class NotFoundException extends RuntimeException{
    public NotFoundException (String msg){
        super(msg);
    }
}
