package com.agroinventario.application.usecase.dashboard;

import com.agroinventario.application.dto.response.DashboardProductoResponse;
import com.agroinventario.application.dto.response.DashboardResumenResponse;
import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.MovimientoInventario;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.ports.input.dashboard.DashboardResumenUseCase;
import com.agroinventario.domain.ports.output.AlertaRepositoryPort;
import com.agroinventario.domain.ports.output.MovimientoInventarioRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class DashboardResumenUseCaseImpl implements DashboardResumenUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final AlertaRepositoryPort alertaRepository;
    private final MovimientoInventarioRepositoryPort movimientoRepository;

    public DashboardResumenUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            AlertaRepositoryPort alertaRepository,
            MovimientoInventarioRepositoryPort movimientoRepository) {
        this.productoRepository = productoRepository;
        this.alertaRepository = alertaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    public DashboardResumenResponse ejecutar() {
        List<Producto> productos = productoRepository.findAll();
        List<Alerta> alertasPendientes = alertaRepository.findByEstado(EstadoAlerta.PENDIENTE);

        List<DashboardProductoResponse> items = productos.stream()
                .map(this::toDashboardProducto)
                .sorted((a, b) -> Integer.compare(prioridadValor(b.prioridad()), prioridadValor(a.prioridad())))
                .toList();

        long totalProductosEnRiesgo = items.stream().filter(item -> !"NORMAL".equals(item.estado())).count();
        long totalProductosCriticos = items.stream().filter(item -> "CRITICO".equals(item.estado())).count();
        long totalSinMovimiento = items.stream().filter(item -> "SIN_MOVIMIENTO".equals(item.estado())).count();

        return new DashboardResumenResponse(
                totalProductosEnRiesgo,
                alertasPendientes.size(),
                totalProductosCriticos,
                totalSinMovimiento,
                items
        );
    }

    private DashboardProductoResponse toDashboardProducto(Producto producto) {
        List<MovimientoInventario> movimientos = movimientoRepository.findByProductoId(producto.id());
        String ultimoMovimiento = movimientos.isEmpty() ? "Sin movimientos" : movimientos.stream()
                .map(MovimientoInventario::fechaMovimiento)
                .max(java.util.Comparator.naturalOrder())
                .map(Object::toString)
                .orElse("Sin movimientos");

        double consumoPromedio = consumoPromedio(producto);
        long diasHastaAgotamiento = producto.stockActual() <= 0 ? 0 : Math.max(0, Math.round((double) producto.stockActual() / consumoPromedio));
        Long diasHastaVencimiento = producto.fechaVencimiento() == null ? null : Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), producto.fechaVencimiento()));

        String estado = determinarEstado(producto, diasHastaAgotamiento, diasHastaVencimiento, movimientos);
        String riesgo = determinarRiesgo(producto, diasHastaAgotamiento, diasHastaVencimiento);
        String recomendacion = construirRecomendacion(producto, diasHastaAgotamiento, diasHastaVencimiento);
        String prioridad = prioridad(producto, diasHastaAgotamiento, diasHastaVencimiento);

        return new DashboardProductoResponse(
                producto.id(),
                producto.nombre(),
                producto.categoriaNombre() == null ? "Sin categoría" : producto.categoriaNombre(),
                producto.stockActual(),
                producto.stockMinimo(),
                consumoPromedio,
                diasHastaAgotamiento,
                diasHastaVencimiento,
                ultimoMovimiento,
                estado,
                riesgo,
                recomendacion,
                prioridad
        );
    }

    private double consumoPromedio(Producto producto) {
        if (producto.stockMinimo() <= 0) {
            return 1.0;
        }
        return Math.max(0.5, producto.stockMinimo() / 7.0);
    }

    private String determinarEstado(Producto producto, long diasHastaAgotamiento, Long diasHastaVencimiento, List<MovimientoInventario> movimientos) {
        if (producto.stockActual() <= 0) {
            return "CRITICO";
        }
        if (producto.stockActual() <= producto.stockMinimo()) {
            return "BAJO";
        }
        if (diasHastaVencimiento != null && diasHastaVencimiento <= 30) {
            return "VENCIMIENTO_PROXIMO";
        }
        if (movimientos.isEmpty() || diasSinMovimiento(movimientos) > 30) {
            return "SIN_MOVIMIENTO";
        }
        if (diasHastaAgotamiento <= 7) {
            return "CRITICO";
        }
        return "NORMAL";
    }

    private String determinarRiesgo(Producto producto, long diasHastaAgotamiento, Long diasHastaVencimiento) {
        if (producto.stockActual() <= producto.stockMinimo() || diasHastaAgotamiento <= 7) {
            return "ALTO";
        }
        if (diasHastaVencimiento != null && diasHastaVencimiento <= 30) {
            return "ALTO";
        }
        if (diasHastaVencimiento != null && diasHastaVencimiento <= 90) {
            return "MEDIO";
        }
        return "BAJO";
    }

    private String construirRecomendacion(Producto producto, long diasHastaAgotamiento, Long diasHastaVencimiento) {
        if (producto.stockActual() <= producto.stockMinimo()) {
            return "Reponer hasta el nivel mínimo configurado.";
        }
        if (diasHastaAgotamiento <= 7) {
            return "Solicitar reposición inmediata para evitar agotamiento.";
        }
        if (diasHastaVencimiento != null && diasHastaVencimiento <= 30) {
            return "Priorizar revisión de vencimiento y rotación.";
        }
        return "Mantener nivel actual y monitorear consumo.";
    }

    private String prioridad(Producto producto, long diasHastaAgotamiento, Long diasHastaVencimiento) {
        if (producto.stockActual() <= producto.stockMinimo() || diasHastaAgotamiento <= 7) {
            return "ALTA";
        }
        if (diasHastaVencimiento != null && diasHastaVencimiento <= 30) {
            return "ALTA";
        }
        if (diasHastaVencimiento != null && diasHastaVencimiento <= 90) {
            return "MEDIA";
        }
        return "BAJA";
    }

    private long diasSinMovimiento(List<MovimientoInventario> movimientos) {
        if (movimientos.isEmpty()) {
            return Long.MAX_VALUE;
        }
        var ultimaFecha = movimientos.stream()
                .map(MovimientoInventario::fechaMovimiento)
                .max(java.util.Comparator.naturalOrder())
                .orElse(LocalDateTime.now());
        return ChronoUnit.DAYS.between(ultimaFecha.toLocalDate(), LocalDate.now());
    }

    private int prioridadValor(String prioridad) {
        return switch (prioridad) {
            case "ALTA" -> 3;
            case "MEDIA" -> 2;
            default -> 1;
        };
    }
}
