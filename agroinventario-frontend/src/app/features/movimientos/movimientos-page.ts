import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import {
  MovimientoInventarioResponse,
  ProductoResponse,
  TipoMovimiento,
} from '../../core/models';
import { InventarioService } from '../../core/services/inventario.service';
import { ProductoService } from '../../core/services/producto.service';
import { extractErrorMessage } from '../../core/utilities/error.util';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header';

@Component({
  selector: 'app-movimientos-page',
  standalone: true,
  imports: [
    DatePipe,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatFormFieldModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    PageHeaderComponent,
  ],
  templateUrl: './movimientos-page.html',
  styleUrl: './movimientos-page.scss',
})
export class MovimientosPage implements OnInit {
  private readonly inventarioService = inject(InventarioService);
  private readonly productoService = inject(ProductoService);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly movimientos = signal<MovimientoInventarioResponse[]>([]);
  readonly productos = signal<ProductoResponse[]>([]);

  readonly displayedColumns = [
    'fecha',
    'producto',
    'tipo',
    'cantidad',
    'usuario',
    'descripcion',
  ];

  readonly tipos: Array<TipoMovimiento | ''> = ['', 'ENTRADA', 'SALIDA'];

  readonly filters = this.fb.nonNullable.group({
    productoId: ['' as number | ''],
    tipoMovimiento: ['' as TipoMovimiento | ''],
  });

  ngOnInit(): void {
    this.cargarProductos();
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    const productoId = this.filters.controls.productoId.value;
    const tipo = this.filters.controls.tipoMovimiento.value;

    this.inventarioService
      .listarMovimientos(productoId === '' ? undefined : Number(productoId))
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          let data = response.data ?? [];
          if (tipo) {
            data = data.filter((m) => m.tipoMovimiento === tipo);
          }
          data = [...data].sort(
            (a, b) =>
              new Date(b.fechaMovimiento).getTime() - new Date(a.fechaMovimiento).getTime()
          );
          this.movimientos.set(data);
        },
        error: (error: unknown) => {
          this.errorMessage.set(
            extractErrorMessage(error, 'No se pudieron cargar los movimientos.')
          );
        },
      });
  }

  limpiar(): void {
    this.filters.reset({ productoId: '', tipoMovimiento: '' });
    this.cargar();
  }

  private cargarProductos(): void {
    this.productoService.listar().subscribe({
      next: (response) => this.productos.set(response.data ?? []),
      error: () => {
        // Filtro opcional
      },
    });
  }
}
