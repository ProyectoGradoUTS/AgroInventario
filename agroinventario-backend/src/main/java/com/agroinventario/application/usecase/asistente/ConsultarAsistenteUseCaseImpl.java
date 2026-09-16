package com.agroinventario.application.usecase.asistente;

import com.agroinventario.application.dto.response.AsistenteConsultaResponse;
import com.agroinventario.application.service.ia.ClasificadorIntencionInventario;
import com.agroinventario.application.service.ia.ClasificadorIntencionInventario.Resultado;
import com.agroinventario.application.service.ia.GeneradorConversacionInventario;
import com.agroinventario.application.service.ia.InventarioContextoIa;
import com.agroinventario.application.service.ia.LlmInventarioClient;
import com.agroinventario.application.service.predictive.ProyeccionInventarioService;
import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.ProyeccionInventario;
import com.agroinventario.domain.ports.input.asistente.ConsultarAsistenteUseCase;
import com.agroinventario.domain.ports.output.AlertaRepositoryPort;
import com.agroinventario.domain.ports.output.HistoricoInventarioRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConsultarAsistenteUseCaseImpl implements ConsultarAsistenteUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final AlertaRepositoryPort alertaRepository;
    private final HistoricoInventarioRepositoryPort historicoRepository;
    private final ProyeccionInventarioService proyeccionInventarioService;
    private final LlmInventarioClient llmInventarioClient;

    public ConsultarAsistenteUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            AlertaRepositoryPort alertaRepository,
            HistoricoInventarioRepositoryPort historicoRepository,
            ProyeccionInventarioService proyeccionInventarioService,
            LlmInventarioClient llmInventarioClient) {
        this.productoRepository = productoRepository;
        this.alertaRepository = alertaRepository;
        this.historicoRepository = historicoRepository;
        this.proyeccionInventarioService = proyeccionInventarioService;
        this.llmInventarioClient = llmInventarioClient;
    }

    @Override
    public AsistenteConsultaResponse ejecutar(String pregunta) {
        List<Producto> productos = productoRepository.findAll();
        List<Producto> activos = InventarioContextoIa.activos(productos);
        List<Alerta> alertas = alertaRepository.findAll();
        List<ProyeccionInventario> proyecciones = proyeccionInventarioService.proyectarTodos(activos);
        long historico = historicoRepository.contarRegistros();
        Resultado clasificacion = ClasificadorIntencionInventario.clasificar(pregunta, activos);

        String conversacional = GeneradorConversacionInventario.responder(
                clasificacion, activos, alertas, proyecciones, historico);

        Optional<String> llm = clasificacion.esSocial()
                ? Optional.empty()
                : llmInventarioClient.responder(pregunta, InventarioContextoIa.construir(productos, alertas, proyecciones));
        String respuesta = llm.filter(texto -> !texto.isBlank()).orElse(conversacional);

        Map<String, Object> metadatos = new LinkedHashMap<>();
        metadatos.put("productos_activos", activos.size());
        metadatos.put("stock_bajo", activos.stream().filter(Producto::stockBajo).count());
        metadatos.put("alertas_pendientes", alertas.stream().filter(a -> a.estado() == EstadoAlerta.PENDIENTE).count());
        metadatos.put("registros_historicos", historico);
        metadatos.put("intenciones", clasificacion.intenciones().stream().map(Enum::name).toList());
        if (clasificacion.producto() != null) {
            metadatos.put("producto", clasificacion.producto().nombre());
        }

        return new AsistenteConsultaResponse(
                respuesta,
                "asistente",
                clasificacion.principal().name(),
                metadatos);
    }
}
