# Task Management Platform - Microservices Architecture

## 🏗️ Структура проекта

```
microservices/
├── infrastructure/           # Инфраструктурные компоненты
│   ├── docker/              # Docker конфигурация
│   ├── kubernetes/          # Kubernetes манифесты
│   ├── kafka/               # Kafka конфигурация
│   └── monitoring/          # Мониторинг и логирование
├── services/                # Микросервисы
│   ├── user-service/        # Сервис пользователей
│   ├── task-service/        # Сервис задач
│   ├── project-service/     # Сервис проектов
│   ├── time-service/        # Сервис учета времени
│   ├── analytics-service/   # Сервис аналитики
│   ├── notification-service/# Сервис уведомлений
│   ├── search-service/      # Сервис поиска
│   ├── file-service/        # Сервис файлов
│   └── gateway-service/     # API Gateway
└── shared/                  # Общие компоненты
    ├── common/              # Общие утилиты
    ├── config/              # Конфигурация
    └── database/            # База данных
```

## 🚀 Быстрый старт

### Предварительные требования

- Docker Desktop
- Docker Compose
- Java 17+
- Maven 3.9+
- kubectl (для Kubernetes)
- minikube (для локального Kubernetes)

### 1. Запуск с Docker Compose

```bash
# Перейти в папку с Docker конфигурацией
cd microservices/infrastructure/docker

# Запустить все сервисы
docker-compose up -d

# Проверить статус сервисов
docker-compose ps

# Просмотр логов
docker-compose logs -f [service-name]
```

### 2. Запуск с Kubernetes

```bash
# Запустить minikube
minikube start

# Применить манифесты
kubectl apply -f microservices/infrastructure/kubernetes/

# Проверить статус подов
kubectl get pods -n taskboard-microservices

# Получить доступ к сервисам
minikube service [service-name] -n taskboard-microservices
```

## 📊 Мониторинг и наблюдение

### Доступные сервисы

| Сервис | URL | Описание |
|--------|-----|----------|
| **API Gateway** | http://localhost:8000 | Kong API Gateway |
| **Kafka UI** | http://localhost:8080 | Управление Kafka |
| **Prometheus** | http://localhost:9090 | Метрики |
| **Grafana** | http://localhost:3000 | Визуализация (admin/admin) |
| **Jaeger** | http://localhost:16686 | Трассировка |
| **User Service** | http://localhost:8081 | API пользователей |
| **Task Service** | http://localhost:8082 | API задач |
| **Project Service** | http://localhost:8083 | API проектов |

### Health Checks

Все сервисы имеют health checks:
- **Docker**: `docker-compose ps`
- **Kubernetes**: `kubectl get pods -n taskboard-microservices`
- **HTTP**: `curl http://localhost:8081/actuator/health`

## 🔧 Разработка

### Создание нового микросервиса

1. **Создать папку сервиса**:
   ```bash
   mkdir microservices/services/new-service
   ```

2. **Скопировать базовую структуру**:
   ```bash
   cp -r microservices/services/user-service/* microservices/services/new-service/
   ```

3. **Обновить конфигурацию**:
   - Изменить `pom.xml`
   - Обновить `Dockerfile`
   - Настроить порты в `docker-compose.yml`

4. **Добавить в Kubernetes**:
   - Создать манифесты в `kubernetes/`
   - Обновить `configmap.yaml`

### Kafka Topics

Основные топики для взаимодействия сервисов:

- `user.events` - События пользователей
- `task.events` - События задач
- `project.events` - События проектов
- `time.events` - События учета времени
- `notification.events` - События уведомлений
- `analytics.events` - События аналитики

### База данных

- **PostgreSQL**: Основная база данных
- **Redis**: Кэширование и сессии
- **Kafka**: Асинхронная обработка сообщений

## 🧪 Тестирование

### Unit тесты
```bash
cd microservices/services/user-service
mvn test
```

### Integration тесты
```bash
# Запустить тестовую среду
docker-compose -f docker-compose.test.yml up -d

# Запустить тесты
mvn verify

# Остановить тестовую среду
docker-compose -f docker-compose.test.yml down
```

### E2E тесты
```bash
# Запустить полную среду
docker-compose up -d

# Запустить E2E тесты
npm run e2e:microservices
```

## 📝 Логирование

### Структурированные логи
Все сервисы используют JSON логирование:
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "level": "INFO",
  "service": "user-service",
  "traceId": "abc123",
  "message": "User created successfully",
  "userId": "123"
}
```

### Централизованное логирование
- **ELK Stack** (планируется)
- **Fluentd** для сбора логов
- **Elasticsearch** для хранения
- **Kibana** для визуализации

## 🔒 Безопасность

### Аутентификация
- **JWT токены** для API
- **OAuth2** для внешних интеграций
- **WebAuthn** для двухфакторной аутентификации

### Авторизация
- **RBAC** (Role-Based Access Control)
- **ABAC** (Attribute-Based Access Control)
- **Service-to-Service** аутентификация

### Сетевая безопасность
- **mTLS** между сервисами
- **Network Policies** в Kubernetes
- **Service Mesh** (планируется)

## 🚀 Развертывание

### Development
```bash
docker-compose up -d
```

### Staging
```bash
kubectl apply -f microservices/infrastructure/kubernetes/
```

### Production
- **Helm Charts** (планируется)
- **GitOps** с ArgoCD
- **CI/CD** с GitHub Actions

## 📚 Документация

- **API Documentation**: Swagger UI для каждого сервиса
- **Architecture**: `docs_private/` папка
- **Runbooks**: Операционные процедуры
- **Troubleshooting**: Руководство по устранению неполадок

## 🤝 Участие в разработке

1. **Fork** репозитория
2. **Создать feature branch**
3. **Разработать** микросервис
4. **Добавить тесты**
5. **Создать Pull Request**

## 📞 Поддержка

- **Issues**: GitHub Issues
- **Discussions**: GitHub Discussions
- **Documentation**: Wiki
- **Chat**: Discord (планируется)
