/** Envelope de éxito del backend: ApiResponse&lt;T&gt;. */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
