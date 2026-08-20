import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, AuditoriaQuery, AuditoriaResponse, PageResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class AuditoriaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/v1/auditoria`;

  listar(
    query: AuditoriaQuery = {}
  ): Observable<ApiResponse<PageResponse<AuditoriaResponse>>> {
    let params = new HttpParams()
      .set('page', String(query.page ?? 0))
      .set('size', String(query.size ?? 20));

    if (query.entidad) {
      params = params.set('entidad', query.entidad);
    }

    return this.http.get<ApiResponse<PageResponse<AuditoriaResponse>>>(this.baseUrl, { params });
  }
}
