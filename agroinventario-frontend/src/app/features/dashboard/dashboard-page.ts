import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/authentication/auth.service';
import {
  DashboardService,
  DashboardSnapshot,
} from '../../core/services/dashboard.service';
import { extractErrorMessage } from '../../core/utilities/error.util';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [
    DatePipe,
    DecimalPipe,
    RouterLink,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatProgressSpinnerModule,
    PageHeaderComponent,
  ],
  templateUrl: './dashboard-page.html',
  styleUrl: './dashboard-page.scss',
})
export class DashboardPage implements OnInit {
  private readonly dashboardService = inject(DashboardService);
  private readonly auth = inject(AuthService);

  readonly user = this.auth.currentUser;
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly snapshot = signal<DashboardSnapshot | null>(null);

  readonly alertColumns = ['producto', 'tipo', 'estado', 'fecha'];
  readonly movimientoColumns = ['producto', 'tipo', 'cantidad', 'usuario', 'fecha'];
  readonly recomendacionColumns = ['nombre', 'stock', 'agotamiento', 'cantidad', 'estrategia'];
  readonly categoriaColumns = ['categoria', 'total', 'stockBajo', 'criticos', 'consumo', 'nivel'];

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
        error: (error: unknown) => {
          this.errorMessage.set(
            extractErrorMessage(error, 'No se pudo cargar el dashboard.')
          );
        },
      });
  }

  recomendacionesVisibles(data: DashboardSnapshot) {
    return data.recomendaciones.slice(0, 8);
  }

  prediccionesVisibles(data: DashboardSnapshot) {
    return [...data.predicciones]
      .sort((a, b) => a.diasHastaAgotamiento - b.diasHastaAgotamiento)
      .slice(0, 8);
  }
}
