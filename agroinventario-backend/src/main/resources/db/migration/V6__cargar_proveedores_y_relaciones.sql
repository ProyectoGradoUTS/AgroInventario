-- Flyway V6: agrega proveedores base y los relaciona con el catálogo.
-- Son datos iniciales de desarrollo; pueden sustituirse por proveedores reales.

INSERT INTO proveedores (nombre, identificacion, email, telefono, dias_lead_time)
VALUES
    ('AgroVet Colombia', 'NIT-AGROVET-001', 'compras@agrovet.example', '+57 300 000 0001', 8),
    ('Semillas del Oriente', 'NIT-SEMILLAS-001', 'ventas@semillas.example', '+57 300 000 0002', 15),
    ('Control Agroquímico Nacional', 'NIT-CONTROL-001', 'pedidos@controlagro.example', '+57 300 000 0003', 10),
    ('Nutrición Animal Andina', 'NIT-NUTRICION-001', 'pedidos@nutricion.example', '+57 300 000 0004', 8)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO producto_proveedor (
    producto_id, proveedor_id, es_principal, lead_time_dias
)
SELECT
    p.id,
    pr.id,
    TRUE,
    p.lead_time_dias
FROM productos p
JOIN categorias c ON c.id = p.categoria_id
JOIN proveedores pr ON pr.nombre = CASE LOWER(c.nombre)
    WHEN 'medicina' THEN 'AgroVet Colombia'
    WHEN 'semillas' THEN 'Semillas del Oriente'
    WHEN 'toxicológica' THEN 'Control Agroquímico Nacional'
    WHEN 'toxicologica' THEN 'Control Agroquímico Nacional'
    WHEN 'herbicidas' THEN 'Control Agroquímico Nacional'
    WHEN 'insecticidas' THEN 'Control Agroquímico Nacional'
    WHEN 'fungicidas' THEN 'Control Agroquímico Nacional'
    WHEN 'alimentos' THEN 'Nutrición Animal Andina'
    ELSE NULL
END
WHERE NOT EXISTS (
    SELECT 1
    FROM producto_proveedor pp
    WHERE pp.producto_id = p.id
);

UPDATE productos p
SET lead_time_dias = pp.lead_time_dias
FROM producto_proveedor pp
WHERE pp.producto_id = p.id
  AND pp.es_principal = TRUE
  AND pp.lead_time_dias IS NOT NULL;
