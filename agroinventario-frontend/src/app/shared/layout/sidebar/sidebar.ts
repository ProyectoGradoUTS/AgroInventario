import { Component, EventEmitter, Input, Output, computed, inject } from '@angular/core';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/authentication/auth.service';
import { APP_NAV_ITEMS, NavItem } from '../../../core/utilities/nav-items';

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

  readonly items = computed(() =>
    APP_NAV_ITEMS.filter((item) => this.canSee(item))
  );

  private canSee(item: NavItem): boolean {
    if (!item.roles?.length) {
      return true;
    }
    return this.auth.hasAnyRole(item.roles);
  }
}
