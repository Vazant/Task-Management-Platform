import { Component, AfterViewInit, OnDestroy, ViewChild, ElementRef, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { LayoutComponent } from '@shared/components/layout';
import {
  LucideAngularModule,
  User,
  Palette,
  Bell,
  Shield,
  Grid3X3,
  Link,
  Settings,
  ChevronLeft,
  ChevronRight
} from 'lucide-angular';

interface SettingsTab {
  label: string;
  route: string;
  icon: typeof User; // Using typeof for Lucide icon type
  description: string;
}

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, LayoutComponent, LucideAngularModule]
})
export class SettingsComponent implements AfterViewInit, OnDestroy {
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  @ViewChild('navTabs', { static: false }) navTabsRef!: ElementRef<HTMLElement>;

  // Lucide icons для standalone компонентов
  readonly UserIcon = User;
  readonly PaletteIcon = Palette;
  readonly BellIcon = Bell;
  readonly ShieldIcon = Shield;
  readonly GridIcon = Grid3X3;
  readonly LinkIcon = Link;
  readonly SettingsIcon = Settings;
  readonly ChevronLeft = ChevronLeft;
  readonly ChevronRight = ChevronRight;

  // Scroll state
  canScrollLeft = false;
  canScrollRight = false;
  isScrolling = false;

  private scrollHandler!: () => void;
  private resizeHandler!: () => void;
  private scrollTimeout?: number;

  settingsTabs: SettingsTab[] = [
    { label: 'Профиль', route: 'profile', icon: this.UserIcon, description: 'Личная информация и настройки профиля' },
    { label: 'Внешний вид', route: 'appearance', icon: this.PaletteIcon, description: 'Темы, язык и настройки отображения' },
    { label: 'Уведомления', route: 'notifications', icon: this.BellIcon, description: 'Настройки уведомлений и оповещений' },
    { label: 'Безопасность', route: 'security', icon: this.ShieldIcon, description: 'Пароль, 2FA и безопасность аккаунта' },
    { label: 'Рабочее пространство', route: 'workspace', icon: this.GridIcon, description: 'Настройки интерфейса и рабочего пространства' },
    { label: 'Интеграции', route: 'integrations', icon: this.LinkIcon, description: 'Подключение внешних сервисов и API' },
    { label: 'Дополнительно', route: 'advanced', icon: this.SettingsIcon, description: 'Дополнительные настройки и управление данными' }
  ];

  // Helpers for template
  getCurrentTab(): SettingsTab {
    const currentRoute = this.router.url.split('/').pop() ?? 'profile';
    return this.settingsTabs.find(tab => tab.route === currentRoute) ?? this.settingsTabs[0];
  }
  getCurrentTabIcon(): typeof User { return this.getCurrentTab().icon; }
  getCurrentTabLabel(): string { return this.getCurrentTab().label; }
  getCurrentTabDescription(): string { return this.getCurrentTab().description; }

  ngAfterViewInit(): void {
    this.setupScrollHandlers();
    this.updateScrollState();
  }

  ngOnDestroy(): void {
    this.cleanupScrollHandlers();
  }

  private setupScrollHandlers(): void {
    this.scrollHandler = () => {
      this.updateScrollState();
      this.isScrolling = true;

      if (this.scrollTimeout) {
        clearTimeout(this.scrollTimeout);
      }

      this.scrollTimeout = window.setTimeout(() => {
        this.isScrolling = false;
        this.cdr.detectChanges();
      }, 150);
    };

    this.resizeHandler = () => {
      this.updateScrollState();
    };

    const navTabs = this.navTabsRef?.nativeElement;
    if (navTabs) {
      navTabs.addEventListener('scroll', this.scrollHandler);
      window.addEventListener('resize', this.resizeHandler);
    }
  }

  private cleanupScrollHandlers(): void {
    const navTabs = this.navTabsRef?.nativeElement;
    if (navTabs) {
      navTabs.removeEventListener('scroll', this.scrollHandler);
      window.removeEventListener('resize', this.resizeHandler);
    }

    if (this.scrollTimeout) {
      clearTimeout(this.scrollTimeout);
    }
  }

  private updateScrollState(): void {
    const navTabs = this.navTabsRef?.nativeElement;
    if (!navTabs) return;

    this.canScrollLeft = navTabs.scrollLeft > 0;
    this.canScrollRight = navTabs.scrollLeft < (navTabs.scrollWidth - navTabs.clientWidth);
    this.cdr.detectChanges();
  }

  scrollLeft(): void {
    const navTabs = this.navTabsRef?.nativeElement;
    if (navTabs) {
      navTabs.scrollBy({ left: -200, behavior: 'smooth' });
    }
  }

  scrollRight(): void {
    const navTabs = this.navTabsRef?.nativeElement;
    if (navTabs) {
      navTabs.scrollBy({ left: 200, behavior: 'smooth' });
    }
  }

  trackByRoute(index: number, tab: SettingsTab): string {
    return tab.route;
  }
}
