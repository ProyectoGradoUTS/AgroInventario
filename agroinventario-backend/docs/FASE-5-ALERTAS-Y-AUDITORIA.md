# Fase 5 — Alertas inteligentes y auditoría básica

## Objetivo

Completar el ciclo de **alertas de inventario** (stock bajo y vencimiento próximo) y registrar una **trazabilidad básica** de operaciones críticas del sistema.

## Componentes implementados

### 1. Motor de alertas por producto

Caso de uso: `ProcesarAlertasProductoUseCase`

Se ejecuta automáticamente cuando:

- Se crea un producto
- Se actualiza un producto
- Se cambia el estado de un producto
- Se registra un movimiento de inventario

Reglas:

| Tipo | Condición | Acción |
|------|-----------|--------|
| `STOCK_BAJO` | `stock_actual <= stock_minimo` y producto activo | Crea alerta `PENDIENTE` si no existe |
| `VENCIMIENTO_PROXIMO` | Fecha de vencimiento dentro de N días | Crea alerta `PENDIENTE` si no existe |
| Cualquiera | Condición ya no aplica | Resuelve alerta `PENDIENTE` automáticamente |
| Producto `INACTIVO` | — | Resuelve alertas pendientes |

Configuración (`application.yml`):

```yaml
app:
  alertas:
    dias-anticipacion-vencimiento: 30
    cron-vencimiento: "0 0 6 * * *"
```

### 2. Proceso programado de vencimiento

`AlertaVencimientoScheduler` ejecuta diariamente (6:00 AM, zona `America/Bogota`) el caso de uso `ProcesarAlertasVencimientoProgramadasUseCase`, revisando productos activos con fecha de vencimiento próxima o vencida.

### 3. Gestión manual de alertas

```http
PATCH /api/v1/alertas/{id}/estado
Authorization: Bearer <token>
Content-Type: application/json

{ "estado": "LEIDA" }
```

Transiciones válidas:

- `PENDIENTE` → `LEIDA` o `RESUELTA`
- `LEIDA` → `RESUELTA`
- `RESUELTA` → (ninguna)

Roles: `ADMIN`, `EMPLEADO`

### 4. Auditoría básica

Tabla `auditoria` registra:

- Entidad afectada (`PRODUCTO`, `INVENTARIO`, `ALERTA`, etc.)
- Acción (`CREAR`, `MOVIMIENTO_INVENTARIO`, `ALERTA_GENERADA`, ...)
- Detalle legible
- Usuario (si hay sesión JWT)
- Fecha del evento

Consulta (solo `ADMIN`):

```http
GET /api/v1/auditoria?page=0&size=20&entidad=PRODUCTO
Authorization: Bearer <token_admin>
```

### 5. Mejora arquitectónica

Los servicios de dominio (`AlertaDomainService`, `InventarioDomainService`) ya **no dependen de Spring** (`@Component` eliminado). Se registran como beans en `DomainServiceConfig`, respetando mejor la arquitectura hexagonal.

## Migración de base de datos

Si ya tenías PostgreSQL con Docker desde Fase 1, ejecuta en tu BD:

```sql
-- Ver schema.sql sección auditoria
```

O recrea el volumen:

```powershell
docker compose down -v
docker compose up -d
```

## Ejemplos de prueba

### Listar alertas pendientes

```http
GET /api/v1/alertas?estado=PENDIENTE
Authorization: Bearer <token>
```

### Crear producto con stock bajo (genera alerta)

```http
POST /api/v1/productos
Authorization: Bearer <token_admin>
Content-Type: application/json

{
  "nombre": "Semilla maíz",
  "descripcion": "Semilla híbrida",
  "precio": 120000,
  "stockActual": 3,
  "stockMinimo": 10,
  "fechaVencimiento": "2026-08-01",
  "categoriaId": 1,
  "estado": "ACTIVO"
}
```

Luego consulta `GET /api/v1/alertas?estado=PENDIENTE`.

## Próximos pasos sugeridos (fuera de Fase 5)

- Notificaciones por email/WhatsApp
- Dashboard con métricas agregadas
- Análisis predictivo de demanda
- Integración con modelos de IA
