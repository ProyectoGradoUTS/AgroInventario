import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/authentication/auth.service';
import { LoginRequest } from '../../../core/models';
import { extractErrorMessage } from '../../../core/utilities/error.util';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './login-page.html',
  styleUrl: './login-page.scss',
})
export class LoginPage {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly hidePassword = signal(true);
  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  submit(): void {
    this.errorMessage.set(null);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const request: LoginRequest = {
      email: this.form.controls.email.value.trim(),
      password: this.form.controls.password.value,
    };

    this.loading.set(true);
    this.form.disable({ emitEvent: false });

    this.auth
      .login(request)
      .pipe(
        finalize(() => {
          this.loading.set(false);
          this.form.enable({ emitEvent: false });
        })
      )
      .subscribe({
        next: () => {
          const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') || '/app/dashboard';
          void this.router.navigateByUrl(returnUrl);
        },
        error: (error: unknown) => {
          this.errorMessage.set(this.resolveLoginError(error));
        },
      });
  }

  togglePasswordVisibility(): void {
    this.hidePassword.update((value) => !value);
  }

  private resolveLoginError(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.status === 0) {
      return 'No se pudo conectar con el servidor. Verifique que la API esté en ejecución.';
    }

    return extractErrorMessage(error, 'No se pudo iniciar sesión. Verifique sus credenciales.');
  }
}
