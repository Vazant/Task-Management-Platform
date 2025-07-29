# TaskBoard Pro Backend API

Spring Boot backend для TaskBoard Pro - платформы управления проектами.

## 📚 Документация

- [API Documentation](API-README.md) - Подробная документация по REST API
- [OpenAPI Specification](src/main/resources/openapi.yaml) - OpenAPI 3.0 спецификация
- [Fix Log](fix-log.md) - Журнал исправлений и улучшений кода

## 🚀 Быстрый старт

### Требования
- Java 17+
- Maven 3.6+

### Запуск

1. **Перейдите в папку backend**
```bash
cd src/backend
```

2. **Запустите приложение**
```bash
mvn spring-boot:run
```

3. **Проверьте работу**
- API доступен по адресу: `http://localhost:3000/api`
- H2 консоль: `http://localhost:3000/api/h2-console`

## 📋 API Endpoints

### Аутентификация

#### Регистрация
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "confirmPassword": "password123"
}
```

#### Вход
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123"
}
```

#### Обновление токена
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "your-refresh-token"
}
```

#### Восстановление пароля
```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "test@example.com"
}
```

#### Сброс пароля
```http
POST /api/auth/reset-password
Content-Type: application/json

{
  "token": "reset-token",
  "password": "newpassword123"
}
```

## 🗄️ База данных

- **H2** (в памяти) для разработки
- Автоматическое создание таблиц
- Консоль доступна по адресу: `http://localhost:3000/api/h2-console`

### Настройки подключения к H2:
- JDBC URL: `jdbc:h2:mem:taskboarddb`
- Username: `sa`
- Password: `password`

## 🔧 Конфигурация

Основные настройки в `application.properties`:

```properties
# Порт сервера
server.port=3000

# Контекст API
server.servlet.context-path=/api

# JWT настройки
jwt.secret=your-secret-key-here
jwt.expiration=86400000
jwt.refresh-expiration=604800000

# CORS
spring.web.cors.allowed-origins=http://localhost:4200
```

## 🏗️ Архитектура

```
src/main/java/com/taskboard/api/
├── config/          # Конфигурации
├── controller/      # REST контроллеры
├── dto/            # Data Transfer Objects
├── model/          # JPA сущности
├── repository/     # Репозитории
└── service/        # Бизнес-логика
```

## 🔐 Безопасность

- **JWT** токены для аутентификации
- **BCrypt** для хеширования паролей
- **CORS** настроен для Angular frontend
- **Spring Security** для защиты endpoints

## 🧪 Тестирование

```bash
# Запуск тестов
mvn test

# Запуск с coverage
mvn jacoco:report
```

## 📦 Сборка

```bash
# Создание JAR файла
mvn clean package

# Запуск JAR
java -jar target/taskboard-api-0.0.1-SNAPSHOT.jar
```

## 🔗 Интеграция с Frontend

Backend настроен для работы с Angular frontend:
- CORS разрешен для `http://localhost:4200`
- API endpoints соответствуют ожиданиям frontend
- JWT токены для аутентификации

## 🚨 Troubleshooting

### Проблемы с портом
Если порт 3000 занят, измените в `application.properties`:
```properties
server.port=3001
```

### Проблемы с CORS
Проверьте настройки CORS в `SecurityConfig.java`

### Проблемы с JWT
Убедитесь, что JWT secret достаточно длинный (минимум 256 бит) 
