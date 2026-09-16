export interface AsistenteConsultaRequest {
  pregunta: string;
}

export interface AsistenteConsultaResponse {
  respuesta: string;
  motor?: string;
  intencion?: string;
  metadatos?: Record<string, unknown> | null;
}

export interface AsistenteEstadoResponse {
  disponible: boolean;
  mensaje: string;
  llmActivo?: boolean;
  productosActivos?: number;
  stockBajo?: number;
  alertasPendientes?: number;
  registrosHistoricos?: number;
}

export interface AsistenteMensajeUi {
  id: string;
  rol: 'usuario' | 'asistente' | 'sistema';
  texto: string;
  fecha: string;
  motor?: string;
}
