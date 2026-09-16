import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  AsistenteConsultaRequest,
  AsistenteConsultaResponse,
  AsistenteEstadoResponse,
} from '../models';

@Injectable({ providedIn: 'root' })
export class AsistenteIaService {
  private readonly http = inject(HttpClient);
  private readonly config = environment.asistenteIa;

  isEnabled(): boolean {
    return this.config.enabled === true;
  }

  obtenerEstado(): Observable<AsistenteEstadoResponse> {
    if (!this.isEnabled()) {
      return throwError(() => ({
        status: 501,
        message: 'El Asistente IA está deshabilitado en el entorno.',
      }));
    }

    return this.http
      .get<ApiResponse<AsistenteEstadoResponse>>(`${this.baseUrl()}/estado`)
      .pipe(map((response) => response.data));
  }

  consultar(
    request: AsistenteConsultaRequest
  ): Observable<ApiResponse<AsistenteConsultaResponse>> {
    if (!this.isEnabled()) {
      return throwError(() => ({
        status: 501,
        message: 'No se puede consultar el asistente: el módulo está deshabilitado.',
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
