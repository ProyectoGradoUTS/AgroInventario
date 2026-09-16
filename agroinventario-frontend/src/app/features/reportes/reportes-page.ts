import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import {
  DashboardPrediccionData,
  DashboardRecomendacionData,
  DashboardService,
  DashboardSnapshot,
} from '../../core/services/dashboard.service';
import { extractErrorMessage } from '../../core/utilities/error.util';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header';

@Component({
  selector: 'app-reportes-page',
  standalone: true,
  imports: [
    DecimalPipe,
    RouterLink,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatProgressSpinnerModule,
    PageHeaderComponent,
  ],
  templateUrl: './reportes-page.html',
  styleUrl: './reportes-page.scss',
})
export class ReportesPage implements OnInit {
  private readonly dashboardService = inject(DashboardService);

  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly snapshot = signal<DashboardSnapshot | null>(null);

  readonly reposicionColumns = ['nombre', 'stock', 'agotamiento', 'cantidad', 'estrategia', 'mensaje'];
  readonly vencimientoColumns = ['nombre', 'stock', 'agotamiento', 'riesgo', 'mensaje'];

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.errorMessage.set(null);
    this.dashboardService
      .cargarResumen()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (data) => this.snapshot.set(data),
        error: (error: unknown) =>
          this.errorMessage.set(extractErrorMessage(error, 'No se pudieron cargar los reportes.')),
      });
  }

  reposicion(data: DashboardSnapshot): DashboardRecomendacionData[] {
    return data.recomendaciones;
  }

  vencimientos(data: DashboardSnapshot): DashboardPrediccionData[] {
    return data.predicciones.filter((item) => item.riesgo === 'ALTO' || item.riesgo === 'CRITICO');
  }
}
