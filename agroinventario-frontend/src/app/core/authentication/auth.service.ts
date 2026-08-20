import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  SessionUser,
  UsuarioResponse,
} from '../models';
import { TokenStorageService } from './token-storage.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly router = inject(Router);
  private readonly baseUrl = `${environment.apiUrl}/v1/auth`;

  readonly currentUser = this.tokenStorage.user;
  readonly isAuthenticated = computed(() => !!this.tokenStorage.getToken());
  readonly isAdmin = computed(() => this.hasRole('ADMIN'));

  login(request: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseUrl}/login`, request).pipe(
      tap((response) => this.persistAuth(response.data))
    );
  }

  register(request: RegisterRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseUrl}/register`, request).pipe(
      tap((response) => this.persistAuth(response.data))
    );
  }

  me(): Observable<ApiResponse<UsuarioResponse>> {
    return this.http.get<ApiResponse<UsuarioResponse>>(`${this.baseUrl}/me`).pipe(
      tap((response) => {
        const data = response.data;
        this.tokenStorage.updateUser({
          id: data.id,
          email: data.email,
          nombre: data.nombre,
          roles: data.roles,
          estado: data.estado,
        });
      })
    );
  }

  logout(): void {
    this.tokenStorage.clear();
    void this.router.navigate(['/auth/login']);
  }

  hasRole(role: string): boolean {
    const roles = this.tokenStorage.user()?.roles ?? [];
    return roles.includes(role);
  }

  hasAnyRole(roles: string[]): boolean {
    return roles.some((role) => this.hasRole(role));
  }

  private persistAuth(data: AuthResponse): void {
    const user: SessionUser = {
      id: data.usuarioId,
      email: data.email,
      nombre: data.nombre,
      roles: data.roles,
    };
    this.tokenStorage.setSession(data.token, user);
  }
}
