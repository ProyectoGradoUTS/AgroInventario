package com.agroinventario.infrastructure.adapters.input.rest;

import com.agroinventario.application.dto.request.AsistenteConsultaRequest;
import com.agroinventario.application.dto.response.ApiResponse;
import com.agroinventario.application.dto.response.AsistenteConsultaResponse;
import com.agroinventario.application.dto.response.AsistenteEstadoResponse;
import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.TipoAlerta;
import com.agroinventario.domain.ports.output.AlertaRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import com.agroinventario.infrastructure.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/asistente")
@Tag(name = "Asistente IA", description = "Consulta conversacional del estado del inventario y recomendaciones")
@SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
public class AsistenteController {

    private final ProductoRepositoryPort productoRepositoryPort;
    private final AlertaRepositoryPort alertaRepositoryPort;

    public AsistenteController(
            ProductoRepositoryPort productoRepositoryPort,
            AlertaRepositoryPort alertaRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
        this.alertaRepositoryPort = alertaRepositoryPort;
    }

    @GetMapping("/estado")
    @Operation(summary = "Estado del asistente", description = "Indica si el asistente está operativo y responde con métricas clave del inventario")
    public ResponseEntity<ApiResponse<AsistenteEstadoResponse>> estado() {
        List<Producto> productosActivos = productoRepositoryPort.findByEstado(EstadoGeneral.ACTIVO);
        long stockBajo = productosActivos.stream().filter(Producto::stockBajo).count();
        long alertasPendientes = alertaRepositoryPort.findByEstado(EstadoAlerta.PENDIENTE).size();

        String mensaje = String.format(
                "Asistente disponible. Hay %d productos activos, %d con stock bajo y %d alertas pendientes.",
                productosActivos.size(), stockBajo, alertasPendientes
        );

        return ResponseEntity.ok(ApiResponse.ok(new AsistenteEstadoResponse(true, mensaje)));
    }

    @PostMapping("/consultar")
    @Operation(summary = "Consulta al asistente", description = "Responde preguntas de negocio sobre inventario, stock, alertas y vencimientos usando datos reales del sistema")
    public ResponseEntity<ApiResponse<AsistenteConsultaResponse>> consultar(
            @Valid @RequestBody AsistenteConsultaRequest request) {

        String pregunta = request.pregunta() == null ? "" : request.pregunta().trim();
        String respuesta = generarRespuesta(pregunta);

        Map<String, Object> metadatos = new HashMap<>();
        List<Producto> productosActivos = productoRepositoryPort.findByEstado(EstadoGeneral.ACTIVO);
        metadatos.put("productos_activos", productosActivos.size());
        metadatos.put("stock_bajo", productosActivos.stream().filter(Producto::stockBajo).count());
        metadatos.put("alertas_pendientes", alertaRepositoryPort.findByEstado(EstadoAlerta.PENDIENTE).size());

        return ResponseEntity.ok(ApiResponse.ok("Consulta atendida", new AsistenteConsultaResponse(respuesta, metadatos)));
    }

    private String generarRespuesta(String pregunta) {
        if (pregunta == null || pregunta.isBlank()) {
            return "Necesito que me plantees una pregunta sobre el inventario, por ejemplo: stock bajo, vencimientos, alertas o reposición.";
        }

        String texto = pregunta.toLowerCase(Locale.ROOT);
        List<Producto> productosActivos = productoRepositoryPort.findByEstado(EstadoGeneral.ACTIVO);

        if (texto.contains("resumen") || texto.contains("estado general") || texto.contains("cómo va") || texto.contains("situación") || texto.contains("inventario")) {
            return resumenGeneral(productosActivos);
        }

        if (texto.contains("reponer") || texto.contains("reposici") || texto.contains("compra") || texto.contains("pedido")) {
            return respuestaReposicion(productosActivos);
        }

        if (texto.contains("stock bajo") || texto.contains("bajo stock") || texto.contains("sin stock") || texto.contains("critico")) {
            return respuestaStockBajo(productosActivos);
        }

        if (texto.contains("venc") || texto.contains("caduc") || texto.contains("fecha de vencimiento") || texto.contains("por vencer")) {
            return respuestaVencimiento(productosActivos);
        }

        if (texto.contains("alerta") || texto.contains("pendiente") || texto.contains("riesgo") || texto.contains("problema")) {
            return respuestaAlertas();
        }

        if (texto.contains("categoria") || texto.contains("medicina") || texto.contains("semen") || texto.contains("herbic") || texto.contains("insect") || texto.contains("fungi") || texto.contains("alimento")) {
            return respuestaCategoria(productosActivos, texto);
        }

        for (Producto producto : productosActivos) {
            String nombre = producto.nombre() == null ? "" : producto.nombre().toLowerCase(Locale.ROOT);
            if (texto.contains(nombre)) {
                return respuestaProducto(producto);
            }
        }

        return "Puedo ayudarte con análisis del inventario, stock bajo, vencimientos, alertas y recomendaciones de reposición. Ejemplos: '¿Qué productos están bajos?', '¿Qué vence pronto?' o '¿Qué debo pedir?'.";
    }

    private String resumenGeneral(List<Producto> productosActivos) {
        long totalActivos = productosActivos.size();
        long stockBajo = productosActivos.stream().filter(Producto::stockBajo).count();
        long conVencimiento = productosActivos.stream()
                .filter(producto -> producto.tieneFechaVencimiento() && producto.diasHastaVencimiento() <= 30)
                .count();
        long pendientes = alertaRepositoryPort.findByEstado(EstadoAlerta.PENDIENTE).size();

        String categoriaMasCritica = categoriaMasCritica(productosActivos);

        return String.format(
                "El inventario presenta %d productos activos, %d con stock bajo, %d en riesgo de vencimiento cercano y %d alertas pendientes. La categoría más crítica es %s. La prioridad operativa es revisar reposición y evitar pérdidas por productos cercanos a vencer.",
                totalActivos,
                stockBajo,
                conVencimiento,
                pendientes,
                categoriaMasCritica
        );
    }

    private String respuestaStockBajo(List<Producto> productosActivos) {
        List<Producto> stockBajo = productosActivos.stream()
                .filter(Producto::stockBajo)
                .sorted(Comparator.comparingInt(Producto::stockActual))
                .toList();

        if (stockBajo.isEmpty()) {
            return "No hay productos con stock bajo en este momento. La operación está estable y no requiere reposición urgente.";
        }

        String detalle = stockBajo.stream()
                .limit(5)
                .map(p -> String.format("%s (%d unidades, mínimo %d)", p.nombre(), p.stockActual(), p.stockMinimo()))
                .collect(Collectors.joining("; "));

        return String.format(
                "Hay %d productos por debajo del mínimo. Los más críticos son: %s. Se recomienda priorizar pedidos y revisión de inventario para evitar rupturas de stock.",
                stockBajo.size(),
                detalle
        );
    }

    private String respuestaVencimiento(List<Producto> productosActivos) {
        List<Producto> vencimientoProximo = productosActivos.stream()
                .filter(producto -> producto.tieneFechaVencimiento() && producto.diasHastaVencimiento() <= 30)
                .sorted(Comparator.comparingLong(Producto::diasHastaVencimiento))
                .toList();

        if (vencimientoProximo.isEmpty()) {
            return "No se registran productos con vencimiento cercano en los próximos 30 días. La condición es estable en ese aspecto.";
        }

        String detalle = vencimientoProximo.stream()
                .limit(5)
                .map(p -> String.format("%s (%d días para vencer)", p.nombre(), p.diasHastaVencimiento()))
                .collect(Collectors.joining("; "));

        return String.format(
                "Hay %d productos con vencimiento cercano. Los principales son: %s. Recomendación: priorizar ventas o movimientos de salida para evitar pérdidas por vencimiento.",
                vencimientoProximo.size(),
                detalle
        );
    }

    private String respuestaAlertas() {
        List<Alerta> alertasPendientes = alertaRepositoryPort.findByEstado(EstadoAlerta.PENDIENTE);
        if (alertasPendientes.isEmpty()) {
            return "No hay alertas pendientes. El sistema está operando sin eventos críticos en este momento.";
        }

        String detalle = alertasPendientes.stream()
                .limit(5)
                .map(a -> String.format("%s (%s)", a.productoNombre(), a.tipoAlerta()))
                .collect(Collectors.joining("; "));

        return String.format(
                "Hay %d alertas pendientes. Las más relevantes son: %s. Se recomienda revisar la operación de inventario y resolverlas de forma prioritaria.",
                alertasPendientes.size(),
                detalle
        );
    }

    private String respuestaReposicion(List<Producto> productosActivos) {
        List<Producto> criticos = productosActivos.stream()
                .filter(Producto::stockBajo)
                .sorted(Comparator.comparingInt(Producto::stockActual))
                .toList();

        if (criticos.isEmpty()) {
            return "No hay productos en reposición urgentemente. El inventario está por encima del nivel mínimo en la mayoría de líneas.";
        }

        String detalle = criticos.stream()
                .limit(3)
                .map(p -> {
                    int sugerido = Math.max(p.stockMinimo() - p.stockActual(), 5);
                    return String.format("%s: pedir %d unidades", p.nombre(), sugerido);
                })
                .collect(Collectors.joining("; "));

        return String.format(
                "La recomendación de reposición prioritaria es: %s. Esto ayuda a cubrir la demanda esperada y evitar desabastecimiento en las próximas semanas.",
                detalle
        );
    }

    private String respuestaCategoria(List<Producto> productosActivos, String texto) {
        Map<String, Long> porCategoria = productosActivos.stream()
                .collect(Collectors.groupingBy(
                        producto -> Objects.requireNonNullElse(producto.categoriaNombre(), "Sin categoría"),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        String categoriaObjetivo = buscarCategoria(texto, porCategoria.keySet());

        if (categoriaObjetivo != null) {
            List<Producto> filtrados = productosActivos.stream()
                    .filter(producto -> categoriaObjetivo.equalsIgnoreCase(producto.categoriaNombre()))
                    .toList();
            long bajo = filtrados.stream().filter(Producto::stockBajo).count();

            return String.format(
                    "La categoría %s tiene %d productos activos y %d con stock bajo. La revisión prioritaria debe centrarse en %s.",
                    categoriaObjetivo,
                    filtrados.size(),
                    bajo,
                    bajo > 0 ? "reposición y control de rotación" : "seguimiento continuo de consumo"
            );
        }

        return String.format(
                "Las categorías con mayor presencia son: %s. Si requires un análisis por familia de producto, puedo profundizar en medicina, herbicidas, insecticidas, alimentos o semillas.",
                porCategoria.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(4)
                        .map(entry -> entry.getKey() + " (" + entry.getValue() + ")")
                        .collect(Collectors.joining(", "))
        );
    }

    private String respuestaProducto(Producto producto) {
        String vencimiento = producto.tieneFechaVencimiento() ?
                producto.diasHastaVencimiento() + " días para vencer" :
                "sin vencimiento definido";

        String riesgo = producto.stockBajo() ? "crítico" : "estable";

        return String.format(
                "%s está en estado %s. Tiene %d unidades disponibles, mínimo %d, y %s. La recomendación es %s revisar su nivel de inventario y mantener control de rotación.",
                producto.nombre(),
                riesgo,
                producto.stockActual(),
                producto.stockMinimo(),
                vencimiento,
                producto.stockBajo() ? "reponer" : "seguir monitoreando"
        );
    }

    private String buscarCategoria(String texto, java.util.Set<String> categorias) {
        for (String categoria : categorias) {
            String nombre = categoria.toLowerCase(Locale.ROOT);
            if (texto.contains(nombre)) {
                return categoria;
            }
        }
        return null;
    }

    private String categoriaMasCritica(List<Producto> productosActivos) {
        Map<String, Long> porCategoria = productosActivos.stream()
                .collect(Collectors.groupingBy(
                        producto -> Objects.requireNonNullElse(producto.categoriaNombre(), "Sin categoría"),
                        Collectors.counting()
                ));

        Map<String, Long> criticas = productosActivos.stream()
                .filter(Producto::stockBajo)
                .collect(Collectors.groupingBy(
                        producto -> Objects.requireNonNullElse(producto.categoriaNombre(), "Sin categoría"),
                        Collectors.counting()
                ));

        if (criticas.isEmpty()) {
            return porCategoria.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("general");
        }

        return criticas.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("general");
    }
}
