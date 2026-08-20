import { EstadoGeneral, RolNombre } from './enums.model';

/** AuthResponse del backend. Campo del token: `token` (no accessToken). */
export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresInMs: number;
  usuarioId: number;
  email: string;
  nombre: string;
  roles: RolNombre[] | string[];
}

/** LoginRequest del backend. */
export interface LoginRequest {
  email: string;
  password: string;
}

/** RegisterRequest del backend. */
export interface RegisterRequest {
  nombre: string;
  email: string;
  password: string;
}

/** Sesión local derivada del AuthResponse / UsuarioResponse. */
export interface SessionUser {
  id: number;
  email: string;
  nombre: string;
  roles: string[];
  estado?: EstadoGeneral;
}
