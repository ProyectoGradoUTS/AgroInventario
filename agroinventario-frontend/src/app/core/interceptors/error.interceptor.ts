import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { TokenStorageService } from '../authentication/token-storage.service';

/** Maneja 401 global: limpia sesión y redirige al login. */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenStorage = inject(TokenStorageService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const isAuthEndpoint =
        req.url.includes('/v1/auth/login') || req.url.includes('/v1/auth/register');

      if (error.status === 401 && !isAuthEndpoint) {
        tokenStorage.clear();
        void router.navigate(['/auth/login'], {
          queryParams: { returnUrl: router.url },
        });
      }

      return throwError(() => error);
    })
  );
};
