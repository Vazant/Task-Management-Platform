# Task Management Platform

Современная платформа для управления задачами и проектами с микросервисной архитектурой.

[![Build Status](https://github.com/your-org/task-management/workflows/CI/badge.svg)](https://github.com/your-org/task-management/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 🚀 Быстрый старт

### Предварительные требования
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL 13+

### Запуск
```bash
# Клонирование
git clone https://github.com/your-org/task-management.git
cd task-management

# Запуск с Docker
cd microservices/infrastructure/docker
./run-with-env.sh development up -d
```

Приложение будет доступно по адресу: http://localhost:4200

## ✨ Возможности

- 📋 **Управление задачами** - Полный CRUD функционал с Kanban досками
- 👥 **Командная работа** - Совместное редактирование в реальном времени
- 📊 **Аналитика** - Детальные отчеты и метрики производительности
- 🔐 **Безопасность** - Enterprise-уровень с JWT аутентификацией
- 🏗️ **Микросервисы** - Масштабируемая архитектура
- 📱 **Responsive** - Адаптивный дизайн для всех устройств

## 🛠 Технологии

| Компонент | Технология | Версия |
|-----------|------------|--------|
| Frontend | Angular | 17+ |
| Backend | Spring Boot | 3.x |
| Database | PostgreSQL | 15+ |
| Cache | Redis | 7+ |
| Message Queue | Kafka | 3+ |
| Container | Docker | 24+ |
| Orchestration | Kubernetes | 1.28+ |

## 📁 Структура проекта

```
├── client/                    # Angular Frontend
├── server/                    # Spring Boot Backend (Legacy)
├── microservices/            # Микросервисная архитектура
│   ├── services/             # Микросервисы
│   ├── shared/               # Общие компоненты
│   └── infrastructure/       # Docker, K8s, мониторинг
└── docs/                     # Документация
```

## 🔧 Конфигурация

### Переменные окружения
```bash
# Загрузка конфигурации
source microservices/shared/config/load-env.sh development

# Основные переменные
export SPRING_PROFILES_ACTIVE=development
export DATABASE_URL=postgresql://localhost:5432/taskboard
export JWT_SECRET=your-secret-key
```

### Docker Compose
```bash
# Разработка
./run-with-env.sh development up -d

# Продакшен
./run-with-env.sh production up -d
```

## 🧪 Тестирование

```bash
# Backend тесты
cd server && mvn test

# Frontend тесты
cd client && npm test

# E2E тесты
cd client && npm run e2e

# Интеграционные тесты
mvn test -Dtest="*IntegrationTest"
```

## 🚀 Развертывание

### Docker
```bash
# Сборка и запуск
docker-compose up -d --build

# Проверка статуса
docker-compose ps
```

### Kubernetes
```bash
# Применение манифестов
kubectl apply -f microservices/infrastructure/kubernetes/

# Проверка статуса
kubectl get pods -n taskboard
```

## 📚 API Документация

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

### Основные endpoints:
- `GET /api/tasks` - Список задач
- `POST /api/tasks` - Создание задачи
- `PUT /api/tasks/{id}` - Обновление задачи
- `DELETE /api/tasks/{id}` - Удаление задачи

## 🤝 Вклад в проект

1. Форкните репозиторий
2. Создайте feature ветку (`git checkout -b feature/amazing-feature`)
3. Зафиксируйте изменения (`git commit -m 'Add amazing feature'`)
4. Отправьте в ветку (`git push origin feature/amazing-feature`)
5. Создайте Pull Request

### Стандарты кода
- Следуйте правилам в `.cursor/rules/`
- Покрывайте код тестами (минимум 80%)
- Обновляйте документацию
- Используйте conventional commits

## 📄 Лицензия

MIT License - см. [LICENSE](LICENSE) для деталей.

## 🆘 Поддержка

- 📖 [Документация](docs/)
- 🐛 [Issues](https://github.com/your-org/task-management/issues)
- 💬 [Discussions](https://github.com/your-org/task-management/discussions)
- 📧 Email: support@example.com

---

*Версия: 1.0.0 | Последнее обновление: 2024-12-19*