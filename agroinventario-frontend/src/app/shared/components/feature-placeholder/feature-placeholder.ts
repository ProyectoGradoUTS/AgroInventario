import { Component, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { PageHeaderComponent } from '../page-header/page-header';

/** Shell reutilizable para módulos aún no implementados (Fases 3–10). */
@Component({
  selector: 'app-feature-placeholder',
  standalone: true,
  imports: [MatCardModule, MatIconModule, PageHeaderComponent],
  template: `
    <app-page-header [title]="title" [subtitle]="subtitle" [icon]="icon" />

    <mat-card class="placeholder-card">
      <mat-card-content>
        <div class="placeholder-card__body">
          <mat-icon>{{ icon || 'construction' }}</mat-icon>
          <div>
            <h2>{{ title }}</h2>
            <p>
              Estructura y rutas listas. La implementación funcional se completará en
              {{ phaseLabel }}.
            </p>
          </div>
        </div>
      </mat-card-content>
    </mat-card>
  `,
  styles: `
    .placeholder-card {
      border: 1px solid var(--agro-border);
      box-shadow: none;
      background: #fff;
    }

    .placeholder-card__body {
      display: flex;
      gap: 1rem;
      align-items: flex-start;
      padding: 0.5rem 0;
    }

    mat-icon {
      color: var(--agro-secondary);
      font-size: 2rem;
      width: 2rem;
      height: 2rem;
    }

    h2 {
      margin: 0 0 0.35rem;
      font-size: 1.1rem;
    }

    p {
      margin: 0;
      color: var(--agro-text-muted);
      max-width: 42rem;
      line-height: 1.5;
    }
  `,
})
export class FeaturePlaceholderComponent {
  @Input({ required: true }) title!: string;
  @Input() subtitle = 'Módulo preparado para consumir la API REST existente.';
  @Input() icon = 'construction';
  @Input() phaseLabel = 'una fase posterior';
}
