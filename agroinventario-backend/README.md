# Agro Inventario API

Backend del proyecto de grado: **Asistente inteligente para la gestión automatizada de inventarios** en una empresa agropecuaria de Bucaramanga.

## Stack (Fase 1)

| Tecnología        | Versión / nota        |
|-------------------|------------------------|
| Java              | 21 (LTS)               |
| Spring Boot       | 3.4.x                  |
| Maven             | Wrapper o instalación local |
| PostgreSQL        | 16                     |
| Arquitectura      | Hexagonal (Ports & Adapters) |

## Estructura del proyecto

```
com.agroinventario
├── domain/              # Núcleo: modelos, puertos, reglas de negocio
├── application/         # Casos de uso, DTOs, mappers
└── infrastructure/      # REST, JPA, Security, config
```

## Requisitos previos

1. **JDK 21+** (tienes Java 25 instalado — compatible)
2. **Maven 3.9+** o usar el wrapper (`mvnw.cmd`)
3. **Docker Desktop** (opcional, para PostgreSQL local)

## Inicio rápido

### 1. Base de datos con Docker

```powershell
cd agroinventario-backend
docker compose up -d
```

### 2. Variables de entorno

```powershell
Copy-Item .env.example .env
```

### 3. Ejecutar la aplicación

Desde IntelliJ: ejecutar `AgroInventarioApplication`.

O con Maven Wrapper (recomendado):

```powershell
.\mvnw.cmd spring-boot:run
```

O con Maven instalado globalmente:

```powershell
mvn spring-boot:run
```

### 4. Verificar

- Health custom: `GET http://localhost:8080/api/v1/health`
- Actuator: `GET http://localhost:8080/api/actuator/health`
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- OpenAPI JSON: `GET http://localhost:8080/api/v3/api-docs`

### 5. Probar la API con Swagger

1. Abre Swagger UI en el enlace anterior.
2. Ejecuta `POST /v1/auth/login` con el usuario admin de desarrollo.
3. Copia el `accessToken` de la respuesta.
4. Pulsa **Authorize** e ingresa: `Bearer <tu_token>`
5. Prueba el resto de endpoints protegidos.

> En perfil `prod` Swagger está deshabilitado por seguridad.

## Fases del proyecto

| Fase | Estado | Contenido |
|------|--------|-----------|
| 1 | ✅ Completada | Proyecto base, Maven, PostgreSQL, estructura hexagonal |
| 2 | ✅ Completada | Dominio, puertos, casos de uso, adaptadores JPA, API REST |
| 3 | ✅ Completada | Spring Security + JWT + roles + protección endpoints |
| 4 | ✅ Completada | Validaciones, excepciones, reglas negocio, paginación |
| 5 | ✅ Completada | Alertas stock/vencimiento, gestión de estados, auditoría, scheduler |
| 6 | ✅ Completada | Flyway, Maven Wrapper, gestión usuarios ADMIN, tests integración |

## Documentación adicional

- `docs/FASE-1-ARQUITECTURA.md` — Base del proyecto
- `docs/FASE-5-ALERTAS-Y-AUDITORIA.md` — Alertas y auditoría (Fase 5)

## Migraciones Flyway

El esquema se aplica al iniciar la app (`src/main/resources/db/migration/`).

Si ya tenías Docker con el esquema antiguo:

```powershell
docker compose down -v
docker compose up -d
```

Luego ejecuta la aplicación para que Flyway migre la BD.

## Tests

```powershell
.\mvnw.cmd test
```
