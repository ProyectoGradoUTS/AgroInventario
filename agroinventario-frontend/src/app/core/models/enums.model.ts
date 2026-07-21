/** Valores exactos del backend (EstadoGeneral). */
export type EstadoGeneral = 'ACTIVO' | 'INACTIVO';

/** Valores exactos del backend (TipoMovimiento). */
export type TipoMovimiento = 'ENTRADA' | 'SALIDA';

/** Valores exactos del backend (TipoAlerta). */
export type TipoAlerta = 'STOCK_BAJO' | 'VENCIMIENTO_PROXIMO';

/** Valores exactos del backend (EstadoAlerta). */
export type EstadoAlerta = 'PENDIENTE' | 'LEIDA' | 'RESUELTA';

/** Valores exactos del backend (EntidadAuditoria). */
export type EntidadAuditoria =
  | 'PRODUCTO'
  | 'CATEGORIA'
  | 'INVENTARIO'
  | 'ALERTA'
  | 'USUARIO';

/** Valores exactos del backend (TipoAccionAuditoria). */
export type TipoAccionAuditoria =
  | 'CREAR'
  | 'ACTUALIZAR'
  | 'ELIMINAR'
  | 'CAMBIAR_ESTADO'
  | 'MOVIMIENTO_INVENTARIO'
  | 'ALERTA_GENERADA'
  | 'ALERTA_ACTUALIZADA'
  | 'PROCESO_AUTOMATICO';

/** Roles del sistema (sin prefijo ROLE_). */
export type RolNombre = 'ADMIN' | 'EMPLEADO';
