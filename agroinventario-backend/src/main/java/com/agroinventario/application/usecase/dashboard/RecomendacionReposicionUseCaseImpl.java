package com.agroinventario.application.usecase.dashboard;

import com.agroinventario.application.dto.response.RecomendacionProductoResponse;
import com.agroinventario.application.service.predictive.ProyeccionInventarioService;
import com.agroinventario.domain.model.ProyeccionInventario;
import com.agroinventario.domain.ports.input.dashboard.RecomendacionReposicionUseCase;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RecomendacionReposicionUseCaseImpl implements RecomendacionReposicionUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final ProyeccionInventarioService proyeccionInventarioService;

    public RecomendacionReposicionUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            ProyeccionInventarioService proyeccionInventarioService) {
        this.productoRepository = productoRepository;
        this.proyeccionInventarioService = proyeccionInventarioService;
    }

    @Override
    public List<RecomendacionProductoResponse> ejecutar() {
        return proyeccionInventarioService.proyectarTodos(productoRepository.findAll()).stream()
                .filter(p -> p.cantidadSugerida() > 0)
                .sorted(Comparator.comparingInt(ProyeccionInventario::cantidadSugerida).reversed())
                .map(this::toResponse)
                .toList();
    }

    private RecomendacionProductoResponse toResponse(ProyeccionInventario p) {
        return new RecomendacionProductoResponse(
                p.productoId(),
                p.nombre(),
                p.categoria(),
                p.stockActual(),
                p.stockMinimo(),
                p.consumoPromedio(),
                p.leadTimeDias(),
                p.diasHastaAgotamiento(),
                p.diasHastaVencimiento(),
                p.cantidadSugerida(),
                p.estrategia(),
                p.riesgo(),
                p.mensaje()
        );
    }
}
