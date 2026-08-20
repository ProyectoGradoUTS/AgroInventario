import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, MovimientoInventarioResponse, RegistrarMovimientoRequest } from '../models';

@Injectable({ providedIn: 'root' })
export class InventarioService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/v1/inventario`;

  registrarMovimiento(
    productoId: number,
    request: RegistrarMovimientoRequest
  ): Observable<ApiResponse<MovimientoInventarioResponse>> {
    return this.http.post<ApiResponse<MovimientoInventarioResponse>>(
      `${this.baseUrl}/productos/${productoId}/movimientos`,
      request
    );
  }

  listarMovimientos(
    productoId?: number
  ): Observable<ApiResponse<MovimientoInventarioResponse[]>> {
    let params = new HttpParams();
    if (productoId != null) {
      params = params.set('productoId', String(productoId));
    }
    return this.http.get<ApiResponse<MovimientoInventarioResponse[]>>(
      `${this.baseUrl}/movimientos`,
      { params }
    );
  }
}
