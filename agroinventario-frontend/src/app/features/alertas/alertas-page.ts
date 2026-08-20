import { DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { finalize } from 'rxjs';
import {
  AlertaResponse,
  EstadoAlerta,
  TipoAlerta,
} from '../../core/models';
import { AlertaService } from '../../core/services/alerta.service';
import { extractErrorMessage } from '../../core/utilities/error.util';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header';
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from '../../shared/dialogs/confirm-dialog';

@Component({
  selector: 'app-alertas-page',
  standalone: true,
  imports: [
    DatePipe,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatFormFieldModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatMenuModule,
    MatDialogModule,
    MatSnackBarModule,
    PageHeaderComponent,
  ],
  templateUrl: './alertas-page.html',
  styleUrl: './alertas-page.scss',
})
export class AlertasPage implements OnInit {
  private readonly alertaService = inject(AlertaService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly alertas = signal<AlertaResponse[]>([]);

  readonly displayedColumns = ['producto', 'tipo', 'mensaje', 'estado', 'fecha', 'acciones'];

  readonly filtros = this.fb.nonNullable.group({
    estado: ['' as EstadoAlerta | ''],
    tipoAlerta: ['' as TipoAlerta | ''],
  });

  readonly pendientes = computed(
    () => this.alertas().filter((a) => a.estado === 'PENDIENTE').length
  );
  readonly leidas = computed(() => this.alertas().filter((a) => a.estado === 'LEIDA').length);
  readonly resueltas = computed(
    () => this.alertas().filter((a) => a.estado === 'RESUELTA').length
  );

  readonly visibles = computed(() => {
    const tipo = this.filtros.controls.tipoAlerta.value;
    if (!tipo) {
      return this.alertas();
    }
    return this.alertas().filter((a) => a.tipoAlerta === tipo);
  });

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    const estado = this.filtros.controls.estado.value || undefined;

    this.alertaService
      .listar(estado)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          const data = [...(response.data ?? [])].sort(
            (a, b) =>
              new Date(b.fechaGeneracion).getTime() - new Date(a.fechaGeneracion).getTime()
          );
          this.alertas.set(data);
        },
        error: (error: unknown) => {
          this.errorMessage.set(
            extractErrorMessage(error, 'No se pudieron cargar las alertas.')
          );
        },
      });
  }

  limpiar(): void {
    this.filtros.reset({ estado: '', tipoAlerta: '' });
    this.cargar();
  }

  transiciones(alerta: AlertaResponse): EstadoAlerta[] {
    switch (alerta.estado) {
      case 'PENDIENTE':
        return ['LEIDA', 'RESUELTA'];
      case 'LEIDA':
        return ['RESUELTA'];
      default:
        return [];
    }
  }

  cambiarEstado(alerta: AlertaResponse, nuevoEstado: EstadoAlerta): void {
    this.dialog
      .open<ConfirmDialogComponent, ConfirmDialogData, boolean>(ConfirmDialogComponent, {
        data: {
          title: 'Actualizar alerta',
          message: `¿Cambiar la alerta de "${alerta.productoNombre}" a ${nuevoEstado}?`,
          confirmLabel: `Marcar ${nuevoEstado}`,
        },
      })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) {
          return;
        }

        this.alertaService.actualizarEstado(alerta.id, { estado: nuevoEstado }).subscribe({
          next: () => {
            this.snackBar.open('Estado de alerta actualizado', 'Cerrar', { duration: 3000 });
            this.cargar();
          },
          error: (error: unknown) => {
            this.snackBar.open(
              extractErrorMessage(error, 'No se pudo actualizar la alerta.'),
              'Cerrar',
              { duration: 4500 }
            );
          },
        });
      });
  }
}
