import { EstadoGeneral } from './enums.model';

/** ProductoResponse del backend. */
export interface ProductoResponse {
  id: number;
  nombre: string;
  descripcion: string | null;
  precio: number;
  stockActual: number;
  stockMinimo: number;
  stockBajo: boolean;
  fechaVencimiento: string | null;
  categoriaId: number;
  categoriaNombre: string;
  estado: EstadoGeneral;
  fechaCreacion: string;
}

/** CrearProductoRequest del backend. */
export interface CrearProductoRequest {
  nombre: string;
  descripcion?: string | null;
  precio: number;
  stockActual: number;
  stockMinimo: number;
  fechaVencimiento?: string | null;
  categoriaId: number;
  estado?: EstadoGeneral | null;
}

/**
 * ActualizarProductoRequest del backend.
 * No incluye stockActual ni categoriaId.
 */
export interface ActualizarProductoRequest {
  nombre: string;
  descripcion?: string | null;
  precio: number;
  stockMinimo: number;
  fechaVencimiento?: string | null;
  estado: EstadoGeneral;
}

/** CambiarEstadoProductoRequest del backend. */
export interface CambiarEstadoProductoRequest {
  estado: EstadoGeneral;
}

/** Query params de GET /v1/productos/paginado. */
export interface ProductoPaginadoQuery {
  estado?: EstadoGeneral;
  categoriaId?: number;
  nombre?: string;
  page?: number;
  size?: number;
}
