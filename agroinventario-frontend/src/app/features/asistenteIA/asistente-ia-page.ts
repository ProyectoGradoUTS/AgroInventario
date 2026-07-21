import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { environment } from '../../../environments/environment';
import { AsistenteMensajeUi } from '../../core/models';
import { AsistenteIaService } from '../../core/services/asistente-ia.service';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header';

@Component({
  selector: 'app-asistente-ia-page',
  standalone: true,
  imports: [
    DatePipe,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatChipsModule,
    PageHeaderComponent,
  ],
  templateUrl: './asistente-ia-page.html',
  styleUrl: './asistente-ia-page.scss',
})
export class AsistenteIaPage implements OnInit {
  private readonly asistenteService = inject(AsistenteIaService);
  private readonly fb = inject(FormBuilder);

  readonly habilitado = this.asistenteService.isEnabled();
  readonly basePath = environment.asistenteIa.basePath;
  readonly enviando = signal(false);
  readonly mensajes = signal<AsistenteMensajeUi[]>([]);

  /** Sugerencias de UI (no son datos de API ni mocks de negocio). */
  readonly sugerencias = [
    '¿Qué productos tienen stock bajo?',
    'Resumen de alertas pendientes',
    '¿Cómo registrar una entrada de inventario?',
  ];

  readonly form = this.fb.nonNullable.group({
    pregunta: ['', [Validators.required, Validators.maxLength(1000)]],
  });

  ngOnInit(): void {
    this.mensajes.set([
      {
        id: 'sys-1',
        rol: 'sistema',
        texto: this.habilitado
          ? 'Asistente habilitado. Las consultas se enviarán al backend.'
          : 'Módulo preparado. El backend aún no expone el Asistente IA. No se realizan llamadas HTTP.',
        fecha: new Date().toISOString(),
      },
    ]);

    if (!this.habilitado) {
      this.form.disable({ emitEvent: false });
    }
  }

  usarSugerencia(texto: string): void {
    if (!this.habilitado) {
      return;
    }
    this.form.controls.pregunta.setValue(texto);
  }

  enviar(): void {
    if (!this.habilitado || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const pregunta = this.form.controls.pregunta.value.trim();
    if (!pregunta) {
      return;
    }

    const ahora = new Date().toISOString();
    this.mensajes.update((lista) => [
      ...lista,
      {
        id: `u-${Date.now()}`,
        rol: 'usuario',
        texto: pregunta,
        fecha: ahora,
      },
    ]);

    this.enviando.set(true);
    this.form.controls.pregunta.reset('');

    this.asistenteService.consultar({ pregunta }).subscribe({
      next: (response) => {
        this.enviando.set(false);
        this.mensajes.update((lista) => [
          ...lista,
          {
            id: `a-${Date.now()}`,
            rol: 'asistente',
            texto: response.data.respuesta,
            fecha: new Date().toISOString(),
          },
        ]);
      },
      error: (error: { message?: string }) => {
        this.enviando.set(false);
        this.mensajes.update((lista) => [
          ...lista,
          {
            id: `e-${Date.now()}`,
            rol: 'sistema',
            texto:
              error?.message ||
              'No fue posible contactar al asistente. Verifique que el backend esté disponible.',
            fecha: new Date().toISOString(),
          },
        ]);
      },
    });
  }
}
