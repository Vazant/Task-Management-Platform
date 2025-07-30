import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-page-not-found',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <main class="not-found-container" role="main">
      <div class="background-animation">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
      </div>

      <section class="not-found-content" role="alert" aria-labelledby="error-title">
        <h1 id="error-title" class="error-code">404</h1>
        <h2 class="error-title">Страница не найдена</h2>
        <p class="error-description">
          К сожалению, запрашиваемая страница не существует или была перемещена.
        </p>

        <div class="buttons" role="group" aria-label="Действия навигации">
          <button
            class="btn btn--outline"
            (click)="goBack()"
            type="button"
            aria-label="Вернуться на предыдущую страницу">
            <lucide-icon name="arrow-left" class="icon" aria-hidden="true"></lucide-icon>
            <span>Назад</span>
          </button>
          <button
            class="btn btn--primary"
            (click)="goHome()"
            type="button"
            aria-label="Перейти на главную страницу">
            <lucide-icon name="home" class="icon" aria-hidden="true"></lucide-icon>
            <span>На главную</span>
          </button>
        </div>
      </section>
    </main>
  `,
  styles: [`
    .not-found-container {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
      padding: 1rem;
      background: linear-gradient(135deg,
        #667eea 0%,
        #764ba2 25%,
        #e8e6ff 50%,
        #f3f2ff 75%,
        #ffffff 100%);
      position: relative;
      overflow: hidden;
    }

    .background-animation {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      pointer-events: none;
    }

    .floating-shape {
      position: absolute;
      border-radius: 50%;
      background: rgba(102, 126, 234, 0.05);
      animation: float 8s ease-in-out infinite;
      will-change: transform;
    }

    .shape-1 {
      width: clamp(40px, 6vw, 80px);
      height: clamp(40px, 6vw, 80px);
      top: 15%;
      left: 8%;
      animation-delay: 0s;
    }

    .shape-2 {
      width: clamp(60px, 8vw, 120px);
      height: clamp(60px, 8vw, 120px);
      top: 65%;
      right: 12%;
      animation-delay: 2s;
    }

    .shape-3 {
      width: clamp(30px, 4vw, 60px);
      height: clamp(30px, 4vw, 60px);
      bottom: 25%;
      left: 15%;
      animation-delay: 4s;
    }

    @keyframes float {
      0%, 100% {
        transform: translateY(0px) rotate(0deg);
        opacity: 0.6;
      }
      50% {
        transform: translateY(-15px) rotate(90deg);
        opacity: 0.9;
      }
    }

    .not-found-content {
      text-align: center;
      max-width: min(600px, 90vw);
      background: #ffffff;
      padding: clamp(2rem, 6vw, 4rem) clamp(1.5rem, 4vw, 3rem);
      border-radius: 16px;
      box-shadow: 0 4px 16px rgba(102, 126, 234, 0.1);
      position: relative;
      z-index: 1;
      animation: slideIn 0.6s cubic-bezier(0.4, 0, 0.2, 1);
    }

    @keyframes slideIn {
      from {
        opacity: 0;
        transform: translateY(20px) scale(0.98);
      }
      to {
        opacity: 1;
        transform: translateY(0) scale(1);
      }
    }

    .error-code {
      font-size: clamp(4rem, 15vw, 6rem);
      font-weight: 900;
      color: #667eea;
      line-height: 0.9;
      margin-bottom: clamp(1rem, 3vw, 1.5rem);
      text-align: center;
    }

    .error-title {
      font-size: clamp(1.5rem, 5vw, 2rem);
      font-weight: 600;
      color: #2d3748;
      margin-bottom: clamp(1rem, 3vw, 1.5rem);
      line-height: 1.2;
      text-align: center;
    }

    .error-description {
      font-size: clamp(0.875rem, 2.5vw, 1rem);
      color: #4a5568;
      margin-bottom: clamp(2rem, 5vw, 3rem);
      line-height: 1.6;
      max-width: 450px;
      margin-left: auto;
      margin-right: auto;
      text-align: center;
    }

    .buttons {
      display: flex;
      gap: clamp(1rem, 3vw, 1.5rem);
      justify-content: center;
      flex-wrap: wrap;
    }

    .btn {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 0.75rem;
      padding: 12px 24px;
      border: none;
      border-radius: 8px;
      font-size: clamp(0.875rem, 2.5vw, 1rem);
      font-weight: 600;
      cursor: pointer;
      transition: all 0.2s ease;
      text-decoration: none;
      position: relative;
      overflow: hidden;
      min-width: clamp(120px, 25vw, 160px);
      outline: none;
    }

    .btn--primary {
      background: linear-gradient(90deg, #667eea, #764ba2);
      color: #ffffff;
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.2);
    }

    .btn--primary:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
    }

    .btn--primary:focus {
      outline: 2px solid #667eea;
      outline-offset: 2px;
    }

    .btn--outline {
      background: #ffffff;
      border: 2px solid;
      border-image: linear-gradient(90deg, #667eea, #764ba2) 1;
      color: #667eea;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    }

    .btn--outline:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
      background: linear-gradient(90deg, #667eea, #764ba2);
      color: #ffffff;
    }

    .btn--outline:focus {
      outline: 2px solid #667eea;
      outline-offset: 2px;
    }

    .icon {
      width: clamp(1rem, 2.5vw, 1.2rem);
      height: clamp(1rem, 2.5vw, 1.2rem);
      transition: transform 0.2s ease;
      flex-shrink: 0;
    }

    .btn:hover .icon {
      transform: scale(1.1);
    }

    /* Адаптивность */
    @media (max-width: 768px) {
      .not-found-container {
        padding: 0.5rem;
      }

      .not-found-content {
        margin: 0.5rem;
        padding: clamp(1.5rem, 6vw, 2rem) clamp(1rem, 4vw, 1.5rem);
      }

      .buttons {
        flex-direction: column;
        align-items: center;
        gap: 1rem;
      }

      .btn {
        width: 100%;
        max-width: 280px;
      }
    }

    @media (max-width: 480px) {
      .not-found-content {
        padding: clamp(1.5rem, 6vw, 2.5rem) clamp(1rem, 4vw, 1.5rem);
      }

      .error-code {
        font-size: clamp(3rem, 20vw, 4rem);
      }

      .error-title {
        font-size: clamp(1.25rem, 8vw, 1.5rem);
      }

      .error-description {
        font-size: clamp(0.8rem, 4vw, 0.875rem);
      }
    }

    /* Улучшения для слабых устройств */
    @media (prefers-reduced-motion: reduce) {
      .floating-shape {
        animation: none;
      }

      .btn {
        transition: none;
      }

      .btn:hover {
        transform: none;
      }

      .btn:hover .icon {
        transform: none;
      }
    }

    /* Поддержка высокого DPI */
    @media (-webkit-min-device-pixel-ratio: 2), (min-resolution: 192dpi) {
      .not-found-content {
        box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
      }
    }

    /* Темная тема - только если пользователь явно предпочитает */
    @media (prefers-color-scheme: dark) {
      .not-found-content {
        background: #1a202c;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
      }

      .error-code {
        color: #667eea;
      }

      .error-title {
        color: #f7fafc;
      }

      .error-description {
        color: #cbd5e0;
      }

      .btn--outline {
        background: #1a202c;
        color: #667eea;
        border-color: #667eea;
      }

      .btn--outline:hover {
        background: linear-gradient(90deg, #667eea, #764ba2);
        color: #ffffff;
      }
    }
  `]
})
export class PageNotFoundComponent {
  private readonly router = inject(Router);

  goBack(): void {
    window.history.back();
  }

  goHome(): void {
    this.router.navigate(['/']);
  }
}
