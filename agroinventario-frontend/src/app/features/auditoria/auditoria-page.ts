import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { finalize } from 'rxjs';
import {
  EntidadAuditoria,
  PageResponse,
  AuditoriaResponse,
} from '../../core/models';
import { AuditoriaService } from '../../core/services/auditoria.service';
import { extractErrorMessage } from '../../core/utilities/error.util';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header';

@Component({
  selector: 'app-auditoria-page',
  standalone: true,
  imports: [
    DatePipe,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    PageHeaderComponent,
  ],
  templateUrl: './auditoria-page.html',
  styleUrl: './auditoria-page.scss',
})
export class AuditoriaPage implements OnInit {
  private readonly auditoriaService = inject(AuditoriaService);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly pageData = signal<PageResponse<AuditoriaResponse> | null>(null);

  readonly displayedColumns = [
    'fecha',
    'entidad',
    'entidadId',
    'accion',
    'usuario',
    'detalle',
  ];

  readonly entidades: Array<EntidadAuditoria | ''> = [
    '',
    'PRODUCTO',
    'CATEGORIA',
    'INVENTARIO',
    'ALERTA',
    'USUARIO',
  ];

  readonly filters = this.fb.nonNullable.group({
    entidad: ['' as EntidadAuditoria | ''],
  });

  pageIndex = 0;
  pageSize = 20;

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    const entidad = this.filters.controls.entidad.value;

    this.auditoriaService
      .listar({
        page: this.pageIndex,
        size: this.pageSize,
        entidad: entidad || undefined,
      })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => this.pageData.set(response.data),
        error: (error: unknown) => {
          this.errorMessage.set(
            extractErrorMessage(error, 'No se pudo cargar la auditoría.')
          );
        },
      });
  }

  buscar(): void {
    this.pageIndex = 0;
    this.cargar();
  }

  limpiar(): void {
    this.filters.reset({ entidad: '' });
    this.pageIndex = 0;
    this.cargar();
  }

  onPage(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.cargar();
  }
}
