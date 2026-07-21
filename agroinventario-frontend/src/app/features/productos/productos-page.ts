import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/authentication/auth.service';
import {
  CategoriaResponse,
  EstadoGeneral,
  PageResponse,
  ProductoResponse,
} from '../../core/models';
import { CategoriaService } from '../../core/services/categoria.service';
import { ProductoService } from '../../core/services/producto.service';
import { extractErrorMessage } from '../../core/utilities/error.util';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header';
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from '../../shared/dialogs/confirm-dialog';
import {
  ProductoFormDialogComponent,
  ProductoFormDialogData,
} from './producto-form-dialog/producto-form-dialog';

@Component({
  selector: 'app-productos-page',
  standalone: true,
  imports: [
    CurrencyPipe,
    DatePipe,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatMenuModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTooltipModule,
    PageHeaderComponent,
  ],
  templateUrl: './productos-page.html',
  styleUrl: './productos-page.scss',
})
export class ProductosPage implements OnInit {
  private readonly productoService = inject(ProductoService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly auth = inject(AuthService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly fb = inject(FormBuilder);

  readonly isAdmin = this.auth.isAdmin;
  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly pageData = signal<PageResponse<ProductoResponse> | null>(null);
  readonly categorias = signal<CategoriaResponse[]>([]);

  readonly displayedColumns = [
    'nombre',
    'categoria',
    'precio',
    'stock',
    'vencimiento',
    'estado',
    'acciones',
  ];

  readonly estados: Array<EstadoGeneral | ''> = ['', 'ACTIVO', 'INACTIVO'];

  readonly filters = this.fb.nonNullable.group({
    nombre: [''],
    estado: ['' as EstadoGeneral | ''],
    categoriaId: ['' as number | ''],
  });

  pageIndex = 0;
  pageSize = 10;

  ngOnInit(): void {
    this.cargarCategorias();
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    const { nombre, estado, categoriaId } = this.filters.getRawValue();

    this.productoService
      .listarPaginado({
        page: this.pageIndex,
        size: this.pageSize,
        nombre: nombre.trim() || undefined,
        estado: estado || undefined,
        categoriaId: categoriaId === '' ? undefined : Number(categoriaId),
      })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => this.pageData.set(response.data),
        error: (error: unknown) => {
          this.errorMessage.set(
            extractErrorMessage(error, 'No se pudieron cargar los productos.')
          );
        },
      });
  }

  buscar(): void {
    this.pageIndex = 0;
    this.cargar();
  }

  limpiarFiltros(): void {
    this.filters.reset({ nombre: '', estado: '', categoriaId: '' });
    this.pageIndex = 0;
    this.cargar();
  }

  onPage(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.cargar();
  }

  abrirCrear(): void {
    if (!this.isAdmin()) {
      return;
    }

    this.dialog
      .open<ProductoFormDialogComponent, ProductoFormDialogData, ProductoResponse>(
        ProductoFormDialogComponent,
        {
          width: '640px',
          disableClose: true,
          data: { mode: 'create' },
        }
      )
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.snackBar.open('Producto creado', 'Cerrar', { duration: 3000 });
          this.cargar();
        }
      });
  }

  abrirEditar(producto: ProductoResponse): void {
    if (!this.isAdmin()) {
      return;
    }

    this.dialog
      .open<ProductoFormDialogComponent, ProductoFormDialogData, ProductoResponse>(
        ProductoFormDialogComponent,
        {
          width: '640px',
          disableClose: true,
          data: { mode: 'edit', producto },
        }
      )
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.snackBar.open('Producto actualizado', 'Cerrar', { duration: 3000 });
          this.cargar();
        }
      });
  }

  cambiarEstado(producto: ProductoResponse): void {
    if (!this.isAdmin()) {
      return;
    }

    const nuevoEstado: EstadoGeneral = producto.estado === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';

    this.dialog
      .open<ConfirmDialogComponent, ConfirmDialogData, boolean>(ConfirmDialogComponent, {
        data: {
          title: 'Cambiar estado',
          message: `¿Desea marcar el producto "${producto.nombre}" como ${nuevoEstado}?`,
          confirmLabel: `Marcar ${nuevoEstado}`,
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) {
          return;
        }

        this.productoService.cambiarEstado(producto.id, { estado: nuevoEstado }).subscribe({
          next: () => {
            this.snackBar.open('Estado actualizado', 'Cerrar', { duration: 3000 });
            this.cargar();
          },
          error: (error: unknown) => {
            this.snackBar.open(
              extractErrorMessage(error, 'No se pudo cambiar el estado.'),
              'Cerrar',
              { duration: 4500 }
            );
          },
        });
      });
  }

  eliminar(producto: ProductoResponse): void {
    if (!this.isAdmin()) {
      return;
    }

    this.dialog
      .open<ConfirmDialogComponent, ConfirmDialogData, boolean>(ConfirmDialogComponent, {
        data: {
          title: 'Eliminar producto',
          message: `¿Eliminar "${producto.nombre}"? No es posible si tiene movimientos de inventario.`,
          confirmLabel: 'Eliminar',
          danger: true,
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) {
          return;
        }

        this.productoService.eliminar(producto.id).subscribe({
          next: () => {
            this.snackBar.open('Producto eliminado', 'Cerrar', { duration: 3000 });
            if ((this.pageData()?.content.length ?? 0) <= 1 && this.pageIndex > 0) {
              this.pageIndex -= 1;
            }
            this.cargar();
          },
          error: (error: unknown) => {
            this.snackBar.open(
              extractErrorMessage(error, 'No se pudo eliminar el producto.'),
              'Cerrar',
              { duration: 5000 }
            );
          },
        });
      });
  }

  private cargarCategorias(): void {
    this.categoriaService.listar().subscribe({
      next: (response) => this.categorias.set(response.data ?? []),
      error: () => {
        // Filtro de categoría opcional; no bloquea el listado
      },
    });
  }
}
