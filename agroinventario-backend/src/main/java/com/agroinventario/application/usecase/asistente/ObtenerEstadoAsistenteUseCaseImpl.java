package com.agroinventario.application.usecase.asistente;

import com.agroinventario.application.dto.response.AsistenteEstadoResponse;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.ports.input.asistente.ObtenerEstadoAsistenteUseCase;
import com.agroinventario.domain.ports.output.AlertaRepositoryPort;
import com.agroinventario.domain.ports.output.HistoricoInventarioRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import com.agroinventario.infrastructure.config.IaProperties;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObtenerEstadoAsistenteUseCaseImpl implements ObtenerEstadoAsistenteUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final AlertaRepositoryPort alertaRepository;
    private final HistoricoInventarioRepositoryPort historicoRepository;
    private final IaProperties iaProperties;

    public ObtenerEstadoAsistenteUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            AlertaRepositoryPort alertaRepository,
            HistoricoInventarioRepositoryPort historicoRepository,
            IaProperties iaProperties) {
        this.productoRepository = productoRepository;
        this.alertaRepository = alertaRepository;
        this.historicoRepository = historicoRepository;
        this.iaProperties = iaProperties;
    }

    @Override
    public AsistenteEstadoResponse ejecutar() {
        List<Producto> activos = productoRepository.findByEstado(EstadoGeneral.ACTIVO);
        long stockBajo = activos.stream().filter(Producto::stockBajo).count();
        long alertas = alertaRepository.findByEstado(EstadoAlerta.PENDIENTE).size();
        long historico = historicoRepository.contarRegistros();
        String mensaje = "Hola, soy el asistente del inventario. Puedo decirle cómo está el stock, qué se va a agotar, qué vence pronto y qué conviene pedir.";
        return new AsistenteEstadoResponse(
                true,
                mensaje,
                iaProperties.llmHabilitado(),
                activos.size(),
                stockBajo,
                alertas,
                historico);
    }
}
