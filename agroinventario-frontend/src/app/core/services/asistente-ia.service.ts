import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  AsistenteConsultaRequest,
  AsistenteConsultaResponse,
  AsistenteEstadoResponse,
} from '../models';

/**
 * Cliente HTTP preparado para el Asistente IA.
 *
 * Hoy el backend no publica este módulo: las llamadas reales están deshabilitadas
 * hasta que `environment.asistenteIa.enabled` sea `true` y el path coincida
 * con el contrato definitivo de Spring Boot.
 */
@Injectable({ providedIn: 'root' })
export class AsistenteIaService {
  private readonly http = inject(HttpClient);
  private readonly config = environment.asistenteIa;

  /** Indica si el frontend está autorizado a llamar al backend de IA. */
  isEnabled(): boolean {
    return this.config.enabled === true;
  }

  /**
   * Estado local del módulo (no llama API mientras esté deshabilitado).
   * Cuando el backend exista, se puede reemplazar por GET real.
   */
  obtenerEstado(): Observable<AsistenteEstadoResponse> {
    if (!this.isEnabled()) {
      return throwError(() => ({
        status: 501,
        message:
          'El Asistente IA aún no está disponible en el backend. El módulo frontend está preparado.',
      }));
    }

    return this.http.get<AsistenteEstadoResponse>(`${this.baseUrl()}/estado`);
  }

  /**
   * Envía una consulta al asistente.
   * Ajustar request/response a los DTO oficiales cuando el backend los publique.
   */
  consultar(
    request: AsistenteConsultaRequest
  ): Observable<ApiResponse<AsistenteConsultaResponse>> {
    if (!this.isEnabled()) {
      return throwError(() => ({
        status: 501,
        message:
          'No se puede consultar el asistente: el endpoint todavía no está habilitado en el backend.',
      }));
    }

    return this.http.post<ApiResponse<AsistenteConsultaResponse>>(
      `${this.baseUrl()}/consultar`,
      request
    );
  }

  private baseUrl(): string {
    return `${environment.apiUrl}${this.config.basePath}`;
  }
}
