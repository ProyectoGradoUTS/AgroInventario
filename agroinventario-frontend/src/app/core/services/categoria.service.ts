import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, CategoriaRequest, CategoriaResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class CategoriaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/v1/categorias`;

  listar(): Observable<ApiResponse<CategoriaResponse[]>> {
    return this.http.get<ApiResponse<CategoriaResponse[]>>(this.baseUrl);
  }

  obtener(id: number): Observable<ApiResponse<CategoriaResponse>> {
    return this.http.get<ApiResponse<CategoriaResponse>>(`${this.baseUrl}/${id}`);
  }

  crear(request: CategoriaRequest): Observable<ApiResponse<CategoriaResponse>> {
    return this.http.post<ApiResponse<CategoriaResponse>>(this.baseUrl, request);
  }

  actualizar(id: number, request: CategoriaRequest): Observable<ApiResponse<CategoriaResponse>> {
    return this.http.put<ApiResponse<CategoriaResponse>>(`${this.baseUrl}/${id}`, request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
