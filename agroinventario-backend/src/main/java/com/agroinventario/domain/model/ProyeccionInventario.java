package com.agroinventario.domain.model;

import java.time.LocalDate;

public record ProyeccionInventario(
        Long productoId,
        String nombre,
        String categoria,
        int stockActual,
        int stockMinimo,
        double consumoPromedio,
        double pendienteConsumo,
        double desviacionConsumo,
        int leadTimeDias,
        long diasHastaAgotamiento,
        LocalDate fechaProyectadaAgotamiento,
        double demandaProyectada7d,
        double demandaProyectada30d,
        int cantidadSugerida,
        String estrategia,
        String estrategiaPersistida,
        String riesgo,
        String mensaje,
        double nivelConfianza,
        String fuenteDatos,
        long diasObservados,
        long diasHastaVencimiento
) {
}
