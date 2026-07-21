/**
 * Contratos PROVISIONALES del Asistente IA.
 *
 * IMPORTANTES:
 * - El backend AÚN NO expone endpoints de asistente.
 * - Estos tipos NO son DTOs oficiales.
 * - Cuando el backend publique el contrato, reemplazar estos campos
 *   para que coincidan exactamente con los DTO de Spring (nombres y tipos).
 */

export interface AsistenteConsultaRequest {
  /** Pregunta del usuario. Ajustar nombre al DTO real del backend. */
  pregunta: string;
}

export interface AsistenteConsultaResponse {
  /** Respuesta del asistente. Ajustar nombre al DTO real del backend. */
  respuesta: string;
  /** Metadatos opcionales futuros (fuentes, confianza, etc.). */
  metadatos?: Record<string, unknown> | null;
}

export interface AsistenteEstadoResponse {
  disponible: boolean;
  mensaje: string;
}

/** Mensaje de UI local (no proviene de la API). */
export interface AsistenteMensajeUi {
  id: string;
  rol: 'usuario' | 'asistente' | 'sistema';
  texto: string;
  fecha: string;
}
