package com.agroinventario.application.usecase.dashboard;

import com.agroinventario.application.dto.response.RecomendacionProductoResponse;
import com.agroinventario.application.service.predictive.DatasetSinteticoPrediccionService;
import com.agroinventario.domain.model.MovimientoInventario;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.ports.input.dashboard.RecomendacionReposicionUseCase;
import com.agroinventario.domain.ports.output.MovimientoInventarioRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RecomendacionReposicionUseCaseImpl implements RecomendacionReposicionUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final MovimientoInventarioRepositoryPort movimientoRepository;
    private final DatasetSinteticoPrediccionService datasetSinteticoPrediccionService;

    public RecomendacionReposicionUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            MovimientoInventarioRepositoryPort movimientoRepository,
            DatasetSinteticoPrediccionService datasetSinteticoPrediccionService) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.datasetSinteticoPrediccionService = datasetSinteticoPrediccionService;
    }

    @Override
    public List<RecomendacionProductoResponse> ejecutar() {
        return productoRepository.findAll().stream()
                .map(this::toRecomendacion)
                .sorted(Comparator.comparingInt(RecomendacionProductoResponse::cantidadSugerida).reversed())
                .toList();
    }

    private RecomendacionProductoResponse toRecomendacion(Producto producto) {
        List<MovimientoInventario> movimientos = movimientoRepository.findByProductoId(producto.id());
        double consumoPromedio = calcularConsumoPromedio(producto, movimientos);
        int leadTimeDias = leadTimeDias(producto);
        long diasHastaAgotamiento = producto.stockActual() <= 0 ? 0 : Math.max(0, Math.round((double) producto.stockActual() / Math.max(consumoPromedio, 0.5)));
        long diasHastaVencimiento = producto.diasHastaVencimiento();
        double stockSeguridad = Math.max(producto.stockMinimo(), consumoPromedio * leadTimeDias);
        int cantidadSugerida = Math.max(0, (int) Math.ceil(stockSeguridad - producto.stockActual()));

        String estrategia = estrategia(producto, consumoPromedio, leadTimeDias, diasHastaAgotamiento, diasHastaVencimiento, cantidadSugerida);
        String riesgo = riesgo(producto, consumoPromedio, leadTimeDias, diasHastaAgotamiento, diasHastaVencimiento);
        String mensaje = mensaje(producto, consumoPromedio, leadTimeDias, cantidadSugerida, estrategia);

        return new RecomendacionProductoResponse(
                producto.id(),
                producto.nombre(),
                producto.categoriaNombre() == null ? "Sin categoría" : producto.categoriaNombre(),
                producto.stockActual(),
                producto.stockMinimo(),
                redondear(consumoPromedio),
                leadTimeDias,
                diasHastaAgotamiento,
                diasHastaVencimiento == Long.MAX_VALUE ? -1 : diasHastaVencimiento,
                cantidadSugerida,
                estrategia,
                riesgo,
                mensaje
        );
    }

    private double calcularConsumoPromedio(Producto producto, List<MovimientoInventario> movimientos) {
        if (movimientos != null && !movimientos.isEmpty()) {
            double promedio = movimientos.stream()
                    .mapToDouble(m -> Math.abs(m.cantidad()))
                    .average()
                    .orElse(0.0);
            if (promedio > 0.0) {
                return Math.max(0.2, promedio);
            }
        }

        var historico = datasetSinteticoPrediccionService.generarParaProducto(producto);
        double promedioHistorico = historico.stream()
                .mapToDouble(x -> x.consumoReal())
                .average()
                .orElse(Math.max(0.3, producto.stockMinimo() / 7.0));

        return Math.max(0.2, promedioHistorico);
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

    private String estrategia(Producto producto, double consumoPromedio, int leadTimeDias, long diasHastaAgotamiento, long diasHastaVencimiento, int cantidadSugerida) {
        if (producto.stockActual() <= producto.stockMinimo() || cantidadSugerida > 0 && producto.stockActual() <= (consumoPromedio * leadTimeDias) + producto.stockMinimo()) {
            return "REPOSICION_MINIMO";
        }
        if (diasHastaAgotamiento <= 7) {
            return "STOCK_BAJO_DEMANDA";
        }
        if (diasHastaVencimiento >= 0 && diasHastaVencimiento <= 30) {
            return "ROTACION_VENCIMIENTO";
        }
        if (cantidadSugerida > 0) {
            return "TOP_OFF_LEAD_TIME";
        }
        return "MONITOREO";
    }

    private String riesgo(Producto producto, double consumoPromedio, int leadTimeDias, long diasHastaAgotamiento, long diasHastaVencimiento) {
        if (producto.stockActual() <= producto.stockMinimo() || diasHastaAgotamiento <= 7) {
            return "ALTO";
        }
        if (diasHastaVencimiento >= 0 && diasHastaVencimiento <= 30) {
            return "ALTO";
        }
        if (producto.stockActual() <= (consumoPromedio * leadTimeDias) + producto.stockMinimo()) {
            return "MEDIO";
        }
        return "BAJO";
    }

    private String mensaje(Producto producto, double consumoPromedio, int leadTimeDias, int cantidadSugerida, String estrategia) {
        if ("REPOSICION_MINIMO".equals(estrategia)) {
            return "La cobertura actual está por debajo del mínimo y requiere reposición inmediata para evitar faltantes.";
        }
        if ("STOCK_BAJO_DEMANDA".equals(estrategia)) {
            return "El consumo proyectado supera la cobertura disponible; se recomienda reabastecer antes de que se agote el producto.";
        }
        if ("ROTACION_VENCIMIENTO".equals(estrategia)) {
            return "Hay riesgo de vencimiento próximo. Priorizar rotación y revisar la fecha de caducidad.";
        }
        if (cantidadSugerida > 0) {
            return "Se recomienda un top-off para cubrir el lead time y mantener el stock operativo sin rupturas.";
        }
        return "El producto mantiene una cobertura estable y requiere seguimiento periódico.";
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
