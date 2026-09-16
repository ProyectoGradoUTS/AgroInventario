-- Flyway V9: catálogo de demostración (pruebas / tesis).
-- Idempotente: no duplica categorías ni productos existentes (V5 y data-dev se conservan).
-- Marca los productos nuevos con '[DEMO]' en descripcion para poder identificarlos.

INSERT INTO categorias (nombre, descripcion)
VALUES
    ('Mascotas', 'Alimentos y accesorios para perros y gatos'),
    ('Acuicultura', 'Alimentos e insumos para peces'),
    ('Fertilizantes', 'Abonos orgánicos y fertilizantes químicos'),
    ('Herramientas agrícolas', 'Herramientas e insumos de campo'),
    ('Insumos Veterinarios', 'Medicamentos y suplementos pecuarios'),
    ('Granos', 'Cereales y granos almacenados'),
    ('Lácteos', 'Productos derivados de leche')
ON CONFLICT (nombre) DO NOTHING;

WITH catalogo (
    nombre, categoria, descripcion, precio, stock_actual, stock_minimo,
    venc_dias, unidad, perecedero, lead_time, consumo
) AS (
    VALUES
        ('Dog Chow Adulto 25kg', 'Mascotas', 'Alimento seco para perros adultos. [DEMO]', 128000.00, 140, 25, 40, 'BULTO', TRUE, 8, 3.5),
        ('Pedigree Adulto Carne 8kg', 'Mascotas', 'Alimento para perros sabor carne. [DEMO]', 62000.00, 8, 12, 20, 'BULTO', TRUE, 7, 1.8),
        ('Ringo Performance 25kg', 'Mascotas', 'Alimento de alto desempeño canino. [DEMO]', 98000.00, 90, 20, 55, 'BULTO', TRUE, 8, 2.4),
        ('Nutrecan Cachorro 8kg', 'Mascotas', 'Alimento para cachorros. [DEMO]', 54000.00, 18, 10, 22, 'BULTO', TRUE, 7, 1.2),
        ('Chunks Adulto 22kg', 'Mascotas', 'Alimento económico para perros. [DEMO]', 89000.00, 5, 15, 12, 'BULTO', TRUE, 10, 2.0),
        ('Cat Chow Adulto 8kg', 'Mascotas', 'Alimento seco para gatos. [DEMO]', 48000.00, 70, 15, 25, 'BULTO', TRUE, 7, 1.5),
        ('Whiskas Adulto Pollo 1kg', 'Mascotas', 'Alimento para gatos sabor pollo. [DEMO]', 12500.00, 200, 40, 90, 'BOLSA', TRUE, 5, 4.0),
        ('Mirringo Adulto 8kg', 'Mascotas', 'Alimento para gatos adultos. [DEMO]', 39000.00, 12, 10, 18, 'BULTO', TRUE, 7, 1.1),
        ('Gatsy Pescado 1.5kg', 'Mascotas', 'Alimento para gatos sabor pescado. [DEMO]', 9800.00, 4, 10, 8, 'BOLSA', TRUE, 6, 1.6),
        ('Concentrado Iniciación Pollos 40kg', 'Alimentos', 'Iniciador para pollos de engorde. [DEMO]', 112000.00, 60, 40, 45, 'BULTO', TRUE, 8, 5.0),
        ('Concentrado Engorde Pollos 40kg', 'Alimentos', 'Finalizador pollos de engorde. [DEMO]', 108000.00, 3, 40, 10, 'BULTO', TRUE, 8, 6.2),
        ('Alimento Codornices 40kg', 'Alimentos', 'Concentrado para codornices. [DEMO]', 99000.00, 35, 20, 50, 'BULTO', TRUE, 10, 2.2),
        ('Alimento Patos 40kg', 'Alimentos', 'Concentrado para patos. [DEMO]', 95000.00, 22, 15, 80, 'BULTO', TRUE, 10, 1.8),
        ('Conchilla de ostras 25kg', 'Alimentos', 'Aporte de calcio para aves. [DEMO]', 28000.00, 80, 20, 200, 'BULTO', TRUE, 12, 1.0),
        ('Premix ponedoras 5kg', 'Alimentos', 'Premézcla vitamínica ponedoras. [DEMO]', 45000.00, 9, 8, 25, 'BOLSA', TRUE, 8, 0.6),
        ('Iniciador Cerdos 40kg', 'Alimentos', 'Iniciador para lechones. [DEMO]', 118000.00, 50, 30, 40, 'BULTO', TRUE, 8, 3.8),
        ('Levante Cerdos 40kg', 'Alimentos', 'Levante porcino. [DEMO]', 110000.00, 14, 30, 20, 'BULTO', TRUE, 8, 3.2),
        ('Engorde Cerdos 40kg', 'Alimentos', 'Engorde porcino. [DEMO]', 105000.00, 7, 30, 12, 'BULTO', TRUE, 8, 3.5),
        ('Gestación Cerdas 40kg', 'Alimentos', 'Concentrado cerdas gestantes. [DEMO]', 122000.00, 28, 15, 90, 'BULTO', TRUE, 10, 1.4),
        ('Sal mineralizada ganado 25kg', 'Alimentos', 'Sal mineral para bovinos. [DEMO]', 35000.00, 100, 20, 365, 'BULTO', TRUE, 8, 1.3),
        ('Melaza de caña 25kg', 'Alimentos', 'Melaza para suplemento bovino. [DEMO]', 22000.00, 16, 12, 120, 'CANECA', TRUE, 7, 1.7),
        ('Heno de angleton paca', 'Alimentos', 'Forraje empacado. [DEMO]', 18000.00, 200, 40, CAST(NULL AS INTEGER), 'PACAS', FALSE, 5, 4.5),
        ('Concentrado lecheras 40kg', 'Alimentos', 'Concentrado vacas en producción. [DEMO]', 125000.00, 11, 10, 45, 'BULTO', TRUE, 8, 2.8),
        ('Bloque multinutricional 25kg', 'Alimentos', 'Bloque para ganado de ceba. [DEMO]', 42000.00, 6, 8, 180, 'BLOQUE', TRUE, 12, 0.9),
        ('Alimento terneros 40kg', 'Alimentos', 'Iniciador terneros. [DEMO]', 108000.00, 20, 15, 40, 'BULTO', TRUE, 8, 1.6),
        ('Concentrado tilapia 32% 40kg', 'Acuicultura', 'Alimento extruido tilapia. [DEMO]', 135000.00, 40, 20, 90, 'BULTO', TRUE, 10, 2.5),
        ('Concentrado cachama 28% 40kg', 'Acuicultura', 'Alimento para cachama. [DEMO]', 128000.00, 18, 15, 90, 'BULTO', TRUE, 10, 1.9),
        ('Alimento alevinos 20kg', 'Acuicultura', 'Migaja para alevinos. [DEMO]', 89000.00, 5, 8, 60, 'BULTO', TRUE, 12, 0.8),
        ('Harina de pescado 40kg', 'Acuicultura', 'Materia prima acuícola. [DEMO]', 155000.00, 9, 12, 50, 'BULTO', TRUE, 15, 0.7),
        ('Urea granulada 46% 50kg', 'Fertilizantes', 'Fertilizante nitrogenado. [DEMO]', 98000.00, 150, 40, 730, 'BULTO', TRUE, 8, 2.2),
        ('Triple 15 50kg', 'Fertilizantes', 'NPK 15-15-15. [DEMO]', 105000.00, 80, 25, 730, 'BULTO', TRUE, 8, 1.8),
        ('DAP 18-46-0 50kg', 'Fertilizantes', 'Fosfato diamónico. [DEMO]', 118000.00, 12, 15, 730, 'BULTO', TRUE, 10, 1.1),
        ('Cloruro de potasio 50kg', 'Fertilizantes', 'Fuente de potasio. [DEMO]', 92000.00, 55, 20, 730, 'BULTO', TRUE, 10, 1.0),
        ('Compost orgánico 40kg', 'Fertilizantes', 'Abono orgánico compostado. [DEMO]', 28000.00, 90, 25, CAST(NULL AS INTEGER), 'BULTO', FALSE, 6, 2.0),
        ('Humus de lombriz 20kg', 'Fertilizantes', 'Humus sólido. [DEMO]', 32000.00, 40, 10, 365, 'BOLSA', TRUE, 8, 0.8),
        ('Gallinaza compostada 40kg', 'Fertilizantes', 'Abono de gallinaza. [DEMO]', 18000.00, 8, 15, 90, 'BULTO', TRUE, 7, 1.4),
        ('Cal dolomita 50kg', 'Fertilizantes', 'Corrector de acidez. [DEMO]', 24000.00, 110, 30, CAST(NULL AS INTEGER), 'BULTO', FALSE, 8, 1.2),
        ('Agrimins 1kg', 'Fertilizantes', 'Elementos menores. [DEMO]', 15000.00, 25, 8, 365, 'BOLSA', TRUE, 10, 0.4),
        ('Sulfato de magnesio 1kg', 'Fertilizantes', 'Sal de Epsom agrícola. [DEMO]', 8500.00, 40, 10, 800, 'BOLSA', TRUE, 8, 0.5),
        ('Aceite agrícola 1L', 'Fertilizantes', 'Aceite para mezclas foliares. [DEMO]', 14000.00, 28, 10, 540, 'LITRO', TRUE, 7, 0.6),
        ('Ivermectina 1% 50ml', 'Medicina', 'Desparasitante inyectable. [DEMO]', 18500.00, 4, 10, 8, 'FRASCO', TRUE, 8, 0.5),
        ('Albendazol 10% 1L', 'Medicina', 'Desparasitante oral. [DEMO]', 32000.00, 7, 6, 18, 'LITRO', TRUE, 8, 0.4),
        ('Fenbendazol bolos x10', 'Medicina', 'Bolos desparasitantes. [DEMO]', 22000.00, 3, 8, -5, 'CAJA', TRUE, 10, 0.3),
        ('Complejo B 100ml', 'Medicina', 'Vitamina B inyectable. [DEMO]', 12500.00, 20, 8, 12, 'FRASCO', TRUE, 6, 0.7),
        ('AD3E inyectable 100ml', 'Medicina', 'Vitaminas A D3 E. [DEMO]', 16800.00, 15, 8, 22, 'FRASCO', TRUE, 6, 0.5),
        ('Calcio inyectable 100ml', 'Medicina', 'Gluconato de calcio. [DEMO]', 14500.00, 9, 8, 6, 'FRASCO', TRUE, 7, 0.4),
        ('Vacuna Newcastle 1000 dosis', 'Medicina', 'Vacuna aviar Newcastle. [DEMO]', 38000.00, 2, 5, 4, 'FRASCO', TRUE, 12, 0.2),
        ('Oxitocina 50ml', 'Medicina', 'Hormona uterina. [DEMO]', 9800.00, 11, 5, 200, 'FRASCO', TRUE, 10, 0.2),
        ('Enrofloxacina 10% 100ml', 'Medicina', 'Antibiótico de amplio espectro. [DEMO]', 24500.00, 6, 8, 25, 'FRASCO', TRUE, 8, 0.4),
        ('Ketoprofeno 100ml', 'Medicina', 'Antiinflamatorio. [DEMO]', 21000.00, 14, 6, 80, 'FRASCO', TRUE, 8, 0.3),
        ('Suero oral sobres x10', 'Medicina', 'Sales de rehidratación. [DEMO]', 8500.00, 50, 15, 45, 'CAJA', TRUE, 5, 0.8),
        ('Alcohol yodado 1L', 'Medicina', 'Antiséptico. [DEMO]', 12000.00, 30, 10, 400, 'LITRO', TRUE, 5, 0.6),
        ('Yodo povidona 1L', 'Medicina', 'Antiséptico quirúrgico. [DEMO]', 13500.00, 8, 8, 15, 'LITRO', TRUE, 5, 0.5),
        ('Probiótico aviar 1L', 'Insumos Veterinarios', 'Probiótico para aves. [DEMO]', 38000.00, 12, 8, 30, 'LITRO', TRUE, 8, 0.4),
        ('Electrolitos aves 500g', 'Insumos Veterinarios', 'Electrolitos hidratantes. [DEMO]', 16000.00, 5, 10, 10, 'BOLSA', TRUE, 6, 0.6),
        ('Semilla maíz ICA V-156', 'Semillas', 'Maíz híbrido regional. [DEMO]', 42000.00, 60, 20, 365, 'BOLSA', TRUE, 15, 0.9),
        ('Semilla arroz Fedearroz', 'Semillas', 'Arroz secano. [DEMO]', 38000.00, 45, 15, 365, 'BOLSA', TRUE, 15, 0.7),
        ('Semilla Brachiaria 1kg', 'Semillas', 'Pasto Brachiaria. [DEMO]', 29000.00, 22, 10, 540, 'BOLSA', TRUE, 12, 0.5),
        ('Semilla frijol cargamanto', 'Semillas', 'Frijol para siembra. [DEMO]', 18000.00, 10, 12, 120, 'BOLSA', TRUE, 10, 0.6),
        ('Semilla papa parda pastusa', 'Semillas', 'Papa certificada. [DEMO]', 25000.00, 5, 10, 90, 'BOLSA', TRUE, 12, 0.4),
        ('Semilla ahuyama 50g', 'Semillas', 'Ahuyama criolla. [DEMO]', 6500.00, 40, 10, 200, 'SOBRE', TRUE, 8, 0.3),
        ('Semilla pimentón 50g', 'Semillas', 'Pimentón california. [DEMO]', 8900.00, 18, 8, 200, 'SOBRE', TRUE, 8, 0.3),
        ('Cipermetrina 20% 1L', 'Insecticidas', 'Piretroide de contacto. [DEMO]', 28000.00, 16, 8, 400, 'LITRO', TRUE, 8, 0.7),
        ('Clorpirifos 4E 1L', 'Insecticidas', 'Organofosforado. [DEMO]', 31000.00, 4, 6, 20, 'LITRO', TRUE, 10, 0.5),
        ('Imidacloprid 350 250ml', 'Insecticidas', 'Neonicotinoide. [DEMO]', 45000.00, 9, 5, 90, 'FRASCO', TRUE, 10, 0.3),
        ('Lambda cihalotrina 100ml', 'Insecticidas', 'Piretroide de alta concentración. [DEMO]', 22000.00, 13, 6, 180, 'FRASCO', TRUE, 8, 0.3),
        ('Mancozeb 80 WP 1kg', 'Fungicidas', 'Fungicida preventivo. [DEMO]', 19000.00, 35, 10, 365, 'BOLSA', TRUE, 8, 0.8),
        ('Azoxistrobina 250ml', 'Fungicidas', 'Estrobilurina. [DEMO]', 52000.00, 7, 4, 50, 'FRASCO', TRUE, 10, 0.2),
        ('Glifosato 480 20L', 'Herbicidas', 'Herbicida no selectivo. [DEMO]', 165000.00, 25, 8, 730, 'CANECA', TRUE, 8, 0.9),
        ('2,4-D amina 1L', 'Herbicidas', 'Herbicida hormonal. [DEMO]', 18500.00, 6, 8, 25, 'LITRO', TRUE, 8, 0.5),
        ('Atrazina 90 WG 1kg', 'Herbicidas', 'Herbicida residual maíz. [DEMO]', 24000.00, 20, 8, 400, 'BOLSA', TRUE, 10, 0.4),
        ('Paladraga mango madera', 'Herramientas agrícolas', 'Paladraga forjada. [DEMO]', 28000.00, 40, 10, CAST(NULL AS INTEGER), 'UNIDAD', FALSE, 12, 0.2),
        ('Machete pulido 22 pulgadas', 'Herramientas agrícolas', 'Machete agrícola. [DEMO]', 22000.00, 55, 15, CAST(NULL AS INTEGER), 'UNIDAD', FALSE, 10, 0.3),
        ('Palín de punta', 'Herramientas agrícolas', 'Palín para suelo duro. [DEMO]', 18000.00, 12, 8, CAST(NULL AS INTEGER), 'UNIDAD', FALSE, 12, 0.2),
        ('Azadón forjado', 'Herramientas agrícolas', 'Azadón de labranza. [DEMO]', 25000.00, 8, 6, CAST(NULL AS INTEGER), 'UNIDAD', FALSE, 12, 0.2),
        ('Carretilla 90 litros', 'Herramientas agrícolas', 'Carretilla de obra y campo. [DEMO]', 145000.00, 10, 4, CAST(NULL AS INTEGER), 'UNIDAD', FALSE, 15, 0.1),
        ('Manguera media pulgada 50m', 'Herramientas agrícolas', 'Manguera de riego. [DEMO]', 48000.00, 22, 8, CAST(NULL AS INTEGER), 'ROLLO', FALSE, 8, 0.3),
        ('Aspersora manual 20L', 'Herramientas agrícolas', 'Bomba de espalda. [DEMO]', 89000.00, 6, 5, CAST(NULL AS INTEGER), 'UNIDAD', FALSE, 12, 0.2),
        ('Guantes nitrilo caja x100', 'Herramientas agrícolas', 'Guantes desechables. [DEMO]', 35000.00, 3, 8, CAST(NULL AS INTEGER), 'CAJA', FALSE, 8, 0.8),
        ('Botas caucho agro talla 40', 'Herramientas agrícolas', 'Botas de caña. [DEMO]', 42000.00, 18, 10, CAST(NULL AS INTEGER), 'PAR', FALSE, 10, 0.3),
        ('Lima triangular machete', 'Herramientas agrícolas', 'Lima para afilar. [DEMO]', 6500.00, 80, 20, CAST(NULL AS INTEGER), 'UNIDAD', FALSE, 8, 0.4),
        ('Cinta de injerto 50m', 'Herramientas agrícolas', 'Cinta para injertos. [DEMO]', 12000.00, 24, 8, CAST(NULL AS INTEGER), 'ROLLO', FALSE, 10, 0.2),
        ('Trampa ratones x4', 'Toxicológica', 'Trampa mecánica. [DEMO]', 9800.00, 15, 8, CAST(NULL AS INTEGER), 'CAJA', FALSE, 8, 0.3),
        ('Cebada en grano 40kg', 'Granos', 'Cebada para suplemento. [DEMO]', 72000.00, 33, 15, 150, 'BULTO', TRUE, 10, 1.1),
        ('Maíz quebrado 40kg', 'Granos', 'Maíz para mezclas. [DEMO]', 68000.00, 48, 20, 180, 'BULTO', TRUE, 8, 1.5)
)
INSERT INTO productos (
    nombre, descripcion, precio, stock_actual, stock_minimo, fecha_vencimiento,
    categoria_id, estado, unidad_medida, perecedero, lead_time_dias,
    consumo_promedio_diario, stock_seguridad, punto_reorden
)
SELECT
    c.nombre,
    c.descripcion,
    c.precio,
    c.stock_actual,
    c.stock_minimo,
    CASE WHEN c.venc_dias IS NULL THEN NULL ELSE CURRENT_DATE + c.venc_dias END,
    cat.id,
    'ACTIVO',
    c.unidad,
    c.perecedero,
    c.lead_time,
    c.consumo,
    CEIL(c.consumo * c.lead_time)::INTEGER,
    CEIL(c.consumo * c.lead_time)::INTEGER + c.stock_minimo
FROM catalogo c
JOIN categorias cat ON LOWER(cat.nombre) = LOWER(c.categoria)
WHERE NOT EXISTS (
    SELECT 1 FROM productos p WHERE LOWER(p.nombre) = LOWER(c.nombre)
);

-- Precios de demostración para el catálogo V5 que quedó en cero (no cambia stock ni nombres).
UPDATE productos p
SET precio = (
    CASE
        WHEN p.nombre IN ('PONEDORA', 'CERDOS', 'GANADERIA', 'POLLOS') THEN 98000
        WHEN p.nombre ILIKE '%litro%' OR p.nombre ILIKE '%ml%' THEN 28000
        WHEN p.nombre IN ('Zanahoria', 'Tomate', 'Lechuga', 'Cilantro', 'Pepino', 'Cebolla', 'Arveja') THEN 8500
        ELSE 22000
    END + (p.id * 173) % 40000
)::numeric(12, 2)
WHERE p.precio = 0;

-- Histórico diario de 180 días solo para productos de demostración (el V8 de los 32 originales no se toca).
INSERT INTO inventario_diario (
    producto_id, fecha, stock_inicial, entradas, salidas, stock_final, dias_stockout
)
SELECT
    p.id,
    (CURRENT_DATE - (180 - gs.n))::date,
    GREATEST(0, p.stock_actual + ((180 - gs.n) / 4)),
    CASE WHEN gs.n % 14 = 0 THEN GREATEST(p.stock_minimo, 5) ELSE 0 END,
    GREATEST(
        0,
        ROUND((
            GREATEST(p.consumo_promedio_diario, 0.2)
            + CASE (p.id % 6)
                WHEN 0 THEN (gs.n / 50.0)
                WHEN 1 THEN GREATEST(0, 1.8 - (gs.n / 90.0))
                WHEN 2 THEN 2.2 * sin(gs.n / 5.0)
                WHEN 3 THEN CASE WHEN gs.n % 6 IN (1, 2) THEN 3.5 ELSE 0.2 END
                ELSE 0.4 * sin(gs.n / 9.0 + p.id)
              END
        )::numeric)
    )::int,
    GREATEST(p.stock_actual, 0),
    CASE WHEN p.stock_actual = 0 THEN 1 ELSE 0 END
FROM productos p
CROSS JOIN generate_series(1, 180) AS gs(n)
WHERE p.descripcion LIKE '%[DEMO]%'
ON CONFLICT (producto_id, fecha) DO NOTHING;

-- Alertas con las mismas reglas de negocio (stock <= mínimo; vence en 30 días o ya venció).
INSERT INTO alertas (producto_id, tipo_alerta, mensaje, estado, fecha_generacion)
SELECT
    p.id,
    'STOCK_BAJO',
    format('Stock bajo en ''%s'': actual=%s, mínimo=%s', p.nombre, p.stock_actual, p.stock_minimo),
    'PENDIENTE',
    CURRENT_TIMESTAMP
FROM productos p
WHERE p.estado = 'ACTIVO'
  AND p.stock_actual <= p.stock_minimo
  AND NOT EXISTS (
      SELECT 1 FROM alertas a
      WHERE a.producto_id = p.id AND a.tipo_alerta = 'STOCK_BAJO' AND a.estado = 'PENDIENTE'
  );

INSERT INTO alertas (producto_id, tipo_alerta, mensaje, estado, fecha_generacion)
SELECT
    p.id,
    'VENCIMIENTO_PROXIMO',
    CASE
        WHEN p.fecha_vencimiento < CURRENT_DATE THEN
            format('El producto ''%s'' está vencido desde hace %s día(s) (%s)',
                   p.nombre, (CURRENT_DATE - p.fecha_vencimiento), p.fecha_vencimiento)
        WHEN p.fecha_vencimiento = CURRENT_DATE THEN
            format('El producto ''%s'' vence hoy (%s)', p.nombre, p.fecha_vencimiento)
        ELSE
            format('El producto ''%s'' vence en %s día(s) (%s)',
                   p.nombre, (p.fecha_vencimiento - CURRENT_DATE), p.fecha_vencimiento)
    END,
    'PENDIENTE',
    CURRENT_TIMESTAMP
FROM productos p
WHERE p.estado = 'ACTIVO'
  AND p.fecha_vencimiento IS NOT NULL
  AND p.fecha_vencimiento <= CURRENT_DATE + 30
  AND NOT EXISTS (
      SELECT 1 FROM alertas a
      WHERE a.producto_id = p.id AND a.tipo_alerta = 'VENCIMIENTO_PROXIMO' AND a.estado = 'PENDIENTE'
  );
