import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, forkJoin, map, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AlertaResponse,
  ApiResponse,
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

export interface DashboardProductoResumen {
  id: number | null;
  nombre: string;
  categoria: string;
  stockActual: number;
  stockMinimo: number;
  consumoPromedio: number;
  diasHastaAgotamiento: number;
  diasHastaVencimiento: number | null;
  ultimoMovimiento: string | null;
  estado: string;
  riesgo: string;
  recomendacion: string;
  prioridad: string;
}

export interface DashboardResumenData {
  totalProductosEnRiesgo: number;
  totalAlertasPendientes: number;
  totalProductosCriticos: number;
  totalSinMovimiento: number;
  productos: DashboardProductoResumen[];
}

export interface DashboardCategoriaResumen {
  categoria: string;
  totalProductos: number;
  productosEnRiesgo: number;
  productosCriticos: number;
  stockBajo: number;
  consumoPromedio: number;
  riesgoPromedio: number;
  nivelRiesgo: string;
}

export interface DashboardEjecutivoData {
  totalProductos: number;
  totalCategorias: number;
  alertasPendientes: number;
  productosCriticos: number;
  productosEnRiesgo: number;
  categoriaMasRiesgosa: string;
  categorias: DashboardCategoriaResumen[];
}

export interface DashboardRecomendacionData {
  id: number | null;
  nombre: string;
  categoria: string;
  stockActual: number;
  stockMinimo: number;
  consumoPromedio: number;
  leadTimeDias: number;
  diasHastaAgotamiento: number;
  diasHastaVencimiento: number;
  cantidadSugerida: number;
  estrategia: string;
  riesgo: string;
  mensaje: string;
}

export interface DashboardPrediccionData {
  id: number | null;
  nombre: string;
  categoria: string;
  stockActual: number;
  stockMinimo: number;
  consumoPromedio: number;
  leadTimeDias: number;
  diasHastaAgotamiento: number;
  fechaProyectadaAgotamiento: string | null;
  demandaProyectada7d: number;
  demandaProyectada30d: number;
  cantidadSugerida: number;
  estrategia: string;
  riesgo: string;
  mensaje: string;
}

export interface DashboardSnapshot {
  kpis: DashboardKpis;
  alertasRecientes: AlertaResponse[];
  movimientosRecientes: MovimientoInventarioResponse[];
  productosStockBajo: ProductoResponse[];
  resumen: DashboardResumenData | null;
  ejecutivo: DashboardEjecutivoData | null;
  recomendaciones: DashboardRecomendacionData[];
  predicciones: DashboardPrediccionData[];
  loadErrors: string[];
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly productoService = inject(ProductoService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly alertaService = inject(AlertaService);
  private readonly inventarioService = inject(InventarioService);
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/v1/dashboard`;

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
      resumen: this.http
        .get<ApiResponse<DashboardResumenData>>(`${this.baseUrl}/resumen`)
        .pipe(
          map((response) => response.data ?? null),
          catchError(() => {
            loadErrors.push('No se pudo cargar el resumen predictivo.');
            return of(null);
          })
        ),
      ejecutivo: this.http
        .get<ApiResponse<DashboardEjecutivoData>>(`${this.baseUrl}/ejecutivo`)
        .pipe(
          map((response) => response.data ?? null),
          catchError(() => {
            loadErrors.push('No se pudo cargar el ejecutivo por categoría.');
            return of(null);
          })
        ),
      recomendaciones: this.http
        .get<ApiResponse<DashboardRecomendacionData[]>>(`${this.baseUrl}/recomendaciones`)
        .pipe(
          map((response) => response.data ?? []),
          catchError(() => {
            loadErrors.push('No se pudieron cargar las recomendaciones.');
            return of([] as DashboardRecomendacionData[]);
          })
        ),
      predicciones: this.http
        .get<ApiResponse<DashboardPrediccionData[]>>(`${this.baseUrl}/predicciones`)
        .pipe(
          map((response) => response.data ?? []),
          catchError(() => {
            loadErrors.push('No se pudieron cargar las predicciones.');
            return of([] as DashboardPrediccionData[]);
          })
        ),
    }).pipe(
      map(({ productos, categorias, alertas, movimientos, resumen, ejecutivo, recomendaciones, predicciones }) => {
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
          resumen,
          ejecutivo,
          recomendaciones,
          predicciones,
          loadErrors: [...loadErrors],
        };
      })
    );
  }
}
