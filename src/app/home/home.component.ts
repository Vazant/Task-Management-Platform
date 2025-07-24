import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, CommonModule],
  template: `
    <div class="home-container">
      <!-- Header -->
      <header class="header">
        <div class="header-content">
          <div class="logo">
            <h1>TaskBoard Pro</h1>
          </div>
          <nav class="nav">
            <a routerLink="/" class="nav-link active">Главная</a>
            <a routerLink="/auth/login" class="nav-link">Войти</a>
            <a routerLink="/auth/register" class="nav-btn">Регистрация</a>
          </nav>
        </div>
      </header>

      <!-- Hero Section -->
      <section class="hero">
        <div class="hero-content">
          <div class="hero-text">
            <h1 class="hero-title">Управляйте проектами эффективно</h1>
            <p class="hero-subtitle">
              Мощная платформа для командной работы, управления задачами и отслеживания прогресса
            </p>
            <div class="hero-buttons">
              <a routerLink="/auth/register" class="btn btn-primary">Начать бесплатно</a>
              <a routerLink="/auth/login" class="btn btn-outline">Войти в систему</a>
            </div>
          </div>
          <div class="hero-image">
            <div class="mockup">
              <div class="mockup-header">
                <div class="mockup-dot red"></div>
                <div class="mockup-dot yellow"></div>
                <div class="mockup-dot green"></div>
              </div>
              <div class="mockup-content">
                <div class="mockup-card">
                  <div class="mockup-card-header">
                    <div class="mockup-avatar"></div>
                    <div class="mockup-text">
                      <div class="mockup-line short"></div>
                      <div class="mockup-line long"></div>
                    </div>
                  </div>
                </div>
                <div class="mockup-card">
                  <div class="mockup-card-header">
                    <div class="mockup-avatar"></div>
                    <div class="mockup-text">
                      <div class="mockup-line short"></div>
                      <div class="mockup-line long"></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section class="features">
        <div class="container">
          <div class="section-header">
            <h2>Все что нужно для управления проектами</h2>
            <p>Полный набор инструментов для эффективной работы команды</p>
          </div>

          <div class="features-grid">
            <div class="feature-card">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M9 11H1l8-8 8 8h-8v8z"/>
                </svg>
              </div>
              <h3>Управление проектами</h3>
              <p>Создавайте проекты, добавляйте участников и отслеживайте прогресс в реальном времени</p>
            </div>

            <div class="feature-card">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                  <line x1="9" y1="9" x2="15" y2="9"/>
                  <line x1="9" y1="12" x2="15" y2="12"/>
                  <line x1="9" y1="15" x2="13" y2="15"/>
                </svg>
              </div>
              <h3>Kanban доска</h3>
              <p>Визуальное управление задачами с drag-and-drop функционалом</p>
            </div>

            <div class="feature-card">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <polyline points="12,6 12,12 16,14"/>
                </svg>
              </div>
              <h3>Отслеживание времени</h3>
              <p>Встроенный таймер и детальная аналитика времени работы</p>
            </div>

            <div class="feature-card">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 3v18h18"/>
                  <path d="M18.7 8l-5.1 5.2-2.8-2.7L7 14.3"/>
                </svg>
              </div>
              <h3>Аналитика и отчеты</h3>
              <p>Подробная статистика производительности и автоматические отчеты</p>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA Section -->
      <section class="cta">
        <div class="container">
          <div class="cta-content">
            <h2>Готовы начать?</h2>
            <p>Присоединяйтесь к тысячам команд, которые уже используют TaskBoard Pro</p>
            <a routerLink="/auth/register" class="btn btn-primary btn-large">Создать аккаунт</a>
          </div>
        </div>
      </section>

      <!-- Footer -->
      <footer class="footer">
        <div class="container">
          <div class="footer-content">
            <div class="footer-logo">
              <h3>TaskBoard Pro</h3>
              <p>Управление проектами стало проще</p>
            </div>
            <div class="footer-links">
              <div class="footer-section">
                <h4>Продукт</h4>
                <a href="#">Возможности</a>
                <a href="#">Цены</a>
                <a href="#">Интеграции</a>
              </div>
              <div class="footer-section">
                <h4>Поддержка</h4>
                <a href="#">Документация</a>
                <a href="#">FAQ</a>
                <a href="#">Контакты</a>
              </div>
              <div class="footer-section">
                <h4>Компания</h4>
                <a href="#">О нас</a>
                <a href="#">Блог</a>
                <a href="#">Карьера</a>
              </div>
            </div>
          </div>
          <div class="footer-bottom">
            <p>&copy; 2024 TaskBoard Pro. Все права защищены.</p>
          </div>
        </div>
      </footer>
    </div>
  `,
  styles: [`
    .home-container {
      background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    }

    /* Header */
    .header {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      border-bottom: 1px solid rgba(0, 0, 0, 0.1);
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      z-index: 1000;
    }

    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 1rem 2rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .logo h1 {
      font-size: 1.5rem;
      font-weight: 700;
      color: #2c3e50;
      margin: 0;
    }

    .nav {
      display: flex;
      align-items: center;
      gap: 2rem;
    }

    .nav-link {
      text-decoration: none;
      color: #6c757d;
      font-weight: 500;
      transition: color 0.3s ease;
    }

    .nav-link:hover,
    .nav-link.active {
      color: #3498db;
    }

    .nav-btn {
      background: #3498db;
      color: white;
      padding: 0.5rem 1.5rem;
      border-radius: 6px;
      text-decoration: none;
      font-weight: 500;
      transition: background 0.3s ease;
    }

    .nav-btn:hover {
      background: #2980b9;
    }

    /* Hero Section */
    .hero {
      padding: 8rem 2rem 4rem;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
    }

    .hero-content {
      max-width: 1200px;
      margin: 0 auto;
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 4rem;
      align-items: center;
    }

    .hero-title {
      font-size: 3.5rem;
      font-weight: 700;
      margin-bottom: 1.5rem;
      line-height: 1.2;
    }

    .hero-subtitle {
      font-size: 1.25rem;
      margin-bottom: 2rem;
      opacity: 0.9;
      line-height: 1.6;
    }

    .hero-buttons {
      display: flex;
      gap: 1rem;
      flex-wrap: wrap;
    }

    .btn {
      padding: 1rem 2rem;
      border-radius: 8px;
      text-decoration: none;
      font-weight: 600;
      transition: all 0.3s ease;
      display: inline-block;
      border: none;
      cursor: pointer;
    }

    .btn-primary {
      background: #2ecc71;
      color: white;
    }

    .btn-primary:hover {
      background: #27ae60;
      transform: translateY(-2px);
    }

    .btn-outline {
      background: transparent;
      color: white;
      border: 2px solid white;
    }

    .btn-outline:hover {
      background: white;
      color: #667eea;
    }

    .btn-large {
      padding: 1.25rem 2.5rem;
      font-size: 1.1rem;
    }

    /* Mockup */
    .hero-image {
      display: flex;
      justify-content: center;
    }

    .mockup {
      background: white;
      border-radius: 12px;
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
      overflow: hidden;
      width: 100%;
      max-width: 400px;
    }

    .mockup-header {
      background: #f8f9fa;
      padding: 1rem;
      display: flex;
      gap: 0.5rem;
    }

    .mockup-dot {
      width: 12px;
      height: 12px;
      border-radius: 50%;
    }

    .mockup-dot.red { background: #ff5f56; }
    .mockup-dot.yellow { background: #ffbd2e; }
    .mockup-dot.green { background: #27ca3f; }

    .mockup-content {
      padding: 1.5rem;
    }

    .mockup-card {
      background: #f8f9fa;
      border-radius: 8px;
      padding: 1rem;
      margin-bottom: 1rem;
    }

    .mockup-card-header {
      display: flex;
      align-items: center;
      gap: 0.75rem;
    }

    .mockup-avatar {
      width: 32px;
      height: 32px;
      background: #3498db;
      border-radius: 50%;
    }

    .mockup-text {
      flex: 1;
    }

    .mockup-line {
      background: #dee2e6;
      border-radius: 2px;
      margin-bottom: 0.25rem;
    }

    .mockup-line.short {
      height: 8px;
      width: 60%;
    }

    .mockup-line.long {
      height: 8px;
      width: 100%;
    }

    /* Features Section */
    .features {
      padding: 5rem 2rem;
      background: white;
    }

    .container {
      max-width: 1200px;
      margin: 0 auto;
    }

    .section-header {
      text-align: center;
      margin-bottom: 4rem;
    }

    .section-header h2 {
      font-size: 2.5rem;
      color: #2c3e50;
      margin-bottom: 1rem;
    }

    .section-header p {
      font-size: 1.1rem;
      color: #6c757d;
    }

    .features-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 2rem;
    }

    .feature-card {
      padding: 2rem;
      border-radius: 12px;
      background: #f8f9fa;
      text-align: center;
      transition: transform 0.3s ease, box-shadow 0.3s ease;
    }

    .feature-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    }

    .feature-icon {
      width: 64px;
      height: 64px;
      background: #3498db;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 1.5rem;
      color: white;
    }

    .feature-icon svg {
      width: 32px;
      height: 32px;
    }

    .feature-card h3 {
      font-size: 1.5rem;
      color: #2c3e50;
      margin-bottom: 1rem;
    }

    .feature-card p {
      color: #6c757d;
      line-height: 1.6;
    }

    /* CTA Section */
    .cta {
      padding: 5rem 2rem;
      background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
      color: white;
      text-align: center;
    }

    .cta-content h2 {
      font-size: 2.5rem;
      margin-bottom: 1rem;
    }

    .cta-content p {
      font-size: 1.1rem;
      margin-bottom: 2rem;
      opacity: 0.9;
    }

    /* Footer */
    .footer {
      background: #2c3e50;
      color: white;
      padding: 3rem 2rem 1rem;
    }

    .footer-content {
      display: grid;
      grid-template-columns: 1fr 2fr;
      gap: 3rem;
      margin-bottom: 2rem;
    }

    .footer-logo h3 {
      font-size: 1.5rem;
      margin-bottom: 0.5rem;
    }

    .footer-logo p {
      opacity: 0.8;
    }

    .footer-links {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 2rem;
    }

    .footer-section h4 {
      margin-bottom: 1rem;
      color: #3498db;
    }

    .footer-section a {
      display: block;
      color: rgba(255, 255, 255, 0.8);
      text-decoration: none;
      margin-bottom: 0.5rem;
      transition: color 0.3s ease;
    }

    .footer-section a:hover {
      color: white;
    }

    .footer-bottom {
      border-top: 1px solid rgba(255, 255, 255, 0.1);
      padding-top: 1rem;
      text-align: center;
      opacity: 0.8;
    }

    /* Responsive */
    @media (max-width: 768px) {
      .hero-content {
        grid-template-columns: 1fr;
        text-align: center;
      }

      .hero-title {
        font-size: 2.5rem;
      }

      .hero-buttons {
        justify-content: center;
      }

      .features-grid {
        grid-template-columns: 1fr;
      }

      .footer-content {
        grid-template-columns: 1fr;
        text-align: center;
      }

      .footer-links {
        grid-template-columns: 1fr;
      }

      .nav {
        gap: 1rem;
      }
    }
  `]
})
export class HomeComponent {
  // Простая главная страница без логики
}
