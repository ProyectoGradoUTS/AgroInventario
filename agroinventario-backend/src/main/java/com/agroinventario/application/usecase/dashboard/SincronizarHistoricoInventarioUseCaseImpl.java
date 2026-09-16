package com.agroinventario.application.usecase.dashboard;

import com.agroinventario.application.service.predictive.ProyeccionInventarioService;
import com.agroinventario.domain.ports.input.dashboard.SincronizarHistoricoInventarioUseCase;
import com.agroinventario.domain.ports.output.HistoricoInventarioRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SincronizarHistoricoInventarioUseCaseImpl implements SincronizarHistoricoInventarioUseCase {

    private static final Logger log = LoggerFactory.getLogger(SincronizarHistoricoInventarioUseCaseImpl.class);
    private static final long DATASET_MINIMO = 150_000;

    private final ProductoRepositoryPort productoRepository;
    private final HistoricoInventarioRepositoryPort historicoRepository;
    private final ProyeccionInventarioService proyeccionInventarioService;

    public SincronizarHistoricoInventarioUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            HistoricoInventarioRepositoryPort historicoRepository,
            ProyeccionInventarioService proyeccionInventarioService) {
        this.productoRepository = productoRepository;
        this.historicoRepository = historicoRepository;
        this.proyeccionInventarioService = proyeccionInventarioService;
    }

    @Override
    public int ejecutar() {
        long registros = historicoRepository.asegurarDatasetMinimo(DATASET_MINIMO);
        historicoRepository.actualizarStockProductosDesdeHistorico();
        proyeccionInventarioService.invalidarCache();
        var productos = productoRepository.findAll();
        proyeccionInventarioService.asegurarHistorico(productos);
        log.info("Histórico de inventario listo: {} observaciones diarias para {} productos", registros, productos.size());
        return productos.size();
    }
}
