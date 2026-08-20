import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResponse } from '../models';

/** Extrae mensaje legible del ErrorResponse del backend. */
export function extractErrorMessage(error: unknown, fallback = 'Ocurrió un error inesperado'): string {
  if (!(error instanceof HttpErrorResponse)) {
    return fallback;
  }

  const body = error.error as ErrorResponse | null;
  if (body?.details?.length) {
    return body.details.join('. ');
  }
  if (body?.message) {
    return body.message;
  }
  if (typeof error.error === 'string' && error.error.trim()) {
    return error.error;
  }

  return fallback;
}
