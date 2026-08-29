package com.agroinventario.application.dto.response;

public record DashboardCategoriaResponse(
        String categoria,
        long totalProductos,
        long productosEnRiesgo,
        long productosCriticos,
        long stockBajo,
        double consumoPromedio,
        double riesgoPromedio,
        String nivelRiesgo
) {
}
