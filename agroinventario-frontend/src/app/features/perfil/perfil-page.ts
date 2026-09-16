import { Component, OnInit, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/authentication/auth.service';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header';

@Component({
  selector: 'app-perfil-page',
  standalone: true,
  imports: [MatCardModule, MatButtonModule, MatIconModule, PageHeaderComponent],
  template: `
    <app-page-header
      title="Perfil"
      subtitle="Datos de la sesión actual. La contraseña se gestiona con el administrador."
      icon="person"
    />

    @if (user(); as session) {
      <mat-card class="perfil-card">
        <mat-card-content>
          <dl>
            <div>
              <dt>Nombre</dt>
              <dd>{{ session.nombre }}</dd>
            </div>
            <div>
              <dt>Correo</dt>
              <dd>{{ session.email }}</dd>
            </div>
            <div>
              <dt>Roles</dt>
              <dd>{{ session.roles.join(', ') }}</dd>
            </div>
            <div>
              <dt>Estado</dt>
              <dd>{{ session.estado || 'ACTIVO' }}</dd>
            </div>
          </dl>
          <button mat-stroked-button color="warn" type="button" (click)="salir()">
            <mat-icon>logout</mat-icon>
            Cerrar sesión
          </button>
        </mat-card-content>
      </mat-card>
    } @else {
      <p class="empty">No hay una sesión cargada.</p>
    }
  `,
  styles: `
    .perfil-card {
      max-width: 560px;
      border: 1px solid var(--agro-border);
      box-shadow: none;
    }
    dl {
      display: grid;
      gap: 0.85rem;
      margin: 0 0 1.25rem;
    }
    dt {
      font-size: 0.72rem;
      font-weight: 700;
      letter-spacing: 0.04em;
      text-transform: uppercase;
      color: var(--agro-text-muted);
    }
    dd {
      margin: 0.2rem 0 0;
      font-size: 1rem;
    }
    .empty {
      color: var(--agro-text-muted);
    }
  `,
})
export class PerfilPage implements OnInit {
  private readonly auth = inject(AuthService);
  readonly user = this.auth.currentUser;
  readonly actualizado = signal(false);

  ngOnInit(): void {
    this.auth.me().subscribe({
      next: () => this.actualizado.set(true),
      error: () => this.actualizado.set(false),
    });
  }

  salir(): void {
    this.auth.logout();
  }
}
