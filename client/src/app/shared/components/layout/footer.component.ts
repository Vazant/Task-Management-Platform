import { Component } from '@angular/core';



@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [],
  template: `
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
  `,
  styles: [`
    /* Footer */
    .footer {
      background: #2c3e50;
      color: white;
      padding: 3rem 2rem 1rem;
      margin-top: auto;
    }

    .container {
      max-width: 1200px;
      margin: 0 auto;
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
      .footer-content {
        grid-template-columns: 1fr;
        text-align: center;
      }

      .footer-links {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class FooterComponent {}
