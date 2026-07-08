-- =============================================================================
-- Agro Inventario - Esquema inicial PostgreSQL (referencia Fase 1)
-- Se ejecuta automáticamente al crear el contenedor Docker por primera vez.
-- En Fase 2 las entidades JPA mapearán estas tablas.
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ---------------------------------------------------------------------------
-- Seguridad / usuarios
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuarios (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    estado          VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
                    CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    fecha_creacion  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usuarios_roles (
    usuario_id  BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    rol_id      BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, rol_id)
);

-- ---------------------------------------------------------------------------
-- Catálogo de productos
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categorias (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS productos (
    id                  BIGSERIAL PRIMARY KEY,
    nombre              VARCHAR(200) NOT NULL,
    descripcion         VARCHAR(1000),
    precio              NUMERIC(12, 2) NOT NULL CHECK (precio >= 0),
    stock_actual        INTEGER NOT NULL DEFAULT 0 CHECK (stock_actual >= 0),
    stock_minimo        INTEGER NOT NULL DEFAULT 0 CHECK (stock_minimo >= 0),
    fecha_vencimiento   DATE,
    categoria_id        BIGINT NOT NULL REFERENCES categorias(id),
    estado              VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
                        CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    fecha_creacion      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------------
-- Inventario y alertas
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id              BIGSERIAL PRIMARY KEY,
    producto_id     BIGINT NOT NULL REFERENCES productos(id),
    tipo_movimiento VARCHAR(20) NOT NULL
                    CHECK (tipo_movimiento IN ('ENTRADA', 'SALIDA')),
    cantidad        INTEGER NOT NULL CHECK (cantidad > 0),
    descripcion     VARCHAR(500),
    usuario_id      BIGINT NOT NULL REFERENCES usuarios(id),
    fecha_movimiento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS alertas (
    id                  BIGSERIAL PRIMARY KEY,
    producto_id         BIGINT NOT NULL REFERENCES productos(id),
    tipo_alerta         VARCHAR(50) NOT NULL,
    mensaje             VARCHAR(500) NOT NULL,
    estado              VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                        CHECK (estado IN ('PENDIENTE', 'LEIDA', 'RESUELTA')),
    fecha_generacion    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------------
-- Índices de rendimiento
-- ---------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_productos_categoria ON productos(categoria_id);
CREATE INDEX IF NOT EXISTS idx_productos_estado ON productos(estado);
CREATE INDEX IF NOT EXISTS idx_movimientos_producto ON movimientos_inventario(producto_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_fecha ON movimientos_inventario(fecha_movimiento);
CREATE INDEX IF NOT EXISTS idx_alertas_producto ON alertas(producto_id);

-- ---------------------------------------------------------------------------
-- Auditoría básica (Fase 5)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS auditoria (
    id              BIGSERIAL PRIMARY KEY,
    entidad         VARCHAR(50) NOT NULL,
    entidad_id      BIGINT,
    accion          VARCHAR(50) NOT NULL,
    detalle         VARCHAR(1000),
    usuario_id      BIGINT REFERENCES usuarios(id),
    usuario_email   VARCHAR(150),
    fecha_evento    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_auditoria_entidad ON auditoria(entidad);
CREATE INDEX IF NOT EXISTS idx_auditoria_fecha ON auditoria(fecha_evento);

-- ---------------------------------------------------------------------------
-- Datos semilla (roles)
-- ---------------------------------------------------------------------------
INSERT INTO roles (nombre) VALUES ('ADMIN'), ('EMPLEADO')
ON CONFLICT (nombre) DO NOTHING;
