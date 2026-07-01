package com.agroinventario.domain.ports.output;

/**
 * Puerto para obtener el usuario autenticado en la petición actual.
 */
public interface CurrentUserPort {

    String getEmail();

    Long getUserId();

    boolean hasRole(String role);
}
