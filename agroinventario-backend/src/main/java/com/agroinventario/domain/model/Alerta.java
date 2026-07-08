package com.agroinventario.domain.model;

import java.time.LocalDateTime;

public record Alerta(
        Long id,
        Long productoId,
        String productoNombre,
        TipoAlerta tipoAlerta,
        String mensaje,
        EstadoAlerta estado,
        LocalDateTime fechaGeneracion
) {

    public Alerta conEstado(EstadoAlerta nuevoEstado) {
        return new Alerta(id, productoId, productoNombre, tipoAlerta, mensaje, nuevoEstado, fechaGeneracion);
    }

    public boolean puedeTransicionarA(EstadoAlerta nuevoEstado) {
        if (estado == nuevoEstado) {
            return true;
        }
        return switch (estado) {
            case PENDIENTE -> nuevoEstado == EstadoAlerta.LEIDA || nuevoEstado == EstadoAlerta.RESUELTA;
            case LEIDA -> nuevoEstado == EstadoAlerta.RESUELTA;
            case RESUELTA -> false;
        };
    }
}
