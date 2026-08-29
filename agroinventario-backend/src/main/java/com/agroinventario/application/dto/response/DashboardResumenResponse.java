package com.agroinventario.application.dto.response;

import java.util.List;

public record DashboardResumenResponse(
        long totalProductosEnRiesgo,
        long totalAlertasPendientes,
        long totalProductosCriticos,
        long totalSinMovimiento,
        List<DashboardProductoResponse> productos
) {
}
