-- Re-ejecutar SOLO los movimientos de demostración (el catálogo va en Flyway V9).
-- Requiere que exista admin@agroinventario.local y productos con descripcion '[DEMO]'.
--
-- docker exec -i agroinventario-postgres psql -U agro_user -d agroinventario < scripts/reseed_demo_movimientos.sql

INSERT INTO movimientos_inventario (
    producto_id, tipo_movimiento, cantidad, descripcion, usuario_id, fecha_movimiento, stock_resultante
)
SELECT
    p.id,
    CASE WHEN n % 3 = 0 THEN 'ENTRADA' ELSE 'SALIDA' END,
    GREATEST(1, 1 + (n % 7)),
    '[DEMO] Movimiento de prueba ' || n,
    u.id,
    CURRENT_TIMESTAMP - ((n * 2) || ' days')::interval,
    GREATEST(p.stock_actual, 0)
FROM productos p
CROSS JOIN generate_series(1, 12) AS n
JOIN usuarios u ON u.email = 'admin@agroinventario.local'
WHERE p.descripcion LIKE '%[DEMO]%'
  AND NOT EXISTS (
      SELECT 1
      FROM movimientos_inventario m
      WHERE m.producto_id = p.id
        AND m.descripcion LIKE '[DEMO]%'
  );
