import { Component, OnInit, inject, signal } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { finalize } from 'rxjs';
import {
  ActualizarProductoRequest,
  CategoriaResponse,
  CrearProductoRequest,
  EstadoGeneral,
  ProductoResponse,
} from '../../../core/models';
import { CategoriaService } from '../../../core/services/categoria.service';
import { ProductoService } from '../../../core/services/producto.service';
import { extractErrorMessage } from '../../../core/utilities/error.util';

export interface ProductoFormDialogData {
  mode: 'create' | 'edit';
  producto?: ProductoResponse;
}

@Component({
  selector: 'app-producto-form-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './producto-form-dialog.html',
  styleUrl: './producto-form-dialog.scss',
})
export class ProductoFormDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly productoService = inject(ProductoService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly dialogRef = inject(MatDialogRef<ProductoFormDialogComponent, ProductoResponse>);
  readonly data = inject<ProductoFormDialogData>(MAT_DIALOG_DATA);

  readonly loading = signal(false);
  readonly loadingCategorias = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly categorias = signal<CategoriaResponse[]>([]);
  readonly estados: EstadoGeneral[] = ['ACTIVO', 'INACTIVO'];

  readonly isEdit = this.data.mode === 'edit';

  readonly form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(200)]],
    descripcion: ['', [Validators.maxLength(1000)]],
    precio: [0, [Validators.required, Validators.min(0)]],
    stockActual: [0, [Validators.required, Validators.min(0)]],
    stockMinimo: [0, [Validators.required, Validators.min(0)]],
    fechaVencimiento: [''],
    categoriaId: [null as number | null, [Validators.required]],
    estado: ['ACTIVO' as EstadoGeneral, [Validators.required]],
  });

  ngOnInit(): void {
    this.cargarCategorias();

    if (this.isEdit && this.data.producto) {
      const p = this.data.producto;
      this.form.patchValue({
        nombre: p.nombre,
        descripcion: p.descripcion ?? '',
        precio: Number(p.precio),
        stockActual: p.stockActual,
        stockMinimo: p.stockMinimo,
        fechaVencimiento: p.fechaVencimiento ?? '',
        categoriaId: p.categoriaId,
        estado: p.estado,
      });
      // ActualizarProductoRequest no permite cambiar stockActual ni categoriaId
      this.form.controls.stockActual.disable({ emitEvent: false });
      this.form.controls.categoriaId.disable({ emitEvent: false });
    }
  }

  submit(): void {
    this.errorMessage.set(null);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.form.disable({ emitEvent: false });

    const raw = this.form.getRawValue();
    const fecha = raw.fechaVencimiento?.trim() ? raw.fechaVencimiento : null;
    const descripcion = raw.descripcion?.trim() ? raw.descripcion.trim() : null;

    const request$ = this.isEdit
      ? this.productoService.actualizar(this.data.producto!.id, {
          nombre: raw.nombre.trim(),
          descripcion,
          precio: Number(raw.precio),
          stockMinimo: Number(raw.stockMinimo),
          fechaVencimiento: fecha,
          estado: raw.estado,
        } satisfies ActualizarProductoRequest)
      : this.productoService.crear({
          nombre: raw.nombre.trim(),
          descripcion,
          precio: Number(raw.precio),
          stockActual: Number(raw.stockActual),
          stockMinimo: Number(raw.stockMinimo),
          fechaVencimiento: fecha,
          categoriaId: Number(raw.categoriaId),
          estado: raw.estado,
        } satisfies CrearProductoRequest);

    request$
      .pipe(
        finalize(() => {
          this.loading.set(false);
          this.form.enable({ emitEvent: false });
          if (this.isEdit) {
            this.form.controls.stockActual.disable({ emitEvent: false });
            this.form.controls.categoriaId.disable({ emitEvent: false });
          }
        })
      )
      .subscribe({
        next: (response) => this.dialogRef.close(response.data),
        error: (error: unknown) => {
          this.errorMessage.set(extractErrorMessage(error, 'No se pudo guardar el producto.'));
        },
      });
  }

  private cargarCategorias(): void {
    this.loadingCategorias.set(true);
    this.categoriaService
      .listar()
      .pipe(finalize(() => this.loadingCategorias.set(false)))
      .subscribe({
        next: (response) => this.categorias.set(response.data ?? []),
        error: (error: unknown) => {
          this.errorMessage.set(
            extractErrorMessage(error, 'No se pudieron cargar las categorías.')
          );
        },
      });
  }
}
