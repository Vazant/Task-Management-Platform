# Руководство по интеграции Task Management Platform

## Обзор

Данное руководство описывает процесс интеграции Angular frontend с Spring Boot backend API, настройку тестирования и развертывания полной системы управления задачами.

## Архитектура системы

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (Angular)     │◄──►│   (Spring Boot) │◄──►│   (PostgreSQL)  │
│   Port: 4200    │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   E2E Tests     │    │   Load Tests    │    │   Monitoring    │
│   (Cypress)     │    │   (JMeter)      │    │   (Prometheus)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Быстрый старт

### 1. Клонирование и настройка

```bash
# Клонирование репозитория
git clone https://github.com/your-org/task-management-platform.git
cd task-management-platform

# Установка зависимостей
npm install
cd server && ./mvnw clean install && cd ..
```

### 2. Настройка базы данных

#### Development (H2)
```properties
# application.properties
spring.datasource.url=jdbc:h2:mem:taskboarddb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
```

#### Production (PostgreSQL)
```properties
# application-prod.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskboarddb
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=taskboard
spring.datasource.password=your_password
```

### 3. Запуск системы

#### Backend
```bash
cd server
./mvnw spring-boot:run
```

#### Frontend
```bash
cd client
npm start
```

#### Полная система (Docker)
```bash
docker-compose up -d
```

## Конфигурация

### Environment переменные

#### Frontend (environment.ts)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  wsUrl: 'ws://localhost:8080/ws',
  enableServiceWorker: false,
  enableAnalytics: false,
  logLevel: 'debug'
};
```

#### Backend (application.properties)
```properties
# Server Configuration
server.port=8080

# CORS Configuration
cors.allowed-origins=http://localhost:4200
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed-headers=*
cors.allow-credentials=true

# JWT Configuration
jwt.secret=your-secret-key-here-make-it-very-long-and-secure-in-production
jwt.expiration=900000
jwt.refresh-expiration=604800000
```

### API Endpoints

#### Аутентификация
```
POST /api/auth/login          # Вход в систему
POST /api/auth/register       # Регистрация
POST /api/auth/refresh        # Обновление токена
POST /api/auth/logout         # Выход из системы
```

#### Задачи
```
GET    /api/tasks             # Получить список задач
POST   /api/tasks             # Создать задачу
GET    /api/tasks/{id}        # Получить задачу по ID
PUT    /api/tasks/{id}        # Обновить задачу
DELETE /api/tasks/{id}        # Удалить задачу
```

#### Проекты
```
GET    /api/projects          # Получить список проектов
POST   /api/projects          # Создать проект
GET    /api/projects/{id}     # Получить проект по ID
PUT    /api/projects/{id}     # Обновить проект
DELETE /api/projects/{id}     # Удалить проект
```

## Тестирование

### 1. Unit тесты

#### Backend (Java/Spring Boot)
```bash
cd server
./mvnw test
```

#### Frontend (Angular/TypeScript)
```bash
cd client
npm test
```

### 2. Integration тесты

#### Backend
```bash
cd server
./mvnw test -Dtest="**/*IntegrationTest"
```

#### Frontend
```bash
cd client
npm run test:integration
```

### 3. E2E тесты

```bash
cd client
npx cypress open    # Интерактивный режим
npx cypress run     # Headless режим
```

### 4. Нагрузочное тестирование

```bash
# Установка JMeter
brew install jmeter  # macOS
# или скачайте с https://jmeter.apache.org/

# Запуск нагрузочных тестов
./scripts/run-load-tests.sh
```

### 5. Комплексное тестирование

```bash
# Запуск всех тестов
./scripts/run-all-tests.sh
```

## Развертывание

### Development

#### Локальная разработка
```bash
# Backend
cd server
./mvnw spring-boot:run

# Frontend (в другом терминале)
cd client
npm start
```

#### Docker Compose
```bash
docker-compose up -d
```

### Production

#### Backend (Spring Boot)
```bash
# Сборка
cd server
./mvnw clean package -Pprod

# Запуск
java -jar target/task-management-platform-1.0.0.jar
```

#### Frontend (Angular)
```bash
# Сборка
cd client
npm run build --prod

# Развертывание (Nginx)
sudo cp -r dist/* /var/www/html/
```

#### Docker Production
```bash
# Сборка образов
docker build -t task-management-backend ./server
docker build -t task-management-frontend ./client

# Запуск
docker-compose -f docker-compose.prod.yml up -d
```

## Мониторинг

### 1. Health Checks

#### Backend Health
```bash
curl http://localhost:8080/actuator/health
```

#### Frontend Health
```bash
curl http://localhost:4200
```

### 2. Metrics

#### Prometheus Metrics
```bash
curl http://localhost:8080/actuator/prometheus
```

#### Application Metrics
- Response time
- Throughput
- Error rate
- Memory usage
- CPU usage

### 3. Logging

#### Backend Logs
```bash
tail -f server/logs/application.log
```

#### Frontend Logs
```bash
# В браузере (Developer Tools)
console.log('Application logs');
```

## Troubleshooting

### Частые проблемы

#### 1. CORS ошибки
```
Access to XMLHttpRequest at 'http://localhost:8080/api/tasks' from origin 'http://localhost:4200' has been blocked by CORS policy
```

**Решение**: Проверьте настройки CORS в backend:
```properties
cors.allowed-origins=http://localhost:4200
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed-headers=*
```

#### 2. JWT токен истек
```
401 Unauthorized: JWT token has expired
```

**Решение**: Обновите токен или войдите заново:
```typescript
// В Angular
this.authService.refreshToken();
```

#### 3. База данных недоступна
```
Connection refused to database
```

**Решение**: Проверьте подключение к базе данных:
```bash
# PostgreSQL
psql -h localhost -U taskboard -d taskboarddb

# H2 Console
http://localhost:8080/h2-console
```

#### 4. Порт занят
```
Port 8080 is already in use
```

**Решение**: Освободите порт или измените конфигурацию:
```properties
server.port=8081
```

### Логирование

#### Backend Logs
```properties
# application.properties
logging.level.com.taskboard.api=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

#### Frontend Logs
```typescript
// environment.ts
export const environment = {
  logLevel: 'debug'
};
```

## Безопасность

### 1. JWT токены
- Используйте сильные секретные ключи
- Настройте правильное время жизни токенов
- Реализуйте refresh токены

### 2. CORS
- Ограничьте разрешенные origins
- Используйте HTTPS в production
- Настройте правильные headers

### 3. База данных
- Используйте сильные пароли
- Ограничьте доступ к базе данных
- Регулярно обновляйте зависимости

## Производительность

### 1. Backend оптимизация
- Настройте connection pool
- Используйте кэширование
- Оптимизируйте SQL запросы
- Настройте JVM параметры

### 2. Frontend оптимизация
- Используйте OnPush change detection
- Реализуйте lazy loading
- Оптимизируйте bundle size
- Используйте Service Workers

### 3. Мониторинг
- Настройте APM (Application Performance Monitoring)
- Используйте Prometheus + Grafana
- Настройте алерты
- Регулярно анализируйте метрики

## Заключение

Данное руководство обеспечивает полную интеграцию Task Management Platform с современными инструментами разработки, тестирования и мониторинга. Следуя этим инструкциям, вы сможете:

1. **Быстро развернуть** систему в любой среде
2. **Обеспечить качество** через комплексное тестирование
3. **Мониторить производительность** в реальном времени
4. **Масштабировать систему** при росте нагрузки
5. **Поддерживать безопасность** на высоком уровне

Для получения дополнительной информации обратитесь к соответствующим разделам документации или создайте issue в репозитории проекта.
