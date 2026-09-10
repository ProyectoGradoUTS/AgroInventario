package com.agroinventario.domain.model;

public record HistoricoInventarioResumen(
        long diasObservados,
        double consumoPromedioDiario,
        double desviacionConsumo,
        int stockPromedio,
        int diasStockout
) {
}
