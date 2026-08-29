package com.agroinventario.application.dto.response;

public record DashboardProductoResponse(
        Long id,
        String nombre,
        String categoria,
        int stockActual,
        int stockMinimo,
        double consumoPromedio,
        long diasHastaAgotamiento,
        Long diasHastaVencimiento,
        String ultimoMovimiento,
        String estado,
        String riesgo,
        String recomendacion,
        String prioridad
) {
}
