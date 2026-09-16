-- Flyway V7: serie histórica de consumo (90 días) para el motor predictivo.
-- Es aditiva y no pisa filas existentes de inventario_diario.

INSERT INTO inventario_diario (
    producto_id,
    fecha,
    stock_inicial,
    entradas,
    salidas,
    stock_final,
    dias_stockout
)
SELECT
    p.id,
    (CURRENT_DATE - (90 - d.n))::date,
    GREATEST(
        0,
        p.stock_actual + ((90 - d.n) * GREATEST(1, ROUND(p.stock_minimo / 20.0)))
        - CASE WHEN d.n % 14 = 0 THEN GREATEST(p.stock_minimo, 5) ELSE 0 END
    )::int,
    CASE WHEN d.n % 14 = 0 THEN GREATEST(p.stock_minimo, 5) ELSE 0 END,
    GREATEST(
        0,
        ROUND(
            (
                GREATEST(p.stock_minimo / 14.0, 0.4)
                + 1.8 * sin(d.n / 6.5 + p.id)
                + 0.6 * sin(d.n / 21.0)
            )::numeric
        )
    )::int,
    GREATEST(
        0,
        p.stock_actual + ((89 - d.n) * GREATEST(1, ROUND(p.stock_minimo / 20.0)))
    )::int,
    CASE WHEN p.stock_actual = 0 AND d.n >= 80 THEN 1 ELSE 0 END
FROM productos p
CROSS JOIN generate_series(1, 90) AS d(n)
ON CONFLICT (producto_id, fecha) DO NOTHING;

UPDATE productos p
SET consumo_promedio_diario = COALESCE(h.promedio, p.consumo_promedio_diario)
FROM (
    SELECT producto_id, AVG(salidas)::numeric(12, 4) AS promedio
    FROM inventario_diario
    GROUP BY producto_id
) h
WHERE p.id = h.producto_id;
