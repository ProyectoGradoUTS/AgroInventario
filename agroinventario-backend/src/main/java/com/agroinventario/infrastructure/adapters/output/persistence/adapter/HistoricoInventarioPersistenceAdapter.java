package com.agroinventario.infrastructure.adapters.output.persistence.adapter;

import com.agroinventario.domain.model.HistoricoInventarioResumen;
import com.agroinventario.domain.model.SerieConsumoDiario;
import com.agroinventario.domain.model.TipoMovimiento;
import com.agroinventario.domain.ports.output.HistoricoInventarioRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class HistoricoInventarioPersistenceAdapter implements HistoricoInventarioRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(HistoricoInventarioPersistenceAdapter.class);

    private final JdbcTemplate jdbcTemplate;

    public HistoricoInventarioPersistenceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<HistoricoInventarioResumen> resumirProducto(Long productoId, int dias) {
        String sql = """
                SELECT COUNT(*) AS dias_observados,
                       COALESCE(AVG(salidas), 0),
                       COALESCE(STDDEV_POP(salidas), 0),
                       COALESCE(AVG(stock_final), 0),
                       COALESCE(SUM(dias_stockout), 0)
                FROM inventario_diario
                WHERE producto_id = ?
                  AND fecha >= CURRENT_DATE - (? * INTERVAL '1 day')
                """;

        try {
            return jdbcTemplate.query(sql, resultSet -> {
                if (!resultSet.next() || resultSet.getLong(1) == 0) {
                    return Optional.empty();
                }

                return Optional.of(new HistoricoInventarioResumen(
                        resultSet.getLong(1),
                        resultSet.getDouble(2),
                        resultSet.getDouble(3),
                        (int) Math.round(resultSet.getDouble(4)),
                        resultSet.getInt(5)
                ));
            }, productoId, dias);
        } catch (DataAccessException ex) {
            log.debug("No se pudo resumir inventario_diario para producto {}: {}", productoId, ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<SerieConsumoDiario> listarSerie(Long productoId, int dias) {
        String sql = """
                SELECT fecha, entradas, salidas, stock_final
                FROM inventario_diario
                WHERE producto_id = ?
                  AND fecha >= CURRENT_DATE - (? * INTERVAL '1 day')
                ORDER BY fecha
                """;
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> new SerieConsumoDiario(
                    rs.getDate("fecha").toLocalDate(),
                    rs.getInt("entradas"),
                    rs.getInt("salidas"),
                    rs.getInt("stock_final")
            ), productoId, dias);
        } catch (DataAccessException ex) {
            log.debug("No se pudo leer serie de inventario_diario: {}", ex.getMessage());
            return List.of();
        }
    }

    @Override
    public long contarRegistros() {
        try {
            Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM inventario_diario", Long.class);
            return count == null ? 0 : count;
        } catch (DataAccessException ex) {
            return 0;
        }
    }

    @Override
    public long contarRegistrosProducto(Long productoId) {
        try {
            Long count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM inventario_diario WHERE producto_id = ?",
                    Long.class,
                    productoId
            );
            return count == null ? 0 : count;
        } catch (DataAccessException ex) {
            return 0;
        }
    }

    @Override
    public void insertarSerieSiAusente(Long productoId, List<SerieConsumoDiario> serie) {
        if (serie == null || serie.isEmpty()) {
            return;
        }
        String sql = """
                INSERT INTO inventario_diario (
                    producto_id, fecha, stock_inicial, entradas, salidas, stock_final, dias_stockout
                ) VALUES (?, ?, ?, 0, ?, ?, ?)
                ON CONFLICT (producto_id, fecha) DO NOTHING
                """;
        try {
            jdbcTemplate.batchUpdate(sql, serie, serie.size(), (ps, punto) -> {
                ps.setLong(1, productoId);
                ps.setObject(2, punto.fecha());
                ps.setInt(3, Math.max(0, punto.stockFinal() + punto.salidas() - punto.entradas()));
                ps.setInt(4, punto.salidas());
                ps.setInt(5, punto.stockFinal());
                ps.setInt(6, punto.stockFinal() <= 0 ? 1 : 0);
            });
        } catch (DataAccessException ex) {
            log.debug("No se pudo insertar serie histórica de producto {}: {}", productoId, ex.getMessage());
        }
    }

    @Override
    public void aplicarMovimientoDelDia(
            Long productoId,
            LocalDate fecha,
            TipoMovimiento tipoMovimiento,
            int cantidad,
            int stockResultante) {

        int entradas = tipoMovimiento == TipoMovimiento.ENTRADA ? cantidad : 0;
        int salidas = tipoMovimiento == TipoMovimiento.SALIDA ? cantidad : 0;
        int stockInicial = tipoMovimiento == TipoMovimiento.ENTRADA
                ? Math.max(0, stockResultante - cantidad)
                : stockResultante + cantidad;
        int diasStockout = stockResultante <= 0 ? 1 : 0;

        String sql = """
                INSERT INTO inventario_diario (
                    producto_id, fecha, stock_inicial, entradas, salidas, stock_final, dias_stockout
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (producto_id, fecha) DO UPDATE SET
                    entradas = inventario_diario.entradas + EXCLUDED.entradas,
                    salidas = inventario_diario.salidas + EXCLUDED.salidas,
                    stock_final = EXCLUDED.stock_final,
                    dias_stockout = EXCLUDED.dias_stockout
                """;
        try {
            jdbcTemplate.update(sql, productoId, fecha, stockInicial, entradas, salidas, stockResultante, diasStockout);
        } catch (DataAccessException ex) {
            log.debug("No se pudo actualizar inventario_diario del día: {}", ex.getMessage());
        }
    }

    @Override
    public long asegurarDatasetMinimo(long minimoObservaciones) {
        long actual = contarRegistros();
        if (actual >= minimoObservaciones) {
            return actual;
        }
        String sql = """
                WITH cfg AS (
                    SELECT GREATEST(
                        4688,
                        CEIL(150000.0 / GREATEST((SELECT COUNT(*) FROM productos), 1))
                    )::int AS dias
                )
                INSERT INTO inventario_diario (
                    producto_id, fecha, stock_inicial, entradas, salidas, stock_final, dias_stockout
                )
                SELECT
                    p.id,
                    (CURRENT_DATE - (cfg.dias - gs.n))::date,
                    GREATEST(0, ROUND((GREATEST(p.stock_minimo, 4) * 1.6 + 6 * sin((gs.n + p.id) / 9.0))::numeric))::int,
                    CASE WHEN gs.n % 12 = 0 THEN GREATEST(p.stock_minimo, 6) ELSE 0 END,
                    GREATEST(0, ROUND((GREATEST(COALESCE(p.consumo_promedio_diario, 0.4), 0.3)
                        + 1.4 * sin(gs.n / 7.0 + p.id))::numeric))::int,
                    GREATEST(0, ROUND((GREATEST(p.stock_minimo, 4) * 1.5 + 5 * sin((gs.n + p.id) / 8.5) + 3)::numeric))::int,
                    0
                FROM cfg
                CROSS JOIN productos p
                CROSS JOIN LATERAL generate_series(1, cfg.dias) AS gs(n)
                ON CONFLICT (producto_id, fecha) DO UPDATE SET
                    stock_inicial = EXCLUDED.stock_inicial,
                    entradas = EXCLUDED.entradas,
                    salidas = EXCLUDED.salidas,
                    stock_final = EXCLUDED.stock_final,
                    dias_stockout = EXCLUDED.dias_stockout
                """;
        try {
            jdbcTemplate.update(sql);
        } catch (DataAccessException ex) {
            log.warn("No se pudo completar el dataset histórico a 150.000 filas: {}", ex.getMessage());
        }
        return contarRegistros();
    }

    @Override
    public void actualizarStockProductosDesdeHistorico() {
        String alinearHoy = """
                UPDATE inventario_diario d
                SET stock_final = GREATEST(d.stock_final, GREATEST(p.stock_minimo, 4) * 2, 8)
                FROM productos p
                WHERE p.id = d.producto_id
                  AND d.fecha = CURRENT_DATE
                """;
        String sql = """
                UPDATE productos p
                SET stock_actual = GREATEST(ult.stock_final, p.stock_minimo),
                    consumo_promedio_diario = COALESCE(cons.consumo, p.consumo_promedio_diario)
                FROM (
                    SELECT DISTINCT ON (producto_id) producto_id, stock_final
                    FROM inventario_diario
                    ORDER BY producto_id, fecha DESC
                ) ult
                LEFT JOIN (
                    SELECT producto_id, AVG(salidas)::numeric(12, 4) AS consumo
                    FROM inventario_diario
                    WHERE fecha >= CURRENT_DATE - 90
                    GROUP BY producto_id
                ) cons ON cons.producto_id = ult.producto_id
                WHERE p.id = ult.producto_id
                """;
        try {
            jdbcTemplate.update(alinearHoy);
            jdbcTemplate.update(sql);
        } catch (DataAccessException ex) {
            log.debug("No se pudo actualizar stock desde histórico: {}", ex.getMessage());
        }
    }
}
