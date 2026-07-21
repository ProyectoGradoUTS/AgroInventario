import { TipoMovimiento } from './enums.model';

/** MovimientoInventarioResponse del backend. */
export interface MovimientoInventarioResponse {
  id: number;
  productoId: number;
  productoNombre: string;
  tipoMovimiento: TipoMovimiento;
  cantidad: number;
  descripcion: string | null;
  usuarioId: number;
  usuarioNombre: string;
  fechaMovimiento: string;
}

/**
 * RegistrarMovimientoRequest del backend.
 * No envía usuarioId; el backend lo toma del JWT.
 */
export interface RegistrarMovimientoRequest {
  tipoMovimiento: TipoMovimiento;
  cantidad: number;
  descripcion?: string | null;
}
