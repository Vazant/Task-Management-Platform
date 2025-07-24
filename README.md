# TaskBoard Pro

Современная веб-платформа для управления задачами с Kanban-доской, отслеживанием времени и аналитикой в реальном времени.

## 🚀 Возможности

### Основной функционал
- **Kanban-доска** с drag&drop для управления задачами
- **Отслеживание времени** для задач с детальной аналитикой
- **Лента активности** в реальном времени (RxJS)
- **Система авторизации** с ролями user/admin и guards
- **Фильтрация и поиск** задач с кастомными pipe
- **Глобальные HTTP-интерсепторы** (авторизация, обработка ошибок, спиннеры)
- **Формы** (reactive/template) с кастомными валидаторами
- **Вложенные подзадачи** и чек-листы
- **Приоритеты и метки** для задач
- **Модульная архитектура** с lazy loading

### Технические особенности
- **OnPush стратегия** для оптимизации производительности
- **Comprehensive unit testing** для компонентов, pipe, сервисов и NgRx effects
- **Современные RxJS паттерны** (combineLatest, switchMap, debounceTime)
- **NgRx best practices** (entity, effects, selectors)
- **TypeScript 5.0+** с строгой типизацией

## 🛠 Технологический стек

### Frontend
- **Angular 20+** - основной фреймворк
- **TypeScript 5.0+** - типизированный JavaScript
- **NgRx 19+** - управление состоянием (store, effects, entity, store-devtools)
- **RxJS 7.8+** - реактивное программирование
- **Angular Material** - UI компоненты
- **Angular CDK** - drag&drop, overlay

### Тестирование
- **Jasmine** - unit testing framework
- **Karma** - test runner
- **Angular Testing Utilities** - тестирование компонентов

### Инструменты разработки
- **Angular CLI** - генерация кода и сборка
- **ESLint** - линтинг кода
- **Prettier** - форматирование кода
- **Husky** - git hooks

### Сборка и деплой
- **Webpack** - сборка проекта
- **Docker** - контейнеризация
- **GitHub Actions** - CI/CD

## 🏗 Архитектура

### Импорты и структура

#### Path Mapping
Проект использует TypeScript path mapping для упрощения импортов:

```json
// tsconfig.json
{
  "paths": {
    "@app/*": ["app/*"],
    "@core/*": ["app/core/*"],
    "@shared/*": ["app/shared/*"],
    "@features/*": ["app/features/*"],
    "@models": ["app/core/models"],
    "@services": ["app/core/services"],
    "@utils": ["app/core/utils"]
  }
}
```

#### Barrel Exports
Централизованные точки доступа для импортов:

```typescript
// Правильно
import { User, Task } from '@models';
import { AuthService, NotificationService } from '@services';
import { ValidationUtils } from '@utils';

// Неправильно
import { User } from '../../../core/models/user.model';
import { AuthService } from '../../../core/services/auth.service';
```

#### Современные паттерны
- **Inject() функция** вместо constructor DI
- **Standalone компоненты** вместо NgModules
- **Строгая типизация** без any
- **OnPush Change Detection** для производительности

#### Автоматические проверки
```bash
npm run check-imports  # Проверка импортов
npm run lint          # ESLint проверка
npm run pre-commit    # Pre-commit проверки
```

### Структура модулей
```
src/
├── app/
│   ├── core/                    # Общие сервисы, гварды, интерсепторы
│   │   ├── guards/
│   │   ├── interceptors/
│   │   ├── services/
│   │   └── models/
│   ├── shared/                  # Переиспользуемые компоненты
│   │   ├── components/
│   │   ├── directives/
│   │   ├── pipes/
│   │   └── utils/
│   ├── features/                # Feature модули
│   │   ├── auth/               # Аутентификация
│   │   ├── dashboard/          # Главная страница
│   │   ├── projects/           # Управление проектами
│   │   ├── tasks/              # Управление задачами
│   │   ├── time-tracking/      # Отслеживание времени
│   │   ├── analytics/          # Аналитика
│   │   └── settings/           # Настройки
│   ├── store/                  # NgRx store
│   │   ├── actions/
│   │   ├── reducers/
│   │   ├── effects/
│   │   ├── selectors/
│   │   └── state/
│   └── app.module.ts
├── assets/
└── environments/
```

### NgRx Store Structure
```
store/
├── actions/                    # Actions для всех feature модулей
├── reducers/                   # Root reducer и feature reducers
├── effects/                    # Side effects
├── selectors/                  # Memoized selectors
└── state/                      # State interfaces
```

## 🚀 Быстрый старт

### Предварительные требования
- Node.js 18+ 
- npm 9+ или yarn 1.22+

### Установка
```bash
# Клонирование репозитория
git clone https://github.com/your-username/taskboard-pro.git
cd taskboard-pro

# Установка зависимостей
npm install
```

### Запуск в режиме разработки
```bash
# Запуск dev сервера
npm start

# Приложение будет доступно по адресу http://localhost:4200
```

### Сборка для продакшена
```bash
# Сборка оптимизированной версии
npm run build

# Файлы будут в папке dist/taskboard-pro/
```

## 🧪 Тестирование

### Запуск unit тестов
```bash
# Запуск всех тестов
npm test

# Запуск тестов с coverage
npm run test:coverage

# Запуск тестов в watch режиме
npm run test:watch
```

### Запуск e2e тестов
```bash
# Установка e2e зависимостей (если нужно)
npm install --save-dev protractor

# Запуск e2e тестов
npm run e2e
```

## 📁 Структура проекта

### Core Module
- **Guards**: AuthGuard, AdminGuard
- **Interceptors**: AuthInterceptor, ErrorInterceptor, LoadingInterceptor
- **Services**: ApiService, AuthService, NotificationService
- **Models**: интерфейсы для всех сущностей

### Shared Module
- **Components**: переиспользуемые UI компоненты
- **Directives**: кастомные директивы
- **Pipes**: кастомные pipe для фильтрации и форматирования
- **Utils**: утилитарные функции

### Feature Modules
Каждый feature модуль содержит:
- Components
- Services
- Store (actions, reducer, effects, selectors)
- Guards (если нужно)
- Module файл

## 🔧 Разработка

### Генерация компонентов
```bash
# Генерация feature модуля
ng generate module features/tasks --routing

# Генерация компонента
ng generate component features/tasks/components/task-list

# Генерация сервиса
ng generate service features/tasks/services/task

# Генерация NgRx store
ng generate @ngrx/schematics:store tasks --module=features/tasks/tasks.module.ts
```

### Code Style
Проект использует:
- **ESLint** для линтинга
- **Prettier** для форматирования
- **Husky** для pre-commit hooks

### Git Workflow
```bash
# Создание feature ветки
git checkout -b feature/task-management

# Коммит изменений
git add .
git commit -m "feat: add task management functionality"

# Push в репозиторий
git push origin feature/task-management
```

## 📊 Аналитика и мониторинг

### Производительность
- **OnPush Change Detection Strategy**
- **TrackBy функции** для ngFor
- **Lazy Loading** модулей
- **Memoized selectors** в NgRx

### Отладка
- **NgRx DevTools** для отладки состояния
- **Angular DevTools** для отладки компонентов
- **Redux DevTools Extension** для браузера

## 🚀 Деплой

### Docker
```bash
# Сборка Docker образа
docker build -t taskboard-pro .

# Запуск контейнера
docker run -p 80:80 taskboard-pro
```

### GitHub Actions
Автоматический деплой при push в main ветку:
- Сборка проекта
- Запуск тестов
- Деплой на staging/production

## 🤝 Вклад в проект

1. Fork репозитория
2. Создайте feature ветку (`git checkout -b feature/amazing-feature`)
3. Commit изменения (`git commit -m 'Add amazing feature'`)
4. Push в ветку (`git push origin feature/amazing-feature`)
5. Откройте Pull Request

### Guidelines для разработки
- Следуйте Angular Style Guide
- Пишите unit тесты для нового функционала
- Используйте TypeScript strict mode
- Документируйте сложную логику
- Следуйте conventional commits

## 📝 Лицензия

Этот проект лицензирован под Apache License 2.0 - см. файл [LICENSE](LICENSE) для деталей.

## 🗺 Roadmap

### v1.0.0 (Текущая версия)
- ✅ Базовая Kanban доска
- ✅ Система авторизации
- ✅ Управление задачами
- ✅ Отслеживание времени

### v1.1.0 (Планируется)
- 🔄 Расширенная аналитика
- 🔄 Интеграция с внешними сервисами
- 🔄 Мобильная версия

### v2.0.0 (Будущее)
- 📋 AI-ассистент для задач
- 📋 Интеграция с календарем
- 📋 Командная аналитика

## 📞 Контакты

- **Автор**: [Ваше имя]
- **Email**: [your.email@example.com]
- **GitHub**: [@your-username]
- **LinkedIn**: [your-linkedin]

## 🙏 Благодарности

- Angular Team за отличный фреймворк
- NgRx Team за state management решение
- Angular Material за UI компоненты
- Сообществу за вклад в развитие проекта
