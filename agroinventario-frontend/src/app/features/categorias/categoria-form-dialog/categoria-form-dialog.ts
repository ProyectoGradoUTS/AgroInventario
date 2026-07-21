import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { finalize } from 'rxjs';
import { CategoriaRequest, CategoriaResponse } from '../../../core/models';
import { CategoriaService } from '../../../core/services/categoria.service';
import { extractErrorMessage } from '../../../core/utilities/error.util';

export interface CategoriaFormDialogData {
  mode: 'create' | 'edit';
  categoria?: CategoriaResponse;
}

@Component({
  selector: 'app-categoria-form-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './categoria-form-dialog.html',
  styleUrl: './categoria-form-dialog.scss',
})
export class CategoriaFormDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly categoriaService = inject(CategoriaService);
  private readonly dialogRef = inject(MatDialogRef<CategoriaFormDialogComponent, CategoriaResponse>);
  readonly data = inject<CategoriaFormDialogData>(MAT_DIALOG_DATA);

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly isEdit = this.data.mode === 'edit';

  readonly form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    descripcion: ['', [Validators.maxLength(500)]],
  });

  ngOnInit(): void {
    if (this.isEdit && this.data.categoria) {
      this.form.patchValue({
        nombre: this.data.categoria.nombre,
        descripcion: this.data.categoria.descripcion ?? '',
      });
    }
  }

  submit(): void {
    this.errorMessage.set(null);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const request: CategoriaRequest = {
      nombre: raw.nombre.trim(),
      descripcion: raw.descripcion.trim() ? raw.descripcion.trim() : null,
    };

    this.loading.set(true);
    this.form.disable({ emitEvent: false });

    const request$ = this.isEdit
      ? this.categoriaService.actualizar(this.data.categoria!.id, request)
      : this.categoriaService.crear(request);

    request$
      .pipe(
        finalize(() => {
          this.loading.set(false);
          this.form.enable({ emitEvent: false });
        })
      )
      .subscribe({
        next: (response) => this.dialogRef.close(response.data),
        error: (error: unknown) => {
          this.errorMessage.set(extractErrorMessage(error, 'No se pudo guardar la categoría.'));
        },
      });
  }
}
