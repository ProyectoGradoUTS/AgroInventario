import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  AsignarRolesUsuarioRequest,
  CambiarEstadoUsuarioRequest,
  CrearUsuarioAdminRequest,
  UsuarioResponse,
} from '../models';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/v1/usuarios`;

  listar(): Observable<ApiResponse<UsuarioResponse[]>> {
    return this.http.get<ApiResponse<UsuarioResponse[]>>(this.baseUrl);
  }

  obtener(id: number): Observable<ApiResponse<UsuarioResponse>> {
    return this.http.get<ApiResponse<UsuarioResponse>>(`${this.baseUrl}/${id}`);
  }

  crear(request: CrearUsuarioAdminRequest): Observable<ApiResponse<UsuarioResponse>> {
    return this.http.post<ApiResponse<UsuarioResponse>>(this.baseUrl, request);
  }

  cambiarEstado(
    id: number,
    request: CambiarEstadoUsuarioRequest
  ): Observable<ApiResponse<UsuarioResponse>> {
    return this.http.patch<ApiResponse<UsuarioResponse>>(`${this.baseUrl}/${id}/estado`, request);
  }

  asignarRoles(
    id: number,
    request: AsignarRolesUsuarioRequest
  ): Observable<ApiResponse<UsuarioResponse>> {
    return this.http.put<ApiResponse<UsuarioResponse>>(`${this.baseUrl}/${id}/roles`, request);
  }
}
