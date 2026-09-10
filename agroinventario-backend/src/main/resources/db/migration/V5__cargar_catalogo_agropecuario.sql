-- Flyway V5: carga el catálogo base agropecuario definido para el proyecto.
-- Los productos existentes no se duplican gracias al índice case-insensitive.

INSERT INTO categorias (nombre, descripcion)
VALUES
    ('Medicina', 'Medicamentos y productos veterinarios'),
    ('Semillas', 'Semillas para cultivos'),
    ('Toxicológica', 'Productos de control toxicológico'),
    ('Herbicidas', 'Productos para control de malezas'),
    ('Insecticidas', 'Productos para control de insectos'),
    ('Fungicidas', 'Productos para control de hongos'),
    ('Alimentos', 'Alimentos para animales')
ON CONFLICT (nombre) DO NOTHING;

WITH catalogo (
    nombre, categoria, stock_minimo, lead_time_dias, consumo_diario,
    unidad_medida, perecedero, vencimiento_dias
) AS (
    VALUES
        ('Oxitetraciclina', 'Medicina', 10, 8, 1.0, 'UNIDAD', TRUE, 365),
        ('Kaput Master', 'Medicina', 10, 8, 1.0, 'UNIDAD', TRUE, 365),
        ('Dexapen', 'Medicina', 10, 8, 1.0, 'UNIDAD', TRUE, 365),
        ('Tripen L.A', 'Medicina', 10, 8, 1.0, 'UNIDAD', TRUE, 365),
        ('Hierro dextran', 'Medicina', 10, 8, 1.0, 'UNIDAD', TRUE, 365),
        ('Streptoland', 'Medicina', 10, 8, 1.0, 'UNIDAD', TRUE, 365),
        ('Bonavit', 'Medicina', 10, 8, 1.0, 'UNIDAD', TRUE, 365),
        ('Zanahoria', 'Semillas', 15, 15, 1.0, 'SOBRE', FALSE, NULL),
        ('Tomate', 'Semillas', 15, 15, 1.0, 'SOBRE', FALSE, NULL),
        ('Lechuga', 'Semillas', 15, 15, 1.0, 'SOBRE', FALSE, NULL),
        ('Cilantro', 'Semillas', 15, 15, 1.0, 'SOBRE', FALSE, NULL),
        ('Pepino', 'Semillas', 15, 15, 1.0, 'SOBRE', FALSE, NULL),
        ('Cebolla', 'Semillas', 15, 15, 1.0, 'SOBRE', FALSE, NULL),
        ('Arveja', 'Semillas', 15, 15, 1.0, 'SOBRE', FALSE, NULL),
        ('Curagan NL 375ml', 'Toxicológica', 8, 15, 1.0, 'UNIDAD', TRUE, 730),
        ('Raid cucarachas 5', 'Toxicológica', 5, 10, 1.0, 'UNIDAD', TRUE, 730),
        ('Negasunt 3', 'Toxicológica', 5, 15, 1.0, 'UNIDAD', TRUE, 730),
        ('Panzer litro 10', 'Herbicidas', 15, 8, 2.0, 'LITRO', TRUE, 1460),
        ('Roundup litro 10', 'Herbicidas', 10, 8, 2.0, 'LITRO', TRUE, 1460),
        ('Destierro litro 10', 'Herbicidas', 10, 8, 2.0, 'LITRO', TRUE, 1460),
        ('Gramafin litro 5', 'Herbicidas', 5, 8, 2.0, 'LITRO', TRUE, 1460),
        ('Invetrina 250ml 6', 'Insecticidas', 5, 8, 1.0, 'UNIDAD', TRUE, 1095),
        ('Alfa point ML 5', 'Insecticidas', 5, 8, 1.0, 'UNIDAD', TRUE, 1095),
        ('Candonga 250ml 5', 'Insecticidas', 5, 8, 1.0, 'UNIDAD', TRUE, 1095),
        ('Fulminator 500ml 3', 'Insecticidas', 3, 10, 0.8, 'UNIDAD', TRUE, 1095),
        ('Lannate sobre 3', 'Insecticidas', 3, 15, 0.2, 'SOBRE', TRUE, 1095),
        ('Mertec 100ml 3', 'Fungicidas', 2, 8, 0.3, 'UNIDAD', TRUE, 730),
        ('Evito-T litro 4', 'Fungicidas', 2, 8, 0.3, 'LITRO', TRUE, 730),
        ('PONEDORA', 'Alimentos', 50, 8, 6.25, 'BULTO', TRUE, 60),
        ('CERDOS', 'Alimentos', 50, 8, 6.25, 'BULTO', TRUE, 60),
        ('GANADERIA', 'Alimentos', 10, 8, 1.25, 'BULTO', TRUE, 60),
        ('POLLOS', 'Alimentos', 40, 8, 6.25, 'BULTO', TRUE, 60)
)
INSERT INTO productos (
    nombre, descripcion, precio, stock_actual, stock_minimo, fecha_vencimiento,
    categoria_id, estado, unidad_medida, perecedero, lead_time_dias,
    consumo_promedio_diario, stock_seguridad, punto_reorden
)
SELECT
    c.nombre,
    'Catálogo base agropecuario',
    0,
    0,
    c.stock_minimo,
    CASE WHEN c.vencimiento_dias IS NULL
         THEN NULL
         ELSE CURRENT_DATE + c.vencimiento_dias
    END,
    cat.id,
    'ACTIVO',
    c.unidad_medida,
    c.perecedero,
    c.lead_time_dias,
    c.consumo_diario,
    CEIL(c.consumo_diario * c.lead_time_dias)::INTEGER,
    CEIL(c.consumo_diario * c.lead_time_dias)::INTEGER + c.stock_minimo
FROM catalogo c
JOIN categorias cat ON LOWER(cat.nombre) = LOWER(c.categoria)
WHERE NOT EXISTS (
    SELECT 1
    FROM productos p
    WHERE LOWER(p.nombre) = LOWER(c.nombre)
);
