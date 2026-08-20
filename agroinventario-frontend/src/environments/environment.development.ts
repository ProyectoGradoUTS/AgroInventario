export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  appName: 'Agro Inventario',
  tokenStorageKey: 'agro_inventario_token',
  userStorageKey: 'agro_inventario_user',
  /**
   * Asistente IA — deshabilitado hasta que el backend publique el contrato.
   * basePath es provisional; alinear con la ruta real sin inventar DTOs definitivos.
   */
  asistenteIa: {
    enabled: false,
    basePath: '/v1/asistente',
  },
};
