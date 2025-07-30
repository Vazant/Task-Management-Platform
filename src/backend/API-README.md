# Taskboard API Documentation

## Обзор

Taskboard API - это RESTful веб-сервис для управления задачами и проектами. API построен на Spring Boot 3.5.4 и использует JWT для аутентификации.

## Версия API

Текущая версия: 1.0.0

## Базовый URL

- Development: `http://localhost:3000/api`
- Production: `https://api.taskboard.com`

## Аутентификация

API использует JWT (JSON Web Tokens) для аутентификации. После успешного входа вы получите:
- `token` - токен доступа (срок действия 24 часа)
- `refreshToken` - токен обновления (срок действия 7 дней)

Включите токен доступа в заголовок Authorization для защищенных endpoints:
```
Authorization: Bearer <your-token>
```

## Формат ответов

Все ответы API следуют единому формату:

```json
{
  "data": {}, // Данные ответа (может быть null)
  "message": "Сообщение об операции",
  "success": true/false,
  "errors": [] // Массив ошибок (опционально)
}
```

## Основные Endpoints

### Аутентификация

#### POST /auth/login
Вход в систему

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "data": {
    "user": {
      "id": 1,
      "username": "johndoe",
      "email": "user@example.com",
      "role": "USER"
    },
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "message": "Вход выполнен успешно",
  "success": true
}
```

#### POST /auth/register
Регистрация нового пользователя

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "confirmPassword": "password123"
}
```

#### POST /auth/refresh
Обновление токена доступа

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Профиль пользователя

#### GET /api/profile
Получить профиль текущего пользователя (требуется аутентификация)

**Response:**
```json
{
  "data": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "avatar": "/uploads/avatars/123.jpg",
    "role": "USER",
    "lastLogin": "2025-07-29T10:00:00",
    "createdAt": "2025-07-01T10:00:00",
    "updatedAt": "2025-07-29T10:00:00"
  },
  "message": "Профиль успешно загружен",
  "success": true
}
```

#### PUT /api/profile
Обновить профиль пользователя (требуется аутентификация)

**Request Body:**
```json
{
  "username": "newusername",
  "email": "newemail@example.com",
  "firstName": "John",
  "lastName": "Smith"
}
```

#### POST /api/profile/avatar
Загрузить аватар (требуется аутентификация)

**Request:** multipart/form-data
- `avatar`: файл изображения (JPEG, PNG, GIF, WebP)
- Максимальный размер: 5 MB

#### DELETE /api/profile/avatar
Удалить аватар (требуется аутентификация)

#### POST /api/profile/change-password
Изменить пароль (требуется аутентификация)

**Request Body:**
```json
{
  "currentPassword": "oldpassword",
  "newPassword": "newpassword123"
}
```

## Коды состояния HTTP

- `200 OK` - Успешная операция
- `400 Bad Request` - Неверный запрос или ошибка валидации
- `401 Unauthorized` - Требуется аутентификация
- `403 Forbidden` - Доступ запрещен
- `404 Not Found` - Ресурс не найден
- `500 Internal Server Error` - Внутренняя ошибка сервера

## Валидация

### Пользователь
- **Username**: 3-20 символов
- **Email**: должен быть валидным email адресом
- **Password**: минимум 8 символов

### Файлы
- **Avatar**: максимум 5 MB, только изображения (JPEG, PNG, GIF, WebP)

## Ограничения

- Максимальный размер запроса: 10 MB
- Лимит запросов: 100 запросов в минуту на IP адрес

## OpenAPI Спецификация

Полная OpenAPI 3.0 спецификация доступна в файле `/src/main/resources/openapi.yaml`

## Примеры использования

### cURL

```bash
# Вход
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'

# Получить профиль
curl -X GET http://localhost:3000/api/api/profile \
  -H "Authorization: Bearer <your-token>"

# Загрузить аватар
curl -X POST http://localhost:3000/api/api/profile/avatar \
  -H "Authorization: Bearer <your-token>" \
  -F "avatar=@/path/to/image.jpg"
```

### JavaScript (Fetch API)

```javascript
// Вход
const login = async (email, password) => {
  const response = await fetch('http://localhost:3000/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ email, password })
  });
  return response.json();
};

// Получить профиль
const getProfile = async (token) => {
  const response = await fetch('http://localhost:3000/api/api/profile', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.json();
};
```

## Поддержка

Для вопросов и поддержки обращайтесь: support@taskboard.com
