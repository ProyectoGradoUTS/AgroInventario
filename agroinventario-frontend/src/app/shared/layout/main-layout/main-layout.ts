import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Subscription } from 'rxjs';
import { NavbarComponent } from '../navbar/navbar';
import { SidebarComponent } from '../sidebar/sidebar';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, NavbarComponent],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.scss',
})
export class MainLayoutComponent implements OnInit, OnDestroy {
  private readonly breakpoint = inject(BreakpointObserver);
  private sub?: Subscription;

  readonly collapsed = signal(false);
  readonly mobileOpen = signal(false);
  readonly isMobile = signal(false);

  ngOnInit(): void {
    this.sub = this.breakpoint.observe([Breakpoints.Handset, Breakpoints.TabletPortrait]).subscribe(
      (state) => {
        this.isMobile.set(state.matches);
        if (state.matches) {
          this.collapsed.set(false);
          this.mobileOpen.set(false);
        }
      }
    );
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  toggleSidebar(): void {
    if (this.isMobile()) {
      this.mobileOpen.update((open) => !open);
      return;
    }
    this.collapsed.update((value) => !value);
  }

  closeMobile(): void {
    this.mobileOpen.set(false);
  }
}
