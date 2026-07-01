# Fase 2 — Dominio, puertos, casos de uso y persistencia

## Flujo hexagonal implementado

```
REST Controller  →  Use Case (application)  →  Port Output  →  Persistence Adapter  →  JPA
       ↑                      ↑
    DTO/MapStruct         Domain Model + Domain Service
```

## Capa DOMAIN

| Elemento | Ubicación | Responsabilidad |
|----------|-----------|-----------------|
| Modelos | `domain.model` | Records puros: `Producto`, `Categoria`, etc. |
| Puertos entrada | `domain.ports.input.*` | Contratos de casos de uso |
| Puertos salida | `domain.ports.output` | Contratos de persistencia |
| Servicios dominio | `domain.service` | Reglas: stock, alertas |
| Excepciones | `domain.exception` | Errores de negocio |

## Capa APPLICATION

| Elemento | Ubicación |
|----------|-----------|
| Implementación casos de uso | `application.usecase.*` |
| DTOs REST | `application.dto.request/response` |
| Mappers DTO | `application.mapper` (MapStruct) |

## Capa INFRASTRUCTURE

| Elemento | Ubicación |
|----------|-----------|
| Entidades JPA | `infrastructure...persistence.entity` |
| Repositories Spring Data | `infrastructure...persistence.repository` |
| Mappers Entity | `infrastructure...persistence.mapper` |
| Adaptadores | `infrastructure...persistence.adapter` |
| REST | `infrastructure.adapters.input.rest` |

## API REST (Fase 2)

Base: `http://localhost:8080/api`

### Categorías
- `POST /v1/categorias`
- `GET /v1/categorias`
- `GET /v1/categorias/{id}`
- `PUT /v1/categorias/{id}`
- `DELETE /v1/categorias/{id}`

### Productos
- `POST /v1/productos`
- `GET /v1/productos?estado=ACTIVO`
- `GET /v1/productos/{id}`
- `PUT /v1/productos/{id}`
- `DELETE /v1/productos/{id}`

### Inventario
- `POST /v1/inventario/productos/{productoId}/movimientos`
- `GET /v1/inventario/movimientos?productoId=1`

### Alertas
- `GET /v1/alertas?estado=PENDIENTE`

## Usuario de prueba (dev)

Tras arrancar con perfil `dev`, existe:
- Email: `admin@agroinventario.local`
- ID: normalmente `1`
- Usar `usuarioId: 1` en movimientos de inventario hasta Fase 3.

## Ejemplo: registrar entrada

```json
POST /api/v1/inventario/productos/1/movimientos
{
  "tipoMovimiento": "ENTRADA",
  "cantidad": 50,
  "descripcion": "Compra proveedor",
  "usuarioId": 1
}
```

Si el stock queda bajo el mínimo, se genera alerta `STOCK_BAJO` automáticamente.
