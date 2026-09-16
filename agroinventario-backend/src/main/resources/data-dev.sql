-- Datos de desarrollo (perfil dev)
-- Usuario admin: contraseña migrada a BCrypt al iniciar (Admin123!) — ver DevAdminPasswordInitializer

INSERT INTO usuarios (nombre, email, password, estado, fecha_creacion)
SELECT 'Administrador Sistema', 'admin@agroinventario.local', '{noop}temporal', 'ACTIVO', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@agroinventario.local');

INSERT INTO usuarios_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u, roles r
WHERE u.email = 'admin@agroinventario.local' AND r.nombre = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM usuarios_roles ur
      WHERE ur.usuario_id = u.id AND ur.rol_id = r.id
  );

INSERT INTO categorias (nombre, descripcion)
SELECT 'Granos', 'Cereales y granos almacenados'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Granos');

INSERT INTO categorias (nombre, descripcion)
SELECT 'Lácteos', 'Productos derivados de leche'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Lácteos');

INSERT INTO categorias (nombre, descripcion)
SELECT 'Insumos Veterinarios', 'Medicamentos y suplementos'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre = 'Insumos Veterinarios');

-- Movimientos de demostración (requiere el admin creado arriba). Idempotente.
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

