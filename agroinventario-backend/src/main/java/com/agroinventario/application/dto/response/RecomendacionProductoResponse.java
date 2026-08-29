package com.agroinventario.application.dto.response;

public record RecomendacionProductoResponse(
        Long id,
        String nombre,
        String categoria,
        int stockActual,
        int stockMinimo,
        double consumoPromedio,
        int leadTimeDias,
        long diasHastaAgotamiento,
        long diasHastaVencimiento,
        int cantidadSugerida,
        String estrategia,
        String riesgo,
        String mensaje
) {
}
