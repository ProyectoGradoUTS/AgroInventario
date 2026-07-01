package com.agroinventario.domain.exception;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String recurso, Long id) {
        super(recurso + " con id " + id + " no encontrado");
    }

    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}
