import { EstadoAlerta, TipoAlerta } from './enums.model';

/** AlertaResponse del backend. */
export interface AlertaResponse {
  id: number;
  productoId: number;
  productoNombre: string;
  tipoAlerta: TipoAlerta;
  mensaje: string;
  estado: EstadoAlerta;
  fechaGeneracion: string;
}

/** ActualizarEstadoAlertaRequest del backend. */
export interface ActualizarEstadoAlertaRequest {
  estado: EstadoAlerta;
}
