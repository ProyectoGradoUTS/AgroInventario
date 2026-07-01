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
