-- Flyway V4: prepara el modelo de datos para históricos, lotes y predicción.
-- Esta migración es aditiva: conserva los datos operativos existentes.

CREATE TABLE proveedores (
    id                  BIGSERIAL PRIMARY KEY,
    nombre              VARCHAR(200) NOT NULL,
    identificacion      VARCHAR(50),
    email               VARCHAR(150),
    telefono            VARCHAR(50),
    dias_lead_time      INTEGER NOT NULL DEFAULT 0 CHECK (dias_lead_time >= 0),
    estado              VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
                        CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    fecha_creacion      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_proveedores_nombre UNIQUE (nombre)
);

CREATE TABLE producto_proveedor (
    producto_id         BIGINT NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
    proveedor_id        BIGINT NOT NULL REFERENCES proveedores(id),
    es_principal        BOOLEAN NOT NULL DEFAULT FALSE,
    costo_referencia    NUMERIC(12, 2) CHECK (costo_referencia >= 0),
    lead_time_dias      INTEGER CHECK (lead_time_dias >= 0),
    fecha_asignacion    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (producto_id, proveedor_id)
);

ALTER TABLE productos
    ADD COLUMN unidad_medida VARCHAR(30) NOT NULL DEFAULT 'UNIDAD',
    ADD COLUMN perecedero BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN lead_time_dias INTEGER NOT NULL DEFAULT 0 CHECK (lead_time_dias >= 0),
    ADD COLUMN consumo_promedio_diario NUMERIC(12, 4) NOT NULL DEFAULT 0
        CHECK (consumo_promedio_diario >= 0),
    ADD COLUMN stock_seguridad INTEGER NOT NULL DEFAULT 0 CHECK (stock_seguridad >= 0),
    ADD COLUMN punto_reorden INTEGER NOT NULL DEFAULT 0 CHECK (punto_reorden >= 0);

CREATE TABLE lotes_producto (
    id                  BIGSERIAL PRIMARY KEY,
    producto_id         BIGINT NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
    proveedor_id        BIGINT REFERENCES proveedores(id),
    numero_lote         VARCHAR(100) NOT NULL,
    fecha_fabricacion   DATE,
    fecha_vencimiento   DATE,
    cantidad_inicial    INTEGER NOT NULL CHECK (cantidad_inicial >= 0),
    cantidad_disponible INTEGER NOT NULL DEFAULT 0 CHECK (cantidad_disponible >= 0),
    estado              VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE'
                        CHECK (estado IN ('DISPONIBLE', 'AGOTADO', 'VENCIDO', 'BLOQUEADO')),
    fecha_ingreso       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_lotes_producto_numero UNIQUE (producto_id, numero_lote),
    CONSTRAINT ck_lote_fechas CHECK (
        fecha_vencimiento IS NULL
        OR fecha_fabricacion IS NULL
        OR fecha_vencimiento >= fecha_fabricacion
    )
);

ALTER TABLE movimientos_inventario
    ADD COLUMN lote_id BIGINT REFERENCES lotes_producto(id),
    ADD COLUMN costo_unitario NUMERIC(12, 2) CHECK (costo_unitario >= 0),
    ADD COLUMN referencia_externa VARCHAR(100),
    ADD COLUMN stock_resultante INTEGER CHECK (stock_resultante >= 0);

CREATE TABLE inventario_diario (
    id                  BIGSERIAL PRIMARY KEY,
    producto_id         BIGINT NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
    fecha               DATE NOT NULL,
    stock_inicial       INTEGER NOT NULL CHECK (stock_inicial >= 0),
    entradas            INTEGER NOT NULL DEFAULT 0 CHECK (entradas >= 0),
    salidas             INTEGER NOT NULL DEFAULT 0 CHECK (salidas >= 0),
    stock_final         INTEGER NOT NULL CHECK (stock_final >= 0),
    dias_stockout       INTEGER NOT NULL DEFAULT 0 CHECK (dias_stockout >= 0),
    creado_en           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_inventario_diario_producto_fecha UNIQUE (producto_id, fecha)
);

CREATE TABLE predicciones_inventario (
    id                      BIGSERIAL PRIMARY KEY,
    producto_id             BIGINT NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
    fecha_prediccion        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_inicio             DATE NOT NULL,
    fecha_fin               DATE NOT NULL,
    horizonte_dias          INTEGER NOT NULL CHECK (horizonte_dias > 0),
    demanda_proyectada     NUMERIC(14, 4) NOT NULL CHECK (demanda_proyectada >= 0),
    consumo_diario_estimado NUMERIC(12, 4) NOT NULL CHECK (consumo_diario_estimado >= 0),
    fecha_agotamiento       DATE,
    stock_proyectado        NUMERIC(14, 4) NOT NULL CHECK (stock_proyectado >= 0),
    riesgo_desabastecimiento VARCHAR(20) NOT NULL
                            CHECK (riesgo_desabastecimiento IN ('BAJO', 'MEDIO', 'ALTO', 'CRITICO')),
    nivel_confianza         NUMERIC(5, 4) CHECK (nivel_confianza >= 0 AND nivel_confianza <= 1),
    modelo                  VARCHAR(100) NOT NULL,
    version_modelo          VARCHAR(50),
    variables_entrada       JSONB NOT NULL DEFAULT '{}'::jsonb,
    creado_en               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE recomendaciones_reposicion (
    id                      BIGSERIAL PRIMARY KEY,
    producto_id             BIGINT NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
    prediccion_id           BIGINT REFERENCES predicciones_inventario(id),
    fecha_generacion        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cantidad_recomendada    INTEGER NOT NULL CHECK (cantidad_recomendada > 0),
    fecha_sugerida          DATE NOT NULL,
    estrategia              VARCHAR(40) NOT NULL
                            CHECK (estrategia IN (
                                'STOCK_MINIMO',
                                'DEMANDA_LEAD_TIME',
                                'NIVELACION',
                                'URGENTE'
                            )),
    prioridad               VARCHAR(20) NOT NULL
                            CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA', 'CRITICA')),
    justificacion           VARCHAR(1000) NOT NULL,
    estado                  VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                            CHECK (estado IN ('PENDIENTE', 'ACEPTADA', 'RECHAZADA', 'EJECUTADA')),
    atendida_en             TIMESTAMP
);

CREATE TABLE ejecuciones_modelo_predictivo (
    id                  BIGSERIAL PRIMARY KEY,
    nombre_modelo       VARCHAR(100) NOT NULL,
    version_modelo      VARCHAR(50) NOT NULL,
    fecha_entrenamiento TIMESTAMP NOT NULL,
    fecha_inicio_datos  DATE NOT NULL,
    fecha_fin_datos     DATE NOT NULL,
    registros_entrenamiento INTEGER NOT NULL CHECK (registros_entrenamiento >= 0),
    registros_validacion INTEGER NOT NULL CHECK (registros_validacion >= 0),
    registros_prueba     INTEGER NOT NULL CHECK (registros_prueba >= 0),
    mae                 NUMERIC(14, 6),
    rmse                NUMERIC(14, 6),
    mape                NUMERIC(14, 6),
    parametros          JSONB NOT NULL DEFAULT '{}'::jsonb,
    estado              VARCHAR(20) NOT NULL DEFAULT 'COMPLETADO'
                        CHECK (estado IN ('EJECUTANDO', 'COMPLETADO', 'FALLIDO')),
    mensaje_error       VARCHAR(2000)
);

ALTER TABLE alertas
    ADD COLUMN prioridad VARCHAR(20) NOT NULL DEFAULT 'MEDIA'
        CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA', 'CRITICA')),
    ADD COLUMN origen VARCHAR(30) NOT NULL DEFAULT 'OPERATIVO'
        CHECK (origen IN ('OPERATIVO', 'PREDICTIVO')),
    ADD COLUMN prediccion_id BIGINT REFERENCES predicciones_inventario(id);

CREATE INDEX idx_proveedores_estado ON proveedores(estado);
CREATE INDEX idx_producto_proveedor_proveedor ON producto_proveedor(proveedor_id);
CREATE INDEX idx_lotes_producto ON lotes_producto(producto_id);
CREATE INDEX idx_lotes_vencimiento ON lotes_producto(fecha_vencimiento);
CREATE INDEX idx_movimientos_lote ON movimientos_inventario(lote_id);
CREATE INDEX idx_movimientos_producto_fecha
    ON movimientos_inventario(producto_id, fecha_movimiento);
CREATE INDEX idx_inventario_diario_fecha ON inventario_diario(fecha);
CREATE INDEX idx_predicciones_producto_fecha
    ON predicciones_inventario(producto_id, fecha_prediccion);
CREATE INDEX idx_predicciones_riesgo ON predicciones_inventario(riesgo_desabastecimiento);
CREATE INDEX idx_recomendaciones_estado_fecha
    ON recomendaciones_reposicion(estado, fecha_sugerida);
CREATE INDEX idx_alertas_origen_prioridad ON alertas(origen, prioridad);

CREATE UNIQUE INDEX uk_producto_proveedor_principal
    ON producto_proveedor(producto_id)
    WHERE es_principal = TRUE;
