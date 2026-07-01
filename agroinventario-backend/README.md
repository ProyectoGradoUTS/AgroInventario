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

O con Maven (si está instalado):

```powershell
mvn spring-boot:run
```

### 4. Verificar

- Health custom: `GET http://localhost:8080/api/v1/health`
- Actuator: `GET http://localhost:8080/api/actuator/health`

## Fases del proyecto

| Fase | Estado | Contenido |
|------|--------|-----------|
| 1 | ✅ Completada | Proyecto base, Maven, PostgreSQL, estructura hexagonal |
| 2 | ✅ Completada | Dominio, puertos, casos de uso, adaptadores JPA, API REST |
| 3 | ✅ Completada | Spring Security + JWT + roles + protección endpoints |
| 4 | ✅ Actual | Validaciones, excepciones, reglas negocio, paginación |
| 5 | Pendiente | Alertas, historial, auditoría |

## Documentación adicional

Ver `docs/FASE-1-ARQUITECTURA.md` para la explicación detallada paso a paso.
