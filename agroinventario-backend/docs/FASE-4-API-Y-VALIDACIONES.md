# Fase 4 — API REST profesional, validaciones y excepciones

## Mejoras implementadas

### 1. Validaciones Jakarta reforzadas
- Mensajes claros en español en todos los DTOs
- `@Validated` en controladores + `@Positive` en path variables
- `@FutureOrPresent` en fechas de vencimiento
- Validación de parámetros de query (`page`, `size`, `categoriaId`)

### 2. Manejo global de excepciones
| Excepción | HTTP |
|-----------|------|
| `ResourceNotFoundException` | 404 |
| `InvalidCredentialsException` | 401 |
| `EmailAlreadyExistsException` | 409 |
| `BusinessRuleException`, stock, categoría/producto con dependencias | 400 |
| `MethodArgumentNotValidException` | 400 + detalles por campo |
| `ConstraintViolationException` | 400 |
| `HttpMessageNotReadableException` | 400 |
| `DataIntegrityViolationException` | 409 |
| `AccessDeniedException` | 403 |
| `Exception` | 500 (con log interno) |

### 3. Reglas de negocio en eliminaciones
- No eliminar **categoría** si tiene productos → `CategoriaConProductosException`
- No eliminar **producto** si tiene movimientos → `ProductoConMovimientosException`

### 4. Inventario vinculado al JWT
- Ya no se envía `usuarioId` en el body
- El movimiento se registra con el usuario autenticado (`CurrentUserPort`)

### 5. Paginación y filtros de productos
```http
GET /api/v1/productos/paginado?page=0&size=20&estado=ACTIVO&categoriaId=1&nombre=urea
```

### 6. Cambio de estado sin borrar
```http
PATCH /api/v1/productos/{id}/estado
{ "estado": "INACTIVO" }
```

## Ejemplo movimiento de inventario (Fase 4)

```http
POST /api/v1/inventario/productos/1/movimientos
Authorization: Bearer <token>
Content-Type: application/json

{
  "tipoMovimiento": "ENTRADA",
  "cantidad": 10,
  "descripcion": "Reposición bodega"
}
```

## Formato de error estándar

```json
{
  "timestamp": "2026-05-20T22:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en el cuerpo de la petición",
  "path": "/api/v1/productos",
  "details": [
    "precio: El precio no puede ser negativo"
  ]
}
```
