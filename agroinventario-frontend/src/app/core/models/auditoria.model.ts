import { EntidadAuditoria, TipoAccionAuditoria } from './enums.model';

/** AuditoriaResponse del backend. */
export interface AuditoriaResponse {
  id: number;
  entidad: EntidadAuditoria;
  entidadId: number | null;
  accion: TipoAccionAuditoria;
  detalle: string | null;
  usuarioId: number | null;
  usuarioEmail: string | null;
  fechaEvento: string;
}

/** Query params de GET /v1/auditoria. */
export interface AuditoriaQuery {
  entidad?: EntidadAuditoria;
  page?: number;
  size?: number;
}
