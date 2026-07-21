import { EstadoGeneral } from './enums.model';

/** UsuarioResponse del backend. */
export interface UsuarioResponse {
  id: number;
  nombre: string;
  email: string;
  estado: EstadoGeneral;
  roles: string[];
  fechaCreacion: string;
}

/** CrearUsuarioAdminRequest del backend. */
export interface CrearUsuarioAdminRequest {
  nombre: string;
  email: string;
  password: string;
  roles: string[];
}

/** CambiarEstadoUsuarioRequest del backend. */
export interface CambiarEstadoUsuarioRequest {
  estado: EstadoGeneral;
}

/** AsignarRolesUsuarioRequest del backend. */
export interface AsignarRolesUsuarioRequest {
  roles: string[];
}
