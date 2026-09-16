import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, ElementRef, OnInit, inject, signal, viewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { AsistenteEstadoResponse, AsistenteMensajeUi } from '../../core/models';
import { AsistenteIaService } from '../../core/services/asistente-ia.service';
import { extractErrorMessage } from '../../core/utilities/error.util';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header';

@Component({
  selector: 'app-asistente-ia-page',
  standalone: true,
  imports: [
    DatePipe,
    DecimalPipe,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    PageHeaderComponent,
  ],
  templateUrl: './asistente-ia-page.html',
  styleUrl: './asistente-ia-page.scss',
})
export class AsistenteIaPage implements OnInit {
  private readonly asistenteService = inject(AsistenteIaService);
  private readonly fb = inject(FormBuilder);
  private readonly chatThread = viewChild<ElementRef<HTMLDivElement>>('chatThread');

  readonly habilitado = this.asistenteService.isEnabled();
  readonly enviando = signal(false);
  readonly estado = signal<AsistenteEstadoResponse | null>(null);
  readonly mensajes = signal<AsistenteMensajeUi[]>([]);

  readonly sugerencias = [
    'Hola, ¿cómo está el inventario?',
    '¿Qué productos se van a agotar esta semana?',
    '¿Qué debo pedir para no desabastecerme?',
    '¿Qué vence pronto y me puede generar pérdidas?',
    '¿Cómo va la categoría de medicina?',
  ];

  readonly form = this.fb.nonNullable.group({
    pregunta: ['', [Validators.required, Validators.maxLength(1000)]],
  });

  ngOnInit(): void {
    this.mensajes.set([
      {
        id: 'bienvenida',
        rol: 'asistente',
        texto: this.habilitado
          ? 'Hola, soy el asistente del inventario. Puedo decirle cómo está el stock, qué se va a agotar, qué vence pronto y qué conviene pedir. Pregúnteme como si habláramos.'
          : 'El asistente no está disponible en este entorno.',
        fecha: new Date().toISOString(),
      },
    ]);

    if (!this.habilitado) {
      this.form.disable({ emitEvent: false });
      return;
    }

    this.asistenteService.obtenerEstado().subscribe({
      next: (estado) => this.estado.set(estado),
      error: (error: unknown) => {
        this.mensajes.update((lista) => [
          ...lista,
          {
            id: 'sys-err',
            rol: 'sistema',
            texto: extractErrorMessage(error, 'No pude leer el estado del inventario en este momento.'),
            fecha: new Date().toISOString(),
          },
        ]);
        this.scrollAlFinal();
      },
    });
  }

  usarSugerencia(texto: string): void {
    if (!this.habilitado) {
      return;
    }
    this.form.controls.pregunta.setValue(texto);
    this.enviar();
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

    this.mensajes.update((lista) => [
      ...lista,
      {
        id: `u-${Date.now()}`,
        rol: 'usuario',
        texto: pregunta,
        fecha: new Date().toISOString(),
      },
    ]);
    this.scrollAlFinal();

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
        this.scrollAlFinal();
      },
      error: (error: unknown) => {
        this.enviando.set(false);
        this.mensajes.update((lista) => [
          ...lista,
          {
            id: `e-${Date.now()}`,
            rol: 'asistente',
            texto: extractErrorMessage(
              error,
              'No pude consultar el inventario ahora. Intente de nuevo en un momento.'
            ),
            fecha: new Date().toISOString(),
          },
        ]);
        this.scrollAlFinal();
      },
    });
  }

  private scrollAlFinal(): void {
    queueMicrotask(() => {
      const el = this.chatThread()?.nativeElement;
      if (el) {
        el.scrollTop = el.scrollHeight;
      }
    });
  }
}
