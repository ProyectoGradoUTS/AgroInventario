package com.agroinventario.application.usecase.dashboard;

import com.agroinventario.application.dto.response.DashboardCategoriaResponse;
import com.agroinventario.application.dto.response.DashboardEjecutivoResponse;
import com.agroinventario.application.service.predictive.ProyeccionInventarioService;
import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.ProyeccionInventario;
import com.agroinventario.domain.ports.input.dashboard.DashboardEjecutivoUseCase;
import com.agroinventario.domain.ports.output.AlertaRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardEjecutivoUseCaseImpl implements DashboardEjecutivoUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final AlertaRepositoryPort alertaRepository;
    private final ProyeccionInventarioService proyeccionInventarioService;

    public DashboardEjecutivoUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            AlertaRepositoryPort alertaRepository,
            ProyeccionInventarioService proyeccionInventarioService) {
        this.productoRepository = productoRepository;
        this.alertaRepository = alertaRepository;
        this.proyeccionInventarioService = proyeccionInventarioService;
    }

    @Override
    public DashboardEjecutivoResponse ejecutar() {
        List<Producto> productos = productoRepository.findAll();
        List<Alerta> alertasPendientes = alertaRepository.findByEstado(EstadoAlerta.PENDIENTE);
        Map<Long, ProyeccionInventario> proyecciones = proyeccionInventarioService.proyectarTodos(productos).stream()
                .collect(Collectors.toMap(ProyeccionInventario::productoId, p -> p, (a, b) -> a));

        Map<String, List<Producto>> agrupados = productos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.categoriaNombre() == null || p.categoriaNombre().isBlank() ? "Sin categoría" : p.categoriaNombre(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<DashboardCategoriaResponse> categorias = agrupados.entrySet().stream()
                .map(entry -> resumirCategoria(entry.getKey(), entry.getValue(), proyecciones))
                .sorted(Comparator.comparingDouble(DashboardCategoriaResponse::riesgoPromedio).reversed())
                .toList();

        long totalProductosEnRiesgo = productos.stream()
                .filter(p -> p.stockActual() <= p.stockMinimo() || p.diasHastaVencimiento() <= 30 && p.tieneFechaVencimiento())
                .count();

        long totalProductosCriticos = productos.stream()
                .filter(p -> p.stockActual() <= 0 || (p.tieneFechaVencimiento() && p.diasHastaVencimiento() <= 7))
                .count();

        String categoriaMasRiesgosa = categorias.isEmpty() ? "Sin categoría" : categorias.get(0).categoria();

        return new DashboardEjecutivoResponse(
                productos.size(),
                categorias.size(),
                alertasPendientes.size(),
                totalProductosCriticos,
                totalProductosEnRiesgo,
                categoriaMasRiesgosa,
                categorias
        );
    }

    private DashboardCategoriaResponse resumirCategoria(
            String categoria,
            List<Producto> productos,
            Map<Long, ProyeccionInventario> proyecciones) {
        long totalProductos = productos.size();
        long stockBajo = productos.stream().filter(p -> p.stockActual() <= p.stockMinimo()).count();
        long criticos = productos.stream().filter(p -> p.stockActual() <= 0 || (p.tieneFechaVencimiento() && p.diasHastaVencimiento() <= 7)).count();
        long enRiesgo = productos.stream().filter(p -> p.stockActual() <= p.stockMinimo() || (p.tieneFechaVencimiento() && p.diasHastaVencimiento() <= 30)).count();

        double consumoPromedio = productos.stream()
                .mapToDouble(p -> {
                    ProyeccionInventario proyeccion = proyecciones.get(p.id());
                    return proyeccion != null ? proyeccion.consumoPromedio() : Math.max(0.5, p.stockMinimo() / 7.0);
                })
                .average()
                .orElse(0.0);

        double riesgoPromedio = productos.stream()
                .mapToDouble(p -> {
                    ProyeccionInventario proyeccion = proyecciones.get(p.id());
                    if (proyeccion != null && "CRITICO".equals(proyeccion.riesgo())) {
                        return 100.0;
                    }
                    if (proyeccion != null && "ALTO".equals(proyeccion.riesgo())) {
                        return 70.0;
                    }
                    if (p.stockActual() <= 0 || (p.tieneFechaVencimiento() && p.diasHastaVencimiento() <= 7)) {
                        return 100.0;
                    }
                    if (p.stockActual() <= p.stockMinimo() || (p.tieneFechaVencimiento() && p.diasHastaVencimiento() <= 30)) {
                        return 70.0;
                    }
                    return 25.0;
                })
                .average()
                .orElse(0.0);

        String nivelRiesgo = riesgoPromedio >= 70 ? "ALTO" : riesgoPromedio >= 40 ? "MEDIO" : "BAJO";

        return new DashboardCategoriaResponse(
                categoria,
                totalProductos,
                enRiesgo,
                criticos,
                stockBajo,
                redondear(consumoPromedio),
                redondear(riesgoPromedio),
                nivelRiesgo
        );
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
