-- V3: Corrige duplicación de productos y eliminación en cascada de alertas

-- 1. Evitar duplicación de productos por nombre (case-insensitive)
CREATE UNIQUE INDEX IF NOT EXISTS uk_productos_nombre_lower ON productos (LOWER(nombre));

-- 2. Permitir eliminar productos que tengan alertas asociadas
ALTER TABLE alertas DROP CONSTRAINT IF EXISTS alertas_producto_id_fkey;
ALTER TABLE alertas
    ADD CONSTRAINT alertas_producto_id_fkey
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE;