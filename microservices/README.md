# Task Management Platform - Микросервисная архитектура

## 🎯 Обзор

Task Management Platform - это современная система управления задачами и проектами, построенная на микросервисной архитектуре с использованием гексагональных принципов.

## 🏗️ Архитектура

### Микросервисы
- **User Service** - управление пользователями и аутентификация
- **Task Service** - управление задачами
- **Project Service** - управление проектами
- **Time Service** - учет времени
- **Analytics Service** - аналитика и отчеты
- **Notification Service** - уведомления
- **Search Service** - поиск
- **File Service** - управление файлами
- **Gateway Service** - API Gateway

### Технологический стек
- **Backend**: Java 21, Spring Boot 3.5.6, Spring Cloud
- **Database**: PostgreSQL, Redis
- **Messaging**: Apache Kafka
- **Infrastructure**: Docker, Kubernetes
- **Monitoring**: Prometheus, Grafana, Jaeger
- **Logging**: ELK Stack

## 🚀 Быстрый старт

### Предварительные требования
- Java 21+
- Maven 3.9+
- Docker & Docker Compose
- Node.js 18+ (для frontend)

### Установка
```bash
# Клонирование репозитория
git clone https://github.com/your-org/task-management-platform.git
cd task-management-platform/microservices

# Копирование переменных окружения
cp infrastructure/docker/env.template .env

# Запуск инфраструктуры
docker-compose -f infrastructure/docker/docker-compose.yml up -d

# Сборка всех сервисов
./scripts/build.sh

# Запуск сервисов
./scripts/deploy.sh local
```

### Проверка работы
```bash
# Проверка health checks
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Task Service
curl http://localhost:8083/actuator/health  # Project Service

# Проверка метрик
curl http://localhost:9090  # Prometheus
curl http://localhost:3000  # Grafana (admin/admin)
curl http://localhost:16686 # Jaeger
```

## 📁 Структура проекта

```
microservices/
├── docs/                          # Документация
├── infrastructure/                # Инфраструктура
│   ├── docker/                   # Docker конфигурации
│   ├── kubernetes/               # Kubernetes манифесты
│   └── monitoring/               # Мониторинг
├── shared/                       # Общие компоненты
│   ├── common/                   # Общие библиотеки
│   ├── config/                   # Конфигурации
│   └── events/                   # События
├── services/                     # Микросервисы
└── scripts/                      # Скрипты автоматизации
```

## 🔧 Разработка

### Создание нового сервиса
```bash
./scripts/create-service.sh service-name "Service Name" "Description"
```

### Структура сервиса
```
service-name/
├── src/main/java/com/taskboard/service/
│   ├── domain/              # Доменный слой
│   ├── application/         # Слой приложения
│   ├── infrastructure/      # Инфраструктурный слой
│   └── interfaces/          # Слой интерфейсов
├── src/test/java/           # Тесты
├── Dockerfile               # Docker образ
├── pom.xml                  # Maven конфигурация
└── README.md                # Документация сервиса
```

### Запуск в режиме разработки
```bash
# Запуск конкретного сервиса
cd services/user-service
mvn spring-boot:run

# Запуск с профилем
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## 🧪 Тестирование

### Типы тестов
- **Unit тесты** - тестирование отдельных компонентов
- **Integration тесты** - тестирование взаимодействия компонентов
- **E2E тесты** - тестирование полных пользовательских сценариев

### Запуск тестов
```bash
# Unit тесты
./scripts/test.sh service-name unit

# Integration тесты
./scripts/test.sh service-name integration

# Все тесты
./scripts/test.sh service-name all

# Покрытие кода
./scripts/test.sh service-name coverage
```

## 📦 Развертывание

### Docker
```bash
# Сборка и запуск
./scripts/deploy.sh docker

# Запуск конкретного сервиса
./scripts/deploy.sh docker user-service
```

### Kubernetes
```bash
# Развертывание в K8s
./scripts/deploy.sh kubernetes

# Проверка статуса
kubectl get pods -n taskboard
kubectl logs -f deployment/user-service -n taskboard
```

### Production
```bash
# Развертывание в продакшн
kubectl apply -f infrastructure/kubernetes/
kubectl rollout status deployment/user-service -n taskboard
```

## 📊 Мониторинг

### Метрики
- **Prometheus** - сбор метрик (http://localhost:9090)
- **Grafana** - визуализация (http://localhost:3000)
- **Jaeger** - трассировка (http://localhost:16686)

### Логирование
- **ELK Stack** - централизованное логирование
- **Structured Logging** - структурированные логи
- **Log Aggregation** - агрегация логов

### Health Checks
```bash
# Проверка состояния сервисов
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

## 🔒 Безопасность

### Аутентификация
- JWT токены для stateless аутентификации
- BCrypt для хеширования паролей
- OAuth2 для внешних провайдеров

### Авторизация
- RBAC (Role-Based Access Control)
- @PreAuthorize для проверки прав
- Принцип минимальных привилегий

### Защита
- HTTPS для всех соединений
- Валидация всех входных данных
- CORS настройки
- Rate limiting

## 🚀 CI/CD

### GitHub Actions
- Автоматическая сборка и тестирование
- Развертывание в staging и production
- Уведомления в Slack

### Pipeline
1. **Build** - сборка и тестирование
2. **Test** - unit, integration, E2E тесты
3. **Deploy** - развертывание в staging
4. **Smoke Tests** - проверка работоспособности
5. **Production** - развертывание в продакшн

## 📚 Документация

### API Documentation
- **Swagger/OpenAPI** - документация API
- **Postman Collections** - коллекции для тестирования
- **API Versioning** - версионирование API

### Документация
- [Руководство по разработке](docs/DEVELOPMENT.md)
- [Архитектура проекта](PROJECT_STRUCTURE.md)
- [API документация](docs/API_DOCUMENTATION.md)

## 🤝 Участие в разработке

### Процесс
1. Fork репозитория
2. Создание feature branch
3. Разработка и тестирование
4. Создание Pull Request
5. Code review
6. Merge в main

### Стандарты
- Conventional Commits
- Code review (минимум 2 одобрения)
- Покрытие тестами > 80%
- Соответствие Checkstyle

## 📞 Поддержка

### Команда
- **Lead Developer** - архитектура и техническое руководство
- **Backend Team** - разработка микросервисов
- **DevOps Team** - инфраструктура и развертывание
- **QA Team** - тестирование и качество

### Ресурсы
- **Repository**: [GitHub](https://github.com/your-org/task-management-platform)
- **Documentation**: [Wiki](https://github.com/your-org/task-management-platform/wiki)
- **Issues**: [GitHub Issues](https://github.com/your-org/task-management-platform/issues)
- **CI/CD**: [GitHub Actions](https://github.com/your-org/task-management-platform/actions)

## 📄 Лицензия

Этот проект лицензирован под MIT License - см. файл [LICENSE](LICENSE) для деталей.

---

*Создано с ❤️ командой разработки Task Management Platform*