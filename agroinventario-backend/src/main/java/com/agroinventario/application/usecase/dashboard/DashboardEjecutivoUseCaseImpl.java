package com.agroinventario.application.usecase.dashboard;

import com.agroinventario.application.dto.response.DashboardCategoriaResponse;
import com.agroinventario.application.dto.response.DashboardEjecutivoResponse;
import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.ports.input.dashboard.DashboardEjecutivoUseCase;
import com.agroinventario.domain.ports.output.AlertaRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardEjecutivoUseCaseImpl implements DashboardEjecutivoUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final AlertaRepositoryPort alertaRepository;

    public DashboardEjecutivoUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            AlertaRepositoryPort alertaRepository) {
        this.productoRepository = productoRepository;
        this.alertaRepository = alertaRepository;
    }

    @Override
    public DashboardEjecutivoResponse ejecutar() {
        List<Producto> productos = productoRepository.findAll();
        List<Alerta> alertasPendientes = alertaRepository.findByEstado(EstadoAlerta.PENDIENTE);

        Map<String, List<Producto>> agrupados = productos.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        p -> p.categoriaNombre() == null || p.categoriaNombre().isBlank() ? "Sin categoría" : p.categoriaNombre(),
                        LinkedHashMap::new,
                        java.util.stream.Collectors.toList()
                ));

        List<DashboardCategoriaResponse> categorias = agrupados.entrySet().stream()
                .map(entry -> resumirCategoria(entry.getKey(), entry.getValue()))
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

    private DashboardCategoriaResponse resumirCategoria(String categoria, List<Producto> productos) {
        long totalProductos = productos.size();
        long stockBajo = productos.stream().filter(p -> p.stockActual() <= p.stockMinimo()).count();
        long criticos = productos.stream().filter(p -> p.stockActual() <= 0 || (p.tieneFechaVencimiento() && p.diasHastaVencimiento() <= 7)).count();
        long enRiesgo = productos.stream().filter(p -> p.stockActual() <= p.stockMinimo() || (p.tieneFechaVencimiento() && p.diasHastaVencimiento() <= 30)).count();

        double consumoPromedio = productos.stream()
                .mapToDouble(p -> Math.max(0.5, p.stockMinimo() / 7.0))
                .average()
                .orElse(0.0);

        double riesgoPromedio = productos.stream()
                .mapToDouble(p -> {
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
