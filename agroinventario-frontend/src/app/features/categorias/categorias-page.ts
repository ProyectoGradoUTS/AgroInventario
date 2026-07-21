import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/authentication/auth.service';
import { CategoriaResponse } from '../../core/models';
import { CategoriaService } from '../../core/services/categoria.service';
import { extractErrorMessage } from '../../core/utilities/error.util';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header';
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from '../../shared/dialogs/confirm-dialog';
import {
  CategoriaFormDialogComponent,
  CategoriaFormDialogData,
} from './categoria-form-dialog/categoria-form-dialog';

@Component({
  selector: 'app-categorias-page',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatMenuModule,
    MatDialogModule,
    MatSnackBarModule,
    PageHeaderComponent,
  ],
  templateUrl: './categorias-page.html',
  styleUrl: './categorias-page.scss',
})
export class CategoriasPage implements OnInit {
  private readonly categoriaService = inject(CategoriaService);
  private readonly auth = inject(AuthService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly fb = inject(FormBuilder);

  readonly isAdmin = this.auth.isAdmin;
  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly categorias = signal<CategoriaResponse[]>([]);
  readonly filtered = signal<CategoriaResponse[]>([]);

  readonly displayedColumns = ['nombre', 'descripcion', 'acciones'];

  readonly search = this.fb.nonNullable.control('');

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.categoriaService
      .listar()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          const data = response.data ?? [];
          this.categorias.set(data);
          this.applyFilter();
        },
        error: (error: unknown) => {
          this.errorMessage.set(
            extractErrorMessage(error, 'No se pudieron cargar las categorías.')
          );
        },
      });
  }

  applyFilter(): void {
    const term = this.search.value.trim().toLowerCase();
    if (!term) {
      this.filtered.set(this.categorias());
      return;
    }

    this.filtered.set(
      this.categorias().filter(
        (c) =>
          c.nombre.toLowerCase().includes(term) ||
          (c.descripcion ?? '').toLowerCase().includes(term)
      )
    );
  }

  abrirCrear(): void {
    if (!this.isAdmin()) {
      return;
    }

    this.dialog
      .open<CategoriaFormDialogComponent, CategoriaFormDialogData, CategoriaResponse>(
        CategoriaFormDialogComponent,
        {
          width: '480px',
          disableClose: true,
          data: { mode: 'create' },
        }
      )
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.snackBar.open('Categoría creada', 'Cerrar', { duration: 3000 });
          this.cargar();
        }
      });
  }

  abrirEditar(categoria: CategoriaResponse): void {
    if (!this.isAdmin()) {
      return;
    }

    this.dialog
      .open<CategoriaFormDialogComponent, CategoriaFormDialogData, CategoriaResponse>(
        CategoriaFormDialogComponent,
        {
          width: '480px',
          disableClose: true,
          data: { mode: 'edit', categoria },
        }
      )
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.snackBar.open('Categoría actualizada', 'Cerrar', { duration: 3000 });
          this.cargar();
        }
      });
  }

  eliminar(categoria: CategoriaResponse): void {
    if (!this.isAdmin()) {
      return;
    }

    this.dialog
      .open<ConfirmDialogComponent, ConfirmDialogData, boolean>(ConfirmDialogComponent, {
        data: {
          title: 'Eliminar categoría',
          message: `¿Eliminar "${categoria.nombre}"? No es posible si tiene productos asociados.`,
          confirmLabel: 'Eliminar',
          danger: true,
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) {
          return;
        }

        this.categoriaService.eliminar(categoria.id).subscribe({
          next: () => {
            this.snackBar.open('Categoría eliminada', 'Cerrar', { duration: 3000 });
            this.cargar();
          },
          error: (error: unknown) => {
            this.snackBar.open(
              extractErrorMessage(error, 'No se pudo eliminar la categoría.'),
              'Cerrar',
              { duration: 5000 }
            );
          },
        });
      });
  }
}
