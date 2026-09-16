import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { finalize } from 'rxjs';
import { UsuarioResponse } from '../../core/models';
import { UsuarioService } from '../../core/services/usuario.service';
import { extractErrorMessage } from '../../core/utilities/error.util';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header';

@Component({
  selector: 'app-usuarios-page',
  standalone: true,
  imports: [
    DatePipe,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatProgressSpinnerModule,
    PageHeaderComponent,
  ],
  templateUrl: './usuarios-page.html',
  styleUrl: './usuarios-page.scss',
})
export class UsuariosPage implements OnInit {
  private readonly usuarioService = inject(UsuarioService);

  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly usuarios = signal<UsuarioResponse[]>([]);
  readonly columns = ['nombre', 'email', 'roles', 'estado', 'fecha'];

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.errorMessage.set(null);
    this.usuarioService
      .listar()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => this.usuarios.set(response.data ?? []),
        error: (error: unknown) =>
          this.errorMessage.set(extractErrorMessage(error, 'No se pudieron cargar los usuarios.')),
      });
  }
}
