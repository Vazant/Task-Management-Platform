# Environment Configuration

Этот каталог содержит конфигурационные файлы для различных сред развертывания.

## Структура файлов

```
config/
├── .env.example          # Шаблон конфигурации
├── .env.development      # Конфигурация для разработки
├── .env.docker          # Конфигурация для Docker
├── .env.production      # Конфигурация для продакшена
├── load-env.sh         # Скрипт загрузки переменных окружения
└── README.md           # Этот файл
```

## Использование

### 1. Локальная разработка

```bash
# Загрузить переменные окружения для разработки
source load-env.sh development

# Или вручную
export $(grep -v '^#' .env.development | grep -v '^$' | xargs)
```

### 2. Docker

```bash
# Запуск с Docker конфигурацией
cd ../../infrastructure/docker
./run-with-env.sh docker up -d

# Или вручную
docker-compose --env-file ../../shared/config/.env.docker -f docker-compose.env.yml up -d
```

### 3. Продакшен

```bash
# Запуск с продакшен конфигурацией
cd ../../infrastructure/docker
./run-with-env.sh production up -d
```

## Переменные окружения

### Основные настройки

| Переменная | Описание | Пример |
|------------|----------|---------|
| `SERVICE_NAME` | Имя сервиса | `user-service` |
| `SPRING_PROFILES_ACTIVE` | Активный профиль Spring | `development` |
| `SERVER_PORT` | Порт сервера | `8080` |
| `SERVER_HOST` | Хост сервера | `localhost` |

### База данных

| Переменная | Описание | Пример |
|------------|----------|---------|
| `DATABASE_URL` | URL базы данных | `jdbc:postgresql://localhost:5432/taskboard` |
| `DATABASE_USERNAME` | Имя пользователя БД | `taskboard_user` |
| `DATABASE_PASSWORD` | Пароль БД | `secure_password` |
| `JPA_DDL_AUTO` | Режим DDL | `update` |

### Redis

| Переменная | Описание | Пример |
|------------|----------|---------|
| `REDIS_HOST` | Хост Redis | `localhost` |
| `REDIS_PORT` | Порт Redis | `6379` |
| `REDIS_PASSWORD` | Пароль Redis | `redis_password` |

### Kafka

| Переменная | Описание | Пример |
|------------|----------|---------|
| `KAFKA_BOOTSTRAP_SERVERS` | Серверы Kafka | `localhost:9092` |
| `KAFKA_CONSUMER_GROUP` | Группа потребителей | `taskboard-group` |
| `KAFKA_AUTO_OFFSET_RESET` | Сброс offset | `earliest` |

### Безопасность

| Переменная | Описание | Пример |
|------------|----------|---------|
| `JWT_SECRET` | Секретный ключ JWT | `your-super-secret-key` |
| `JWT_EXPIRATION` | Время жизни токена | `3600` |
| `CORS_ALLOWED_ORIGINS` | Разрешенные CORS origins | `http://localhost:4200` |

## Создание собственной конфигурации

1. Скопируйте `.env.example` в новый файл:
```bash
cp .env.example .env.my-environment
```

2. Отредактируйте значения под ваши нужды

3. Используйте в скриптах:
```bash
source load-env.sh my-environment
```

## Безопасность

⚠️ **Важно**: Никогда не коммитьте файлы с реальными паролями и секретами!

- Используйте `.env.example` как шаблон
- Добавьте `.env*` в `.gitignore` (кроме .env.example)
- Используйте переменные окружения для секретов в продакшене
- Регулярно ротируйте секретные ключи

## Примеры использования

### Spring Boot приложение

```java
@Value("${DATABASE_URL}")
private String databaseUrl;

@Value("${JWT_SECRET}")
private String jwtSecret;
```

### Docker Compose

```yaml
services:
  app:
    environment:
      DATABASE_URL: ${DATABASE_URL}
      JWT_SECRET: ${JWT_SECRET}
```

### Kubernetes

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  DATABASE_URL: "jdbc:postgresql://postgres:5432/taskboard"
---
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
data:
  JWT_SECRET: <base64-encoded-secret>
```
