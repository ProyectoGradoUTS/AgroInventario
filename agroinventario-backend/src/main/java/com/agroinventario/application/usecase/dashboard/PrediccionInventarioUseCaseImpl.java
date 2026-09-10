package com.agroinventario.application.usecase.dashboard;

import com.agroinventario.application.dto.response.PrediccionProductoResponse;
import com.agroinventario.application.service.predictive.DatasetSinteticoPrediccionService;
import com.agroinventario.domain.model.HistoricoInventarioResumen;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.ports.input.dashboard.PrediccionInventarioUseCase;
import com.agroinventario.domain.ports.output.HistoricoInventarioRepositoryPort;
import com.agroinventario.domain.ports.output.MovimientoInventarioRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PrediccionInventarioUseCaseImpl implements PrediccionInventarioUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final MovimientoInventarioRepositoryPort movimientoRepository;
    private final HistoricoInventarioRepositoryPort historicoRepository;
    private final DatasetSinteticoPrediccionService datasetSinteticoPrediccionService;

    public PrediccionInventarioUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            MovimientoInventarioRepositoryPort movimientoRepository,
            HistoricoInventarioRepositoryPort historicoRepository,
            DatasetSinteticoPrediccionService datasetSinteticoPrediccionService) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.historicoRepository = historicoRepository;
        this.datasetSinteticoPrediccionService = datasetSinteticoPrediccionService;
    }

    @Override
    public List<PrediccionProductoResponse> ejecutar() {
        return productoRepository.findAll().stream()
                .map(this::toPrediccion)
                .sorted(Comparator.comparingLong(PrediccionProductoResponse::diasHastaAgotamiento).thenComparing(PrediccionProductoResponse::nombre))
                .toList();
    }

    private PrediccionProductoResponse toPrediccion(Producto producto) {
        HistoricoInventarioResumen resumenHistorico = historicoRepository
                .resumirProducto(producto.id(), 90)
                .orElse(null);
        double consumoPromedio = calcularConsumoPromedio(producto, resumenHistorico);
        int leadTimeDias = leadTimeDias(producto);
        long diasHastaAgotamiento = producto.stockActual() <= 0 ? 0 : Math.max(0, Math.round((double) producto.stockActual() / Math.max(consumoPromedio, 0.5)));
        LocalDate fechaProyectadaAgotamiento = diasHastaAgotamiento == 0 ? LocalDate.now() : LocalDate.now().plusDays(diasHastaAgotamiento);
        double demanda7d = consumoPromedio * 7;
        double demanda30d = consumoPromedio * 30;
        int cantidadSugerida = Math.max(0, (int) Math.ceil((consumoPromedio * leadTimeDias) + producto.stockMinimo() - producto.stockActual()));

        String estrategia = seleccionarEstrategia(producto, consumoPromedio, leadTimeDias, diasHastaAgotamiento, cantidadSugerida);
        String riesgo = seleccionarRiesgo(
                producto,
                diasHastaAgotamiento,
                consumoPromedio,
                leadTimeDias,
                resumenHistorico
        );
        String mensaje = construirMensaje(producto, diasHastaAgotamiento, cantidadSugerida, estrategia, riesgo);

        return new PrediccionProductoResponse(
                producto.id(),
                producto.nombre(),
                producto.categoriaNombre() == null ? "Sin categoría" : producto.categoriaNombre(),
                producto.stockActual(),
                producto.stockMinimo(),
                redondear(consumoPromedio),
                leadTimeDias,
                diasHastaAgotamiento,
                fechaProyectadaAgotamiento,
                redondear(demanda7d),
                redondear(demanda30d),
                cantidadSugerida,
                estrategia,
                riesgo,
                mensaje
        );
    }

    private double calcularConsumoPromedio(
            Producto producto,
            HistoricoInventarioResumen resumenHistorico) {
        if (resumenHistorico != null && resumenHistorico.consumoPromedioDiario() > 0.0) {
            return consumoConMargenDeVariabilidad(resumenHistorico);
        }

        if (movimientoRepository.existsByProductoId(producto.id())) {
            double promedio = movimientoRepository.findByProductoId(producto.id()).stream()
                    .mapToDouble(m -> Math.abs(m.cantidad()))
                    .average()
                    .orElse(0.0);
            if (promedio > 0.0) {
                return Math.max(0.2, promedio);
            }
        }

        var historicoSintetico = datasetSinteticoPrediccionService.generarParaProducto(producto);
        return historicoSintetico.stream()
                .mapToDouble(h -> h.consumoReal())
                .average()
                .orElse(Math.max(0.3, producto.stockMinimo() / 7.0));
    }

    private double consumoConMargenDeVariabilidad(HistoricoInventarioResumen resumen) {
        double promedio = resumen.consumoPromedioDiario();
        double variabilidad = resumen.desviacionConsumo();
        return Math.max(0.2, promedio + (variabilidad * 0.5));
    }

    private int leadTimeDias(Producto producto) {
        String categoria = producto.categoriaNombre() == null ? "" : producto.categoriaNombre().trim().toLowerCase();
        return switch (categoria) {
            case "medicina" -> 8;
            case "semillas" -> 15;
            case "toxicológica", "toxicologica" -> 12;
            case "herbicidas" -> 8;
            case "insecticidas" -> 10;
            case "fungicidas" -> 8;
            case "alimentos" -> 8;
            default -> 10;
        };
    }

    private String seleccionarEstrategia(Producto producto, double consumoPromedio, int leadTimeDias, long diasHastaAgotamiento, int cantidadSugerida) {
        if (producto.stockActual() <= producto.stockMinimo() || cantidadSugerida > 0 && producto.stockActual() <= (consumoPromedio * leadTimeDias) + producto.stockMinimo()) {
            return "REPOSICION_MINIMO";
        }
        if (diasHastaAgotamiento <= 7) {
            return "STOCK_BAJO_DEMANDA";
        }
        if (cantidadSugerida > 0) {
            return "TOP_OFF_LEAD_TIME";
        }
        return "MONITOREO";
    }

    private String seleccionarRiesgo(
            Producto producto,
            long diasHastaAgotamiento,
            double consumoPromedio,
            int leadTimeDias,
            HistoricoInventarioResumen resumenHistorico) {
        if (resumenHistorico != null && resumenHistorico.diasStockout() > 0) {
            return resumenHistorico.diasStockout() >= 7 ? "ALTO" : "MEDIO";
        }
        if (producto.stockActual() <= producto.stockMinimo() || diasHastaAgotamiento <= 7) {
            return "ALTO";
        }
        if (producto.stockActual() <= (consumoPromedio * leadTimeDias) + producto.stockMinimo()) {
            return "MEDIO";
        }
        return "BAJO";
    }

    private String construirMensaje(Producto producto, long diasHastaAgotamiento, int cantidadSugerida, String estrategia, String riesgo) {
        if ("REPOSICION_MINIMO".equals(estrategia)) {
            return "Se recomienda reabastecer de inmediato para cubrir el stock mínimo y evitar rupturas.";
        }
        if ("STOCK_BAJO_DEMANDA".equals(estrategia)) {
            return "El consumo proyectado es superior al inventario disponible y requiere una reposición prioritaria.";
        }
        if ("TOP_OFF_LEAD_TIME".equals(estrategia)) {
            return "El producto debe recibir un top-off para cubrir el lead time y mantener la continuidad operativa.";
        }
        if ("ALTO".equals(riesgo)) {
            return "El riesgo es alto; se recomienda seguimiento cercano y revisión de compra.";
        }
        return "El producto mantiene una tendencia estable y requiere monitoreo periódico.";
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
