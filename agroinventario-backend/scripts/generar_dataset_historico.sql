-- Genera 150.000 observaciones históricas reproducibles para entrenamiento.
-- Ejecutar después de aplicar las migraciones V4, V5 y V6.
-- No es una migración Flyway: requiere una ejecución explícita y controlada.

BEGIN;

WITH productos_activos AS (
    SELECT
        p.id,
        p.stock_minimo,
        p.consumo_promedio_diario,
        ROW_NUMBER() OVER (ORDER BY p.id) AS producto_orden
    FROM productos p
    WHERE p.estado = 'ACTIVO'
),
fechas AS (
    SELECT fecha::date
    FROM generate_series(
        CURRENT_DATE - INTERVAL '6000 days',
        CURRENT_DATE - INTERVAL '1 day',
        INTERVAL '1 day'
    ) AS fecha
),
parametros AS (
    SELECT COUNT(*)::integer AS total_productos
    FROM productos_activos
),
muestra AS (
    SELECT
        p.id AS producto_id,
        f.fecha,
        p.stock_minimo,
        p.consumo_promedio_diario,
        p.producto_orden,
        parametros.total_productos,
        ROW_NUMBER() OVER (PARTITION BY p.id ORDER BY f.fecha) AS dia_orden
    FROM productos_activos p
    CROSS JOIN fechas f
    CROSS JOIN parametros
),
limitada AS (
    SELECT *
    FROM muestra
    WHERE dia_orden <= FLOOR(150000.0 / total_productos)
        + CASE
            WHEN producto_orden <= MOD(150000, total_productos) THEN 1
            ELSE 0
          END
),
calculada AS (
    SELECT
        producto_id,
        fecha,
        stock_minimo,
        GREATEST(
            0,
            ROUND(
                consumo_promedio_diario
                * (
                    1
                    + 0.15 * SIN(EXTRACT(EPOCH FROM fecha::timestamp) / 86400 / 9 + producto_id)
                    + 0.10 * COS(EXTRACT(MONTH FROM fecha) * 0.8 + producto_id)
                )
            )::integer
        ) AS salidas
    FROM limitada
),
con_entradas AS (
    SELECT
        producto_id,
        fecha,
        stock_minimo,
        salidas,
        CASE
            WHEN EXTRACT(DOW FROM fecha)::integer IN (0, 6) THEN 0
            WHEN salidas > 0 AND EXTRACT(DAY FROM fecha)::integer % 14 = 0
                THEN salidas + stock_minimo
            ELSE 0
        END AS entradas
    FROM calculada
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
    producto_id,
    fecha,
    stock_minimo + salidas + ((EXTRACT(DOY FROM fecha)::integer + producto_id::integer) % 20),
    entradas,
    salidas,
    GREATEST(
        0,
        stock_minimo
        + ((EXTRACT(DOY FROM fecha)::integer + producto_id::integer) % 20)
        + entradas
        - salidas
    ),
    CASE
        WHEN salidas > stock_minimo
            AND EXTRACT(DAY FROM fecha)::integer % 17 = 0 THEN 1
        ELSE 0
    END
FROM con_entradas
ON CONFLICT (producto_id, fecha) DO NOTHING;

COMMIT;

-- Verificación esperada:
-- SELECT COUNT(*) FROM inventario_diario;
-- SELECT MIN(fecha), MAX(fecha) FROM inventario_diario;
-- SELECT producto_id, COUNT(*) FROM inventario_diario GROUP BY producto_id ORDER BY producto_id;
