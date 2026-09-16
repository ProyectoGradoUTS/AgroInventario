package com.agroinventario.domain.ports.output;

import com.agroinventario.domain.model.HistoricoInventarioResumen;
import com.agroinventario.domain.model.SerieConsumoDiario;
import com.agroinventario.domain.model.TipoMovimiento;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HistoricoInventarioRepositoryPort {

    Optional<HistoricoInventarioResumen> resumirProducto(Long productoId, int dias);

    List<SerieConsumoDiario> listarSerie(Long productoId, int dias);

    long contarRegistros();

    long contarRegistrosProducto(Long productoId);

    void insertarSerieSiAusente(Long productoId, List<SerieConsumoDiario> serie);

    void aplicarMovimientoDelDia(
            Long productoId,
            LocalDate fecha,
            TipoMovimiento tipoMovimiento,
            int cantidad,
            int stockResultante);

    long asegurarDatasetMinimo(long minimoObservaciones);

    void actualizarStockProductosDesdeHistorico();
}
