package com.agroinventario.application.dto.response;

import java.util.List;

public record DashboardResumenResponse(
        long totalProductosEnRiesgo,
        long totalAlertasPendientes,
        long totalProductosCriticos,
        long totalSinMovimiento,
        long productosAgotamiento7d,
        long productosVencimiento30d,
        long recomendacionesUrgentes,
        double valorEnRiesgoVencimiento,
        List<DashboardProductoResponse> productos
) {
}
