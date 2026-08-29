package com.agroinventario.application.dto.response;

import java.util.List;

public record DashboardEjecutivoResponse(
        long totalProductos,
        long totalCategorias,
        long alertasPendientes,
        long productosCriticos,
        long productosEnRiesgo,
        String categoriaMasRiesgosa,
        List<DashboardCategoriaResponse> categorias
) {
}
