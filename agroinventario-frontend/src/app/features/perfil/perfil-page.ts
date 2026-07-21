import { Component } from '@angular/core';
import { FeaturePlaceholderComponent } from '../../shared/components/feature-placeholder/feature-placeholder';

@Component({
  selector: 'app-perfil-page',
  standalone: true,
  imports: [FeaturePlaceholderComponent],
  template: `
    <app-feature-placeholder
      title="Perfil"
      subtitle="Consumirá GET /api/v1/auth/me. Sin endpoints de actualización en el backend."
      icon="person"
      phaseLabel="una fase posterior"
    />
  `,
})
export class PerfilPage {}
