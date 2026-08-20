import { Injectable, inject } from '@angular/core';
import { Observable, catchError, forkJoin, map, of } from 'rxjs';
import {
  AlertaResponse,
  CategoriaResponse,
  MovimientoInventarioResponse,
  ProductoResponse,
} from '../models';
import { AlertaService } from './alerta.service';
import { CategoriaService } from './categoria.service';
import { InventarioService } from './inventario.service';
import { ProductoService } from './producto.service';

export interface DashboardKpis {
  totalProductos: number;
  productosActivos: number;
  productosStockBajo: number;
  totalCategorias: number;
  alertasPendientes: number;
  alertasLeidas: number;
  totalMovimientos: number;
  entradas: number;
  salidas: number;
}

export interface DashboardSnapshot {
  kpis: DashboardKpis;
  alertasRecientes: AlertaResponse[];
  movimientosRecientes: MovimientoInventarioResponse[];
  productosStockBajo: ProductoResponse[];
  loadErrors: string[];
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly productoService = inject(ProductoService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly alertaService = inject(AlertaService);
  private readonly inventarioService = inject(InventarioService);

  /**
   * Agrega indicadores desde endpoints existentes.
   * No hay endpoint /dashboard en el backend.
   */
  cargarResumen(): Observable<DashboardSnapshot> {
    const loadErrors: string[] = [];

    return forkJoin({
      productos: this.productoService.listar().pipe(
        map((r) => r.data ?? []),
        catchError(() => {
          loadErrors.push('No se pudieron cargar productos.');
          return of([] as ProductoResponse[]);
        })
      ),
      categorias: this.categoriaService.listar().pipe(
        map((r) => r.data ?? []),
        catchError(() => {
          loadErrors.push('No se pudieron cargar categorías.');
          return of([] as CategoriaResponse[]);
        })
      ),
      alertas: this.alertaService.listar().pipe(
        map((r) => r.data ?? []),
        catchError(() => {
          loadErrors.push('No se pudieron cargar alertas.');
          return of([] as AlertaResponse[]);
        })
      ),
      movimientos: this.inventarioService.listarMovimientos().pipe(
        map((r) => r.data ?? []),
        catchError(() => {
          loadErrors.push('No se pudieron cargar movimientos.');
          return of([] as MovimientoInventarioResponse[]);
        })
      ),
    }).pipe(
      map(({ productos, categorias, alertas, movimientos }) => {
        const productosStockBajo = productos
          .filter((p) => p.stockBajo && p.estado === 'ACTIVO')
          .slice(0, 8);

        const alertasOrdenadas = [...alertas].sort(
          (a, b) =>
            new Date(b.fechaGeneracion).getTime() - new Date(a.fechaGeneracion).getTime()
        );

        const movimientosOrdenados = [...movimientos].sort(
          (a, b) =>
            new Date(b.fechaMovimiento).getTime() - new Date(a.fechaMovimiento).getTime()
        );

        const kpis: DashboardKpis = {
          totalProductos: productos.length,
          productosActivos: productos.filter((p) => p.estado === 'ACTIVO').length,
          productosStockBajo: productos.filter((p) => p.stockBajo && p.estado === 'ACTIVO').length,
          totalCategorias: categorias.length,
          alertasPendientes: alertas.filter((a) => a.estado === 'PENDIENTE').length,
          alertasLeidas: alertas.filter((a) => a.estado === 'LEIDA').length,
          totalMovimientos: movimientos.length,
          entradas: movimientos.filter((m) => m.tipoMovimiento === 'ENTRADA').length,
          salidas: movimientos.filter((m) => m.tipoMovimiento === 'SALIDA').length,
        };

        return {
          kpis,
          alertasRecientes: alertasOrdenadas.slice(0, 6),
          movimientosRecientes: movimientosOrdenados.slice(0, 8),
          productosStockBajo,
          loadErrors: [...loadErrors],
        };
      })
    );
  }
}
