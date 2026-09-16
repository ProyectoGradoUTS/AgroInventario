package com.agroinventario.application.usecase.dashboard;

import com.agroinventario.application.dto.response.PrediccionProductoResponse;
import com.agroinventario.application.service.predictive.ProyeccionInventarioService;
import com.agroinventario.domain.model.ProyeccionInventario;
import com.agroinventario.domain.ports.input.dashboard.PrediccionInventarioUseCase;
import com.agroinventario.domain.ports.output.PrediccionInventarioPersistenciaPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class PrediccionInventarioUseCaseImpl implements PrediccionInventarioUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final ProyeccionInventarioService proyeccionInventarioService;
    private final PrediccionInventarioPersistenciaPort prediccionPersistenciaPort;

    public PrediccionInventarioUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            ProyeccionInventarioService proyeccionInventarioService,
            PrediccionInventarioPersistenciaPort prediccionPersistenciaPort) {
        this.productoRepository = productoRepository;
        this.proyeccionInventarioService = proyeccionInventarioService;
        this.prediccionPersistenciaPort = prediccionPersistenciaPort;
    }

    @Override
    public List<PrediccionProductoResponse> ejecutar() {
        List<ProyeccionInventario> proyecciones = proyeccionInventarioService
                .proyectarTodos(productoRepository.findAll())
                .stream()
                .sorted(Comparator.comparingLong(ProyeccionInventario::diasHastaAgotamiento)
                        .thenComparing(ProyeccionInventario::nombre))
                .toList();

        prediccionPersistenciaPort.reemplazarPrediccionesDelDia(proyecciones);
        prediccionPersistenciaPort.registrarEjecucionModelo(
                proyecciones.size(),
                (int) proyecciones.stream().mapToLong(ProyeccionInventario::diasObservados).sum(),
                proyeccionInventarioService.inicioVentana(),
                LocalDate.now(),
                mae(proyecciones),
                rmse(proyecciones)
        );

        return proyecciones.stream().map(this::toResponse).toList();
    }

    private PrediccionProductoResponse toResponse(ProyeccionInventario p) {
        return new PrediccionProductoResponse(
                p.productoId(),
                p.nombre(),
                p.categoria(),
                p.stockActual(),
                p.stockMinimo(),
                p.consumoPromedio(),
                p.leadTimeDias(),
                p.diasHastaAgotamiento(),
                p.fechaProyectadaAgotamiento(),
                p.demandaProyectada7d(),
                p.demandaProyectada30d(),
                p.cantidadSugerida(),
                p.estrategia(),
                p.riesgo(),
                p.mensaje(),
                p.nivelConfianza(),
                p.fuenteDatos()
        );
    }

    private double mae(List<ProyeccionInventario> proyecciones) {
        return proyecciones.stream()
                .mapToDouble(p -> p.desviacionConsumo())
                .average()
                .orElse(0);
    }

    private double rmse(List<ProyeccionInventario> proyecciones) {
        return Math.sqrt(proyecciones.stream()
                .mapToDouble(p -> p.desviacionConsumo() * p.desviacionConsumo())
                .average()
                .orElse(0));
    }
}
