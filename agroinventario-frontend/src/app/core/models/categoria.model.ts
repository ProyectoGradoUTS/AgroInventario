/** CategoriaResponse del backend. */
export interface CategoriaResponse {
  id: number;
  nombre: string;
  descripcion: string | null;
}

/** CrearCategoriaRequest / ActualizarCategoriaRequest del backend. */
export interface CategoriaRequest {
  nombre: string;
  descripcion?: string | null;
}
