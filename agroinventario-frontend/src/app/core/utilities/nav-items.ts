export interface NavItem {
  label: string;
  route: string;
  icon: string;
  roles?: string[];
  section: NavSection;
}

export type NavSection = 'principal' | 'operacion' | 'inteligencia' | 'admin';

export interface NavSectionGroup {
  id: NavSection;
  label: string;
  items: NavItem[];
}

export const NAV_SECTION_LABELS: Record<NavSection, string> = {
  principal: 'Inicio',
  operacion: 'Inventario',
  inteligencia: 'Inteligencia',
  admin: 'Administración',
};

export const APP_NAV_ITEMS: NavItem[] = [
  { label: 'Dashboard', route: '/app/dashboard', icon: 'dashboard', section: 'principal' },
  { label: 'Productos', route: '/app/productos', icon: 'inventory_2', section: 'operacion' },
  { label: 'Categorías', route: '/app/categorias', icon: 'category', section: 'operacion' },
  { label: 'Inventario', route: '/app/inventario', icon: 'warehouse', section: 'operacion' },
  { label: 'Movimientos', route: '/app/movimientos', icon: 'swap_vert', section: 'operacion' },
  { label: 'Alertas', route: '/app/alertas', icon: 'notifications_active', section: 'operacion' },
  { label: 'Asistente IA', route: '/app/asistente-ia', icon: 'smart_toy', section: 'inteligencia' },
  { label: 'Reportes', route: '/app/reportes', icon: 'assessment', section: 'inteligencia' },
  { label: 'Auditoría', route: '/app/auditoria', icon: 'history', roles: ['ADMIN'], section: 'admin' },
  { label: 'Usuarios', route: '/app/usuarios', icon: 'group', roles: ['ADMIN'], section: 'admin' },
  { label: 'Perfil', route: '/app/perfil', icon: 'person', section: 'admin' },
];
