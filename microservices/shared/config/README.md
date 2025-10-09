# Environment Configuration

Конфигурация для различных сред развертывания микросервисов.

## 🚀 Быстрый старт

### Загрузка конфигурации
```bash
# Разработка
source load-env.sh development

# Docker
source load-env.sh docker

# Продакшен
source load-env.sh production
```

### Запуск с Docker
```bash
cd ../../infrastructure/docker
./run-with-env.sh development up -d
```

## 📁 Файлы конфигурации

| Файл | Назначение |
|------|------------|
| `.env.example` | Шаблон переменных |
| `.env.development` | Конфигурация разработки |
| `.env.docker` | Конфигурация Docker |
| `.env.production` | Конфигурация продакшена |
| `load-env.sh` | Скрипт загрузки |

## 🔧 Основные переменные

### Приложение
```bash
SPRING_PROFILES_ACTIVE=development
SERVICE_NAME=task-service
SERVER_PORT=8080
```

### База данных
```bash
DATABASE_URL=postgresql://localhost:5432/taskboard
DATABASE_USERNAME=taskboard_user
DATABASE_PASSWORD=secure_password
```

### Безопасность
```bash
JWT_SECRET=your-super-secret-jwt-key
JWT_EXPIRATION=3600
```

### Логирование
```bash
LOG_LEVEL_ROOT=INFO
LOG_PATTERN_CONSOLE="%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

## 🐳 Docker

### Запуск
```bash
# Разработка
./run-with-env.sh development up -d

# Продакшен
./run-with-env.sh production up -d

# Остановка
./run-with-env.sh development down
```

### Проверка статуса
```bash
docker-compose ps
docker-compose logs -f service-name
```

## 🔒 Безопасность

- ✅ Секреты в переменных окружения
- ✅ Разные конфигурации для сред
- ✅ `.env` файлы в `.gitignore`
- ✅ Шаблон без секретов

## 📚 Дополнительно

- [Docker Compose документация](https://docs.docker.com/compose/)
- [Spring Boot конфигурация](https://spring.io/guides/gs/spring-boot/)
- [12-Factor App](https://12factor.net/)