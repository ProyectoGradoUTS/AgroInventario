import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import {
  ProductoResponse,
  RegistrarMovimientoRequest,
  TipoMovimiento,
} from '../../core/models';
import { InventarioService } from '../../core/services/inventario.service';
import { ProductoService } from '../../core/services/producto.service';
import { extractErrorMessage } from '../../core/utilities/error.util';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header';

@Component({
  selector: 'app-inventario-page',
  standalone: true,
  imports: [
    CurrencyPipe,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    PageHeaderComponent,
  ],
  templateUrl: './inventario-page.html',
  styleUrl: './inventario-page.scss',
})
export class InventarioPage implements OnInit {
  private readonly productoService = inject(ProductoService);
  private readonly inventarioService = inject(InventarioService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly fb = inject(FormBuilder);

  readonly loadingProductos = signal(false);
  readonly submitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly productos = signal<ProductoResponse[]>([]);

  readonly tipos: TipoMovimiento[] = ['ENTRADA', 'SALIDA'];

  readonly form = this.fb.nonNullable.group({
    productoId: [null as number | null, [Validators.required]],
    tipoMovimiento: ['ENTRADA' as TipoMovimiento, [Validators.required]],
    cantidad: [1, [Validators.required, Validators.min(1)]],
    descripcion: ['', [Validators.maxLength(500)]],
  });

  readonly productoSeleccionado = computed(() => {
    const id = this.form.controls.productoId.value;
    if (id == null) {
      return null;
    }
    return this.productos().find((p) => p.id === id) ?? null;
  });

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos(): void {
    this.loadingProductos.set(true);
    this.errorMessage.set(null);

    this.productoService
      .listar('ACTIVO')
      .pipe(finalize(() => this.loadingProductos.set(false)))
      .subscribe({
        next: (response) => this.productos.set(response.data ?? []),
        error: (error: unknown) => {
          this.errorMessage.set(
            extractErrorMessage(error, 'No se pudieron cargar los productos.')
          );
        },
      });
  }

  submit(): void {
    this.errorMessage.set(null);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const productoId = Number(raw.productoId);
    const request: RegistrarMovimientoRequest = {
      tipoMovimiento: raw.tipoMovimiento,
      cantidad: Number(raw.cantidad),
      descripcion: raw.descripcion.trim() ? raw.descripcion.trim() : null,
    };

    this.submitting.set(true);
    this.form.disable({ emitEvent: false });

    this.inventarioService
      .registrarMovimiento(productoId, request)
      .pipe(
        finalize(() => {
          this.submitting.set(false);
          this.form.enable({ emitEvent: false });
        })
      )
      .subscribe({
        next: (response) => {
          const mov = response.data;
          this.snackBar.open(
            `${mov.tipoMovimiento} de ${mov.cantidad} registrada en ${mov.productoNombre}`,
            'Cerrar',
            { duration: 4000 }
          );
          this.form.patchValue({ cantidad: 1, descripcion: '' });
          this.cargarProductos();
        },
        error: (error: unknown) => {
          this.errorMessage.set(
            extractErrorMessage(error, 'No se pudo registrar el movimiento.')
          );
        },
      });
  }
}
