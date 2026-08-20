import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ActualizarProductoRequest,
  ApiResponse,
  CambiarEstadoProductoRequest,
  CrearProductoRequest,
  EstadoGeneral,
  PageResponse,
  ProductoPaginadoQuery,
  ProductoResponse,
} from '../models';

@Injectable({ providedIn: 'root' })
export class ProductoService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/v1/productos`;

  listar(estado?: EstadoGeneral): Observable<ApiResponse<ProductoResponse[]>> {
    let params = new HttpParams();
    if (estado) {
      params = params.set('estado', estado);
    }
    return this.http.get<ApiResponse<ProductoResponse[]>>(this.baseUrl, { params });
  }

  listarPaginado(
    query: ProductoPaginadoQuery = {}
  ): Observable<ApiResponse<PageResponse<ProductoResponse>>> {
    let params = new HttpParams()
      .set('page', String(query.page ?? 0))
      .set('size', String(query.size ?? 20));

    if (query.estado) {
      params = params.set('estado', query.estado);
    }
    if (query.categoriaId != null) {
      params = params.set('categoriaId', String(query.categoriaId));
    }
    if (query.nombre) {
      params = params.set('nombre', query.nombre);
    }

    return this.http.get<ApiResponse<PageResponse<ProductoResponse>>>(`${this.baseUrl}/paginado`, {
      params,
    });
  }

  obtener(id: number): Observable<ApiResponse<ProductoResponse>> {
    return this.http.get<ApiResponse<ProductoResponse>>(`${this.baseUrl}/${id}`);
  }

  crear(request: CrearProductoRequest): Observable<ApiResponse<ProductoResponse>> {
    return this.http.post<ApiResponse<ProductoResponse>>(this.baseUrl, request);
  }

  actualizar(
    id: number,
    request: ActualizarProductoRequest
  ): Observable<ApiResponse<ProductoResponse>> {
    return this.http.put<ApiResponse<ProductoResponse>>(`${this.baseUrl}/${id}`, request);
  }

  cambiarEstado(
    id: number,
    request: CambiarEstadoProductoRequest
  ): Observable<ApiResponse<ProductoResponse>> {
    return this.http.patch<ApiResponse<ProductoResponse>>(`${this.baseUrl}/${id}/estado`, request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
