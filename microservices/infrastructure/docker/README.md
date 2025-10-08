# Docker Infrastructure Setup

## Быстрый старт

### 1. Настройка переменных окружения

```bash
# Скопировать шаблон конфигурации
cp env.template .env

# Отредактировать .env файл с вашими настройками
nano .env
```

### 2. Запуск инфраструктуры

```bash
# Запустить все сервисы
docker-compose up -d

# Проверить статус
docker-compose ps

# Просмотр логов
docker-compose logs -f [service-name]
```

### 3. Остановка

```bash
# Остановить все сервисы
docker-compose down

# Остановить с удалением volumes
docker-compose down -v
```

## Доступные сервисы

| Сервис | URL | Описание |
|--------|-----|----------|
| **Kafka UI** | http://localhost:8080 | Управление Kafka |
| **Prometheus** | http://localhost:9090 | Метрики |
| **Grafana** | http://localhost:3000 | Визуализация (admin/admin) |
| **Jaeger** | http://localhost:16686 | Трассировка |
| **Kong Gateway** | http://localhost:8000 | API Gateway |
| **User Service** | http://localhost:8081 | API пользователей |
| **Task Service** | http://localhost:8082 | API задач |
| **Project Service** | http://localhost:8083 | API проектов |

## Конфигурация

### Переменные окружения

Все настройки вынесены в переменные окружения в файле `.env`:

- **DATABASE_*** - настройки PostgreSQL
- **REDIS_*** - настройки Redis
- **KAFKA_*** - настройки Kafka
- **SERVICE_*** - URL микросервисов
- **EXTERNAL_*** - внешние сервисы
- **ACTUATOR_*** - настройки мониторинга
- **LOG_*** - настройки логирования

### Безопасность

⚠️ **ВАЖНО**: 
- Файл `.env` НЕ должен попадать в Git
- Все файлы с "env" в названии игнорируются Git
- Используйте `env.template` как основу для создания `.env`
- В продакшене используйте внешние системы управления секретами

## Troubleshooting

### Проблемы с портами

Если порты заняты, измените их в `.env` файле:

```bash
# Изменить порты в .env
SERVER_PORT=8081
KAFKA_UI_PORT=8081
```

### Проблемы с базой данных

```bash
# Проверить статус PostgreSQL
docker-compose logs postgres

# Пересоздать базу данных
docker-compose down -v
docker-compose up -d postgres
```

### Проблемы с Kafka

```bash
# Проверить статус Kafka
docker-compose logs kafka

# Пересоздать Kafka
docker-compose down -v
docker-compose up -d kafka
```

## Мониторинг

### Health Checks

```bash
# Проверить здоровье всех сервисов
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

### Метрики

```bash
# Prometheus метрики
curl http://localhost:9090/api/v1/targets

# Grafana дашборды
open http://localhost:3000
```

## Разработка

### Добавление нового сервиса

1. Создать сервис с помощью скрипта:
   ```bash
   cd microservices
   ./scripts/create-service.sh new-service "New Service" "Description"
   ```

2. Добавить в `docker-compose.yml`:
   ```yaml
   new-service:
     build:
       context: ../../services/new-service
       dockerfile: Dockerfile
     container_name: taskboard-new-service
     ports:
       - "8084:8080"
     environment:
       SERVICE_NAME: new-service
       # ... остальные переменные
   ```

3. Перезапустить:
   ```bash
   docker-compose up -d new-service
   ```
