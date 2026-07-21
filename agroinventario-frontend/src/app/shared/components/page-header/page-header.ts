import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-page-header',
  standalone: true,
  imports: [MatIconModule],
  template: `
    <header class="page-header">
      <div class="page-header__text">
        @if (icon) {
          <mat-icon class="page-header__icon">{{ icon }}</mat-icon>
        }
        <div>
          <h1>{{ title }}</h1>
          @if (subtitle) {
            <p>{{ subtitle }}</p>
          }
        </div>
      </div>
      <div class="page-header__actions">
        <ng-content />
      </div>
    </header>
  `,
  styles: `
    .page-header {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      gap: 1rem;
      margin-bottom: 1.5rem;
    }

    .page-header__text {
      display: flex;
      gap: 0.75rem;
      align-items: flex-start;
    }

    .page-header__icon {
      color: var(--agro-primary);
      margin-top: 0.2rem;
    }

    h1 {
      margin: 0;
      font-size: 1.5rem;
      font-weight: 600;
      color: var(--agro-text);
      letter-spacing: -0.02em;
    }

    p {
      margin: 0.25rem 0 0;
      color: var(--agro-text-muted);
      font-size: 0.875rem;
    }

    .page-header__actions {
      display: flex;
      gap: 0.5rem;
      flex-wrap: wrap;
    }
  `,
})
export class PageHeaderComponent {
  @Input({ required: true }) title!: string;
  @Input() subtitle = '';
  @Input() icon = '';
}
