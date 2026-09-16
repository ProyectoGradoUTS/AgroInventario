package com.agroinventario.infrastructure.adapters.output.persistence.adapter;

import com.agroinventario.domain.model.ProyeccionInventario;
import com.agroinventario.domain.ports.output.PrediccionInventarioPersistenciaPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Component
public class PrediccionInventarioPersistenceAdapter implements PrediccionInventarioPersistenciaPort {

    private static final Logger log = LoggerFactory.getLogger(PrediccionInventarioPersistenceAdapter.class);

    private final JdbcTemplate jdbcTemplate;

    public PrediccionInventarioPersistenceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void reemplazarPrediccionesDelDia(List<ProyeccionInventario> proyecciones) {
        if (proyecciones == null || proyecciones.isEmpty()) {
            return;
        }
        try {
            jdbcTemplate.update("DELETE FROM recomendaciones_reposicion WHERE fecha_generacion::date = CURRENT_DATE");
            jdbcTemplate.update("DELETE FROM predicciones_inventario WHERE fecha_prediccion::date = CURRENT_DATE");

            for (ProyeccionInventario proyeccion : proyecciones) {
                Long prediccionId = insertarPrediccion(proyeccion);
                if (prediccionId != null && proyeccion.cantidadSugerida() > 0 && !"MONITOREO".equals(proyeccion.estrategia())) {
                    insertarRecomendacion(prediccionId, proyeccion);
                }
            }
        } catch (DataAccessException ex) {
            log.warn("No se pudieron persistir predicciones del día: {}", ex.getMessage());
        }
    }

    @Override
    public void registrarEjecucionModelo(
            int productos,
            int registrosHistoricos,
            LocalDate inicioDatos,
            LocalDate finDatos,
            double mae,
            double rmse) {
        String sql = """
                INSERT INTO ejecuciones_modelo_predictivo (
                    nombre_modelo, version_modelo, fecha_entrenamiento,
                    fecha_inicio_datos, fecha_fin_datos,
                    registros_entrenamiento, registros_validacion, registros_prueba,
                    mae, rmse, mape, parametros, estado
                ) VALUES (
                    'MEDIA_MOVIL_REGRESION', '1.0', CURRENT_TIMESTAMP,
                    ?, ?, ?, 0, 0, ?, ?, NULL,
                    CAST(? AS jsonb), 'COMPLETADO'
                )
                """;
        String params = "{\"modelo\":\"media_movil_regresion\",\"horizonteDias\":30,\"productos\":%d}"
                .formatted(productos);
        try {
            jdbcTemplate.update(sql, inicioDatos, finDatos, registrosHistoricos, mae, rmse, params);
        } catch (DataAccessException ex) {
            log.debug("No se pudo registrar ejecución del modelo: {}", ex.getMessage());
        }
    }

    private Long insertarPrediccion(ProyeccionInventario proyeccion) {
        String sql = """
                INSERT INTO predicciones_inventario (
                    producto_id, fecha_inicio, fecha_fin, horizonte_dias,
                    demanda_proyectada, consumo_diario_estimado, fecha_agotamiento,
                    stock_proyectado, riesgo_desabastecimiento, nivel_confianza,
                    modelo, version_modelo, variables_entrada
                ) VALUES (
                    ?, CURRENT_DATE, CURRENT_DATE + 30, 30,
                    ?, ?, ?, ?, ?, ?,
                    'MEDIA_MOVIL_REGRESION', '1.0', CAST(? AS jsonb)
                )
                RETURNING id
                """;
        String variables = """
                {"consumoPromedio":%s,"pendiente":%s,"desviacion":%s,"fuente":"%s","confianza":%s}
                """.formatted(
                format(proyeccion.consumoPromedio()),
                format(proyeccion.pendienteConsumo()),
                format(proyeccion.desviacionConsumo()),
                proyeccion.fuenteDatos(),
                format(proyeccion.nivelConfianza())
        ).trim();

        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                return null;
            }
            return rs.getLong(1);
        },
                proyeccion.productoId(),
                proyeccion.demandaProyectada30d(),
                proyeccion.consumoPromedio(),
                proyeccion.fechaProyectadaAgotamiento(),
                Math.max(0, proyeccion.stockActual() - proyeccion.demandaProyectada30d()),
                mapearRiesgo(proyeccion.riesgo()),
                Math.max(0, Math.min(1, proyeccion.nivelConfianza())),
                variables
        );
    }

    private void insertarRecomendacion(Long prediccionId, ProyeccionInventario proyeccion) {
        String sql = """
                INSERT INTO recomendaciones_reposicion (
                    producto_id, prediccion_id, cantidad_recomendada, fecha_sugerida,
                    estrategia, prioridad, justificacion, estado
                ) VALUES (?, ?, ?, ?, ?, ?, ?, 'PENDIENTE')
                """;
        jdbcTemplate.update(
                sql,
                proyeccion.productoId(),
                prediccionId,
                proyeccion.cantidadSugerida(),
                LocalDate.now().plusDays(Math.min(proyeccion.leadTimeDias(), Math.max(1, proyeccion.diasHastaAgotamiento() / 2))),
                proyeccion.estrategiaPersistida(),
                mapearPrioridad(proyeccion.riesgo()),
                recortar(proyeccion.mensaje(), 1000)
        );
    }

    private String mapearRiesgo(String riesgo) {
        String normalizado = riesgo == null ? "MEDIO" : riesgo.toUpperCase(Locale.ROOT);
        return switch (normalizado) {
            case "BAJO", "MEDIO", "ALTO", "CRITICO" -> normalizado;
            default -> "MEDIO";
        };
    }

    private String mapearPrioridad(String riesgo) {
        return switch (mapearRiesgo(riesgo)) {
            case "CRITICO" -> "CRITICA";
            case "ALTO" -> "ALTA";
            case "BAJO" -> "BAJA";
            default -> "MEDIA";
        };
    }

    private String recortar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= max ? texto : texto.substring(0, max);
    }

    private String format(double valor) {
        return String.format(Locale.US, "%.4f", valor);
    }
}
