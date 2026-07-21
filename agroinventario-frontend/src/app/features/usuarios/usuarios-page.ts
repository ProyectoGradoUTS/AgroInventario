import { Component } from '@angular/core';
import { FeaturePlaceholderComponent } from '../../shared/components/feature-placeholder/feature-placeholder';

@Component({
  selector: 'app-usuarios-page',
  standalone: true,
  imports: [FeaturePlaceholderComponent],
  template: `<app-feature-placeholder title="Usuarios" icon="group" phaseLabel="una fase posterior" />`,
})
export class UsuariosPage {}
