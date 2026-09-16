package com.agroinventario.application.dto.response;

import java.time.LocalDate;

public record PrediccionProductoResponse(
        Long id,
        String nombre,
        String categoria,
        int stockActual,
        int stockMinimo,
        double consumoPromedio,
        int leadTimeDias,
        long diasHastaAgotamiento,
        LocalDate fechaProyectadaAgotamiento,
        double demandaProyectada7d,
        double demandaProyectada30d,
        int cantidadSugerida,
        String estrategia,
        String riesgo,
        String mensaje,
        double nivelConfianza,
        String fuenteDatos
) {
}
