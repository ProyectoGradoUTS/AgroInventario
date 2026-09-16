# Agro Inventario — Frontend

Angular 20 + Material 3 · ERP de inventario agropecuario.

## Requisitos

- Node.js 20+
- Backend en `http://localhost:8080/api`

## CORS (importante)

El backend permite por defecto `localhost:3000` y `5173`. Para Angular (`4200`) configura en el entorno del backend:

```env
CORS_ALLOWED_ORIGINS=http://localhost:4200,http://localhost:3000,http://localhost:5173
```

No se modifican endpoints ni DTOs; solo la variable de entorno.

## Arranque

```powershell
cd agroinventario-frontend
npm start
```

App: http://localhost:4200

## Estructura

```
src/app
├── core/           # auth, guards, interceptors, services, models
├── shared/         # layout, components, pipes, dialogs
└── features/       # módulos lazy (shells en Fase 2)
```

## Fases

| Fase | Estado |
|------|--------|
| 2 — Base, layout, auth infra | ✅ |
| 3 — Login API | ✅ |
| 4 — Dashboard | ✅ |
| 5 — Productos | ✅ |
| 6 — Categorías | ✅ |
| 7 — Inventario / Movimientos | ✅ |
| 8 — Alertas | ✅ |
| 9 — Auditoría | ✅ |
| 10 — Asistente IA | ✅ |

### Asistente IA

El chat en `/app/asistente-ia` consulta `GET /v1/asistente/estado` y `POST /v1/asistente/consultar`.
Las respuestas usan inventario, alertas y proyección de consumo. Opcionalmente un LLM si el backend tiene `IA_API_KEY`.

### Credenciales de desarrollo

| Campo | Valor |
|-------|-------|
| Email | `admin@agroinventario.local` |
| Password | `Admin123!` |

## Contrato API

- Base: `environment.apiUrl` → `http://localhost:8080/api`
- Token JWT: campo `data.token` (no `accessToken`)
- Header: `Authorization: Bearer <token>`
- Envelope: `ApiResponse<T>` / errores `ErrorResponse`
