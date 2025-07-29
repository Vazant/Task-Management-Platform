# TaskBoard Pro - Платформа управления проектами

Полнофункциональная платформа для управления проектами с современным Angular frontend и Spring Boot backend.

## 🏗️ Архитектура

```
Task-Management-Platform/
├── src/
│   ├── app/               # Angular Frontend
│   │   ├── features/      # Модули приложения
│   │   ├── core/          # Общие сервисы и модели
│   │   └── shared/        # Переиспользуемые компоненты
│   └── backend/           # Spring Boot Backend
│       ├── src/main/java/
│       │   └── com/taskboard/api/
│       │       ├── controller/ # REST API контроллеры
│       │       ├── service/    # Бизнес-логика
│       │       ├── model/      # JPA сущности
│       │       └── config/     # Конфигурации
├── package.json           # Angular зависимости
├── angular.json           # Angular конфигурация
└── README.md             # Документация проекта
```

## 🚀 Быстрый старт

### Frontend (Angular)

```bash
# Установка зависимостей
npm install

# Запуск в режиме разработки
npm start

# Приложение будет доступно на http://localhost:4200
```

### Backend (Spring Boot)

```bash
# Переход в папку backend
cd src/backend

# Запуск приложения
mvn spring-boot:run

# API будет доступен на http://localhost:3000/api
# H2 консоль: http://localhost:3000/api/h2-console
```

## 📋 Функциональность

### ✅ Реализовано

#### Frontend (Angular)
- ✅ **Аутентификация** - регистрация, вход, восстановление пароля
- ✅ **NgRx Store** - управление состоянием приложения
- ✅ **Guards** - защита роутов
- ✅ **Interceptors** - обработка HTTP запросов
- ✅ **Material Design** - современный UI
- ✅ **Responsive Design** - адаптивная верстка
- ✅ **Лендинг страница** - красивая главная страница

#### Backend (Spring Boot)
- ✅ **JWT аутентификация** - безопасные токены
- ✅ **Spring Security** - защита API
- ✅ **JPA/Hibernate** - работа с базой данных
- ✅ **H2 Database** - встроенная БД для разработки
- ✅ **CORS** - настройка для Angular
- ✅ **Валидация** - проверка входных данных
- ✅ **BCrypt** - хеширование паролей

### 🔄 API Endpoints

#### Аутентификация
- `POST /api/auth/register` - Регистрация
- `POST /api/auth/login` - Вход
- `POST /api/auth/refresh` - Обновление токена
- `POST /api/auth/forgot-password` - Восстановление пароля
- `POST /api/auth/reset-password` - Сброс пароля

## 🛠️ Технологии

### Frontend
- **Angular 17** - современный фреймворк
- **NgRx** - управление состоянием
- **Angular Material** - UI компоненты
- **TypeScript** - типизированный JavaScript
- **SCSS** - стили

### Backend
- **Spring Boot 3.2** - Java фреймворк
- **Spring Security** - безопасность
- **Spring Data JPA** - работа с БД
- **JWT** - токены аутентификации
- **H2 Database** - встроенная БД
- **Maven** - управление зависимостями

## 🔐 Безопасность

- **JWT токены** для аутентификации
- **BCrypt** для хеширования паролей
- **CORS** настроен для безопасного взаимодействия
- **Spring Security** для защиты endpoints
- **Валидация** всех входных данных

## 📊 База данных

### H2 (для разработки)
- **URL**: `jdbc:h2:mem:taskboarddb`
- **Username**: `sa`
- **Password**: `password`
- **Консоль**: `http://localhost:3000/api/h2-console`

### Модели данных
- **User** - пользователи системы
- **Project** - проекты (готово к реализации)
- **Task** - задачи (готово к реализации)
- **TimeEntry** - записи времени (готово к реализации)

## 🧪 Тестирование

### Frontend
```bash
npm test
```

### Backend
```bash
cd src/backend
mvn test
```

## 📦 Сборка

### Frontend
```bash
npm run build
```

### Backend
```bash
cd src/backend
mvn clean package
```

## 🔧 Конфигурация

### Frontend
- Порт: `4200`
- API URL: `http://localhost:3000/api`

### Backend
- Порт: `3000`
- Контекст: `/api`
- JWT Secret: настраивается в `application.properties`

## 🚨 Troubleshooting

### Проблемы с портами
Если порты заняты, измените в конфигурации:
- Frontend: `angular.json` → `serve.port`
- Backend: `src/backend/src/main/resources/application.properties` → `server.port`

### Проблемы с CORS
Проверьте настройки CORS в `SecurityConfig.java`

### Проблемы с JWT
Убедитесь, что JWT secret достаточно длинный (минимум 256 бит)

## 📈 Следующие шаги

1. **Реализация проектов** - CRUD операции для проектов
2. **Реализация задач** - Kanban доска, drag-and-drop
3. **Отслеживание времени** - таймер, логирование времени
4. **Аналитика** - графики и отчеты
5. **Уведомления** - email и push уведомления
6. **Файлы** - загрузка и управление файлами
7. **Комментарии** - система комментариев к задачам

## 🤝 Вклад в проект

1. Fork репозитория
2. Создайте feature branch
3. Внесите изменения
4. Добавьте тесты
5. Создайте Pull Request

## 📄 Лицензия

MIT License - см. файл [LICENSE](LICENSE)
