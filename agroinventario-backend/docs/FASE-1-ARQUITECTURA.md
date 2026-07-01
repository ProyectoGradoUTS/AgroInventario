# Fase 1 — Base del backend (Arquitectura Hexagonal)

## 1. Por qué Arquitectura Hexagonal

En un backend empresarial, el **dominio** (reglas de inventario, stock, alertas) debe sobrevivir a cambios de framework:

- Si mañana cambias PostgreSQL por otro motor, solo cambias adaptadores de salida.
- Si expones gRPC además de REST, añades otro adaptador de entrada sin tocar el dominio.
- Los casos de uso se prueban sin levantar Tomcat ni base de datos.

```
         [ REST / HTTP ]     ← Adaptador ENTRADA
                │
         [ Casos de uso ]    ← APPLICATION
                │
    [ Dominio + Puertos ]   ← DOMAIN (núcleo)
                │
    [ JPA / PostgreSQL ]    ← Adaptador SALIDA
```

## 2. Capas y responsabilidades

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| Dominio | `domain` | Modelos puros, puertos, reglas de negocio. **Sin Spring.** |
| Aplicación | `application` | Orquesta casos de uso, DTOs, mappers. **Sin HTTP ni JPA.** |
| Infraestructura | `infrastructure` | REST, entidades JPA, Security, YAML, excepciones HTTP. |

### Regla de dependencia (SOLID)

Las dependencias **siempre apuntan hacia el dominio**:

- `infrastructure` → `application` → `domain`
- `domain` **nunca** importa `infrastructure` ni `application`

## 3. Maven (`pom.xml`)

- **Parent Spring Boot 3.4.5**: gestión de versiones compatibles.
- **Java 21**: bytecode y APIs modernas (records, pattern matching).
- **starters**: `web`, `data-jpa`, `validation`, `actuator`, `postgresql`.
- **Lombok + MapStruct**: preparados para Fase 2 (no usados aún en código).
- **Security/JWT**: omitidos en Fase 1; se añaden en Fase 3.

## 4. PostgreSQL y `schema.sql`

El archivo `src/main/resources/db/schema.sql` define el modelo relacional acordado:

- Normalización con tablas `usuarios`, `roles`, `usuarios_roles`.
- `productos` referencia `categorias`.
- `movimientos_inventario` audita entradas/salidas.
- `alertas` para stock bajo (Fase 5).
- Índices en columnas de consulta frecuente.
- Roles semilla: `ADMIN`, `EMPLEADO`.

Docker monta este script en `docker-entrypoint-initdb.d` la **primera vez** que se crea el volumen.

## 5. `application.yml`

Decisiones clave:

| Propiedad | Valor Fase 1 | Motivo |
|-----------|--------------|--------|
| `context-path: /api` | Prefijo global REST | Separación clara de rutas |
| `jpa.open-in-view: false` | Desactivado | Evita sesiones lazy en controllers (anti-patrón) |
| `ddl-auto: none` | Sin auto-DDL | El esquema lo controla SQL/Flyway, no Hibernate |
| Variables `${DB_*}` | Desde entorno | 12-factor app, sin secretos en Git |

## 6. Endpoint de salud (`HealthController`)

Ubicado en **adaptador de entrada** (`infrastructure.adapters.input.rest`):

- No contiene lógica de negocio.
- Solo confirma que Spring MVC y propiedades cargan bien.
- Ruta: `GET /api/v1/health`

## 7. Próximo paso (Fase 2)

Cuando confirmes que Fase 1 compila y arranca con PostgreSQL:

1. Modelos en `domain.model` (sin JPA).
2. Puertos en `domain.ports.*`.
3. Casos de uso en `application.usecase`.
4. Entidades JPA + adaptadores en `infrastructure.adapters.output.persistence`.
