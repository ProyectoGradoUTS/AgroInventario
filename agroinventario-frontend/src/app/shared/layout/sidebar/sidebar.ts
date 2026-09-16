import { Component, EventEmitter, Input, Output, computed, inject } from '@angular/core';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/authentication/auth.service';
import {
  APP_NAV_ITEMS,
  NAV_SECTION_LABELS,
  NavItem,
  NavSection,
  NavSectionGroup,
} from '../../../core/utilities/nav-items';

const SECTION_ORDER: NavSection[] = ['principal', 'operacion', 'inteligencia', 'admin'];

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [MatListModule, MatIconModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss',
})
export class SidebarComponent {
  private readonly auth = inject(AuthService);

  @Input() collapsed = false;
  @Output() navigate = new EventEmitter<void>();

  readonly groups = computed<NavSectionGroup[]>(() => {
    const visible = APP_NAV_ITEMS.filter((item) => this.canSee(item));
    return SECTION_ORDER.map((id) => ({
      id,
      label: NAV_SECTION_LABELS[id],
      items: visible.filter((item) => item.section === id),
    })).filter((group) => group.items.length > 0);
  });

  private canSee(item: NavItem): boolean {
    if (!item.roles?.length) {
      return true;
    }
    return this.auth.hasAnyRole(item.roles);
  }
}
