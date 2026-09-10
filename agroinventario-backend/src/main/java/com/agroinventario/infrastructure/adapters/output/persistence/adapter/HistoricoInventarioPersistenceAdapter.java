package com.agroinventario.infrastructure.adapters.output.persistence.adapter;

import com.agroinventario.domain.model.HistoricoInventarioResumen;
import com.agroinventario.domain.ports.output.HistoricoInventarioRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HistoricoInventarioPersistenceAdapter implements HistoricoInventarioRepositoryPort {

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
    }
}
