# Fase 3 — Spring Security + JWT

## Arquitectura de autenticación (hexagonal)

| Capa | Componente | Rol |
|------|------------|-----|
| Dominio | `LoginUseCase`, `RegisterUsuarioUseCase` | Reglas de negocio auth |
| Dominio | `TokenProviderPort`, `PasswordEncoderPort` | Contratos técnicos |
| Aplicación | `LoginUseCaseImpl`, `RegisterUsuarioUseCaseImpl` | Orquestación |
| Infraestructura | `JwtTokenAdapter`, `BcryptPasswordEncoderAdapter` | Implementan puertos |
| Infraestructura | `JwtAuthenticationFilter`, `SecurityConfig` | Filtro y políticas HTTP |

El dominio **no importa** `io.jsonwebtoken` ni `BCrypt` directamente.

## Endpoints públicos

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/v1/auth/login` | Iniciar sesión |
| POST | `/api/v1/auth/register` | Registro (rol EMPLEADO) |
| GET | `/api/v1/health` | Health check |

## Endpoints autenticados

| Método | Ruta | Roles |
|--------|------|-------|
| GET | `/api/v1/auth/me` | ADMIN, EMPLEADO |
| GET | `/api/v1/**` | ADMIN, EMPLEADO |
| POST/PUT/DELETE | `/api/v1/categorias/**` | ADMIN |
| POST/PUT/DELETE | `/api/v1/productos/**` | ADMIN |
| POST | `/api/v1/inventario/**` | ADMIN, EMPLEADO |

## Uso del token

```http
Authorization: Bearer <token_jwt>
```

## Credenciales de desarrollo

| Campo | Valor |
|-------|-------|
| Email | `admin@agroinventario.local` |
| Password | `Admin123!` |
| Rol | ADMIN |

## Ejemplo login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "admin@agroinventario.local",
  "password": "Admin123!"
}
```

Respuesta:

```json
{
  "success": true,
  "message": "Inicio de sesión exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresInMs": 86400000,
    "usuarioId": 1,
    "email": "admin@agroinventario.local",
    "nombre": "Administrador Sistema",
    "roles": ["ADMIN"]
  }
}
```

## Variables de entorno

| Variable | Descripción |
|----------|-------------|
| `JWT_SECRET` | Clave HMAC ≥ 256 bits (obligatorio en producción) |
| `JWT_EXPIRATION_MS` | Duración del token (default 24h) |
