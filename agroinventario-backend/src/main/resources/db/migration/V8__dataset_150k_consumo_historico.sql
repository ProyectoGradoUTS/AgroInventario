-- Flyway V8: al menos 150.000 observaciones diarias y stock usable en el catálogo.

WITH cfg AS (
    SELECT GREATEST(
        4688,
        CEIL(150000.0 / GREATEST((SELECT COUNT(*) FROM productos), 1))
    )::int AS dias
)
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
    (CURRENT_DATE - (cfg.dias - gs.n))::date,
    GREATEST(
        0,
        ROUND((GREATEST(p.stock_minimo, 4) * 1.6 + 6 * sin((gs.n + p.id) / 9.0) + ((cfg.dias - gs.n) % 18))::numeric)
    )::int,
    CASE WHEN gs.n % 12 = 0 THEN GREATEST(p.stock_minimo, 6) ELSE 0 END,
    GREATEST(
        0,
        ROUND((
            GREATEST(COALESCE(p.consumo_promedio_diario, 0.4), GREATEST(p.stock_minimo / 14.0, 0.3))
            + 1.4 * sin(gs.n / 7.0 + p.id)
            + 0.5 * sin(gs.n / 28.0)
        )::numeric)
    )::int,
    GREATEST(
        0,
        ROUND((GREATEST(p.stock_minimo, 4) * 1.5 + 5 * sin((gs.n + p.id) / 8.5) + 3)::numeric)
    )::int,
    0
FROM cfg
CROSS JOIN productos p
CROSS JOIN LATERAL generate_series(1, cfg.dias) AS gs(n)
ON CONFLICT (producto_id, fecha) DO UPDATE SET
    stock_inicial = EXCLUDED.stock_inicial,
    entradas = EXCLUDED.entradas,
    salidas = EXCLUDED.salidas,
    stock_final = EXCLUDED.stock_final,
    dias_stockout = EXCLUDED.dias_stockout;

UPDATE inventario_diario d
SET stock_final = GREATEST(d.stock_final, GREATEST(p.stock_minimo, 4) * 2, 8)
FROM productos p
WHERE p.id = d.producto_id
  AND d.fecha = CURRENT_DATE;

UPDATE productos p
SET stock_actual = GREATEST(ult.stock_final, p.stock_minimo),
    consumo_promedio_diario = COALESCE(cons.consumo, p.consumo_promedio_diario)
FROM (
    SELECT DISTINCT ON (producto_id)
        producto_id,
        stock_final
    FROM inventario_diario
    ORDER BY producto_id, fecha DESC
) ult
LEFT JOIN (
    SELECT producto_id, AVG(salidas)::numeric(12, 4) AS consumo
    FROM inventario_diario
    WHERE fecha >= CURRENT_DATE - 90
    GROUP BY producto_id
) cons ON cons.producto_id = ult.producto_id
WHERE p.id = ult.producto_id;
