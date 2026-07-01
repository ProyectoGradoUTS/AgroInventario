package com.agroinventario.domain.exception;

public class EmailAlreadyExistsException extends BusinessRuleException {

    public EmailAlreadyExistsException(String email) {
        super("El email ya está registrado: " + email);
    }
}
