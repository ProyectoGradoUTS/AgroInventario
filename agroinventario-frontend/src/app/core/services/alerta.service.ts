import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ActualizarEstadoAlertaRequest,
  AlertaResponse,
  ApiResponse,
  EstadoAlerta,
} from '../models';

@Injectable({ providedIn: 'root' })
export class AlertaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/v1/alertas`;

  listar(estado?: EstadoAlerta): Observable<ApiResponse<AlertaResponse[]>> {
    let params = new HttpParams();
    if (estado) {
      params = params.set('estado', estado);
    }
    return this.http.get<ApiResponse<AlertaResponse[]>>(this.baseUrl, { params });
  }

  actualizarEstado(
    id: number,
    request: ActualizarEstadoAlertaRequest
  ): Observable<ApiResponse<AlertaResponse>> {
    return this.http.patch<ApiResponse<AlertaResponse>>(`${this.baseUrl}/${id}/estado`, request);
  }
}
