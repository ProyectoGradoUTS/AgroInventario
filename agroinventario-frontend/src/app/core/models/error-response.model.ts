/** Error estándar del backend: ErrorResponse (sin ApiResponse). */
export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details: string[] | null;
}
