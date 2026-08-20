import { Component } from '@angular/core';
import { FeaturePlaceholderComponent } from '../../shared/components/feature-placeholder/feature-placeholder';

@Component({
  selector: 'app-reportes-page',
  standalone: true,
  imports: [FeaturePlaceholderComponent],
  template: `
    <app-feature-placeholder
      title="Reportes"
      subtitle="No hay endpoints de reportes en el backend actual. Módulo reservado."
      icon="assessment"
      phaseLabel="cuando el backend los exponga"
    />
  `,
})
export class ReportesPage {}
