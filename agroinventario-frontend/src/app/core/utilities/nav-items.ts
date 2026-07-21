export interface NavItem {
  label: string;
  route: string;
  icon: string;
  roles?: string[];
}

/** Menú lateral ERP. Usuarios y Auditoría solo ADMIN. */
export const APP_NAV_ITEMS: NavItem[] = [
  { label: 'Dashboard', route: '/app/dashboard', icon: 'dashboard' },
  { label: 'Productos', route: '/app/productos', icon: 'inventory_2' },
  { label: 'Categorías', route: '/app/categorias', icon: 'category' },
  { label: 'Inventario', route: '/app/inventario', icon: 'warehouse' },
  { label: 'Movimientos', route: '/app/movimientos', icon: 'swap_vert' },
  { label: 'Alertas', route: '/app/alertas', icon: 'notifications_active' },
  { label: 'Auditoría', route: '/app/auditoria', icon: 'history', roles: ['ADMIN'] },
  { label: 'Usuarios', route: '/app/usuarios', icon: 'group', roles: ['ADMIN'] },
  { label: 'Reportes', route: '/app/reportes', icon: 'assessment' },
  { label: 'Asistente IA', route: '/app/asistente-ia', icon: 'smart_toy' },
  { label: 'Perfil', route: '/app/perfil', icon: 'person' },
];
