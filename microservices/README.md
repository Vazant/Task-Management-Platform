# Task Management Platform - Microservices

Система управления задачами и проектами, построенная на микросервисной архитектуре с использованием Spring Boot, Angular, PostgreSQL, Redis и Apache Kafka.

## 🚀 Быстрый запуск

### Предварительные требования

- Docker Desktop
- Java 21+
- Maven 3.9+

### Запуск с Docker

```bash
# Перейти в папку с Docker конфигурацией
cd microservices/infrastructure/docker

# Скопировать шаблон конфигурации
cp env.template .env

# Запустить все сервисы
docker-compose up -d

# Проверить статус
docker-compose ps
```

### Доступные сервисы

| Сервис | URL | Описание |
|--------|-----|----------|
| **Kafka UI** | http://localhost:8080 | Управление Kafka |
| **Grafana** | http://localhost:3000 | Мониторинг (admin/admin) |
| **Prometheus** | http://localhost:9090 | Метрики |
| **Jaeger** | http://localhost:16686 | Трассировка |
| **User Service** | http://localhost:8081 | API пользователей |
| **Task Service** | http://localhost:8082 | API задач |
| **Project Service** | http://localhost:8083 | API проектов |

### Остановка

```bash
docker-compose down
```

## 📚 Документация

- **[Архитектура](docs_private/)** - Детальная архитектурная документация
- **[Docker Setup](infrastructure/docker/README.md)** - Подробная настройка Docker
- **[Kubernetes](infrastructure/kubernetes/)** - Развертывание в Kubernetes
- **[Разработка](docs/DEVELOPMENT.md)** - Руководство разработчика
- **[API](docs_private/api-specs/)** - Спецификации API микросервисов

## 🛠️ Разработка

### Создание нового микросервиса

```bash
cd microservices
./scripts/create-service.sh service-name "Service Name" "Description"
```

### Структура проекта

```
microservices/
├── infrastructure/    # Docker, Kubernetes, мониторинг
├── services/         # Микросервисы
├── shared/          # Общие компоненты
└── scripts/         # Утилиты разработки
```

## 📞 Поддержка

- **Issues**: GitHub Issues
- **Документация**: `docs_private/` папка
- **Конфигурация**: `infrastructure/docker/env.template`