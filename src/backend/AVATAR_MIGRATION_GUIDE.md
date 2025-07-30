# 🚀 Руководство по миграции системы аватаров

## 📋 Обзор изменений

Данное руководство описывает полную миграцию системы аватаров с локального хранения файлов на современное облачное решение с использованием:

- **PostgreSQL** для метаданных
- **MinIO/S3** для хранения файлов
- **CDN** для быстрой отдачи
- **Presigned URL** для безопасной загрузки
- **Версионирование** и **lifecycle-правила**

## 🎯 Преимущества новой архитектуры

### ✅ Решенные проблемы
- ❌ **404 ошибки** → ✅ Всегда доступные файлы через CDN
- ❌ **Проблемы масштабируемости** → ✅ Облачное хранилище
- ❌ **Отсутствие резервного копирования** → ✅ Автоматическое версионирование
- ❌ **Нет единой точки управления** → ✅ Централизованный ImageService

### 🚀 Новые возможности
- **Presigned URL** для безопасной загрузки
- **Версионирование** аватаров
- **CDN** для быстрой отдачи
- **Lifecycle-правила** для автоматической очистки
- **Мониторинг** и метрики
- **Кэширование** через Redis

## 📦 Требования

### Системные требования
- Docker и Docker Compose
- Java 17+
- Maven 3.6+
- PostgreSQL 15+ (для продакшн)
- MinIO или AWS S3
- Redis (для кэширования)

### Зависимости
Все зависимости уже добавлены в `pom.xml`:
- `software.amazon.awssdk:s3` - для AWS S3
- `io.minio:minio` - для MinIO
- `org.postgresql:postgresql` - для PostgreSQL
- `org.flywaydb:flyway-core` - для миграций
- `org.springframework.boot:spring-boot-starter-data-redis` - для Redis

## 🛠 Пошаговая миграция

### Шаг 1: Подготовка инфраструктуры

#### 1.1 Запуск инфраструктуры
```bash
cd src/backend
docker-compose up -d postgres minio redis
```

#### 1.2 Инициализация MinIO
```bash
# Установка MinIO Client (mc)
wget https://dl.min.io/client/mc/release/linux-amd64/mc
chmod +x mc
sudo mv mc /usr/local/bin/

# Инициализация bucket
./scripts/init-minio.sh
```

#### 1.3 Проверка доступности сервисов
```bash
# PostgreSQL
docker exec taskboard-postgres pg_isready -U taskboard -d taskboarddb

# MinIO
curl http://localhost:9000/minio/health/live

# Redis
docker exec taskboard-redis redis-cli ping
```

### Шаг 2: Настройка базы данных

#### 2.1 Создание базы данных PostgreSQL (для продакшн)
```sql
CREATE DATABASE taskboarddb;
CREATE USER taskboard WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE taskboarddb TO taskboard;
```

#### 2.2 Применение миграций
```bash
# Автоматически при запуске приложения через Flyway
# Или вручную:
mvn flyway:migrate
```

### Шаг 3: Конфигурация приложения

#### 3.1 Настройка профилей
Создайте файлы конфигурации для разных окружений:

**application-dev.properties:**
```properties
# Development с H2
spring.datasource.url=jdbc:h2:mem:taskboarddb
spring.datasource.driverClassName=org.h2.Driver

# MinIO для разработки
app.storage.provider=minio
app.storage.endpoint=http://localhost:9000
app.storage.access-key=minioadmin
app.storage.secret-key=minioadmin
app.storage.bucket-name=taskboard-avatars
```

**application-prod.properties:**
```properties
# Production с PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/taskboarddb
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=taskboard
spring.datasource.password=password

# AWS S3 для продакшн
app.storage.provider=s3
app.storage.endpoint=https://s3.amazonaws.com
app.storage.access-key=${AWS_ACCESS_KEY_ID}
app.storage.secret-key=${AWS_SECRET_ACCESS_KEY}
app.storage.bucket-name=taskboard-avatars-prod
app.storage.region=us-east-1
app.storage.cdn-base-url=https://cdn.taskboard.com
```

### Шаг 4: Миграция данных

#### 4.1 Скрипт миграции существующих аватаров
```bash
# Создайте скрипт для переноса существующих файлов
./scripts/migrate-existing-avatars.sh
```

#### 4.2 Обновление ссылок в базе данных
```sql
-- Обновление ссылок на аватары в таблице users
UPDATE users 
SET avatar = CONCAT('https://cdn.taskboard.com/avatars/', id, '/current.jpg')
WHERE avatar IS NOT NULL AND avatar != '';
```

### Шаг 5: Тестирование

#### 5.1 Запуск приложения
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 5.2 Тестирование API
```bash
# 1. Получение presigned URL для загрузки
curl -X POST http://localhost:3000/api/avatars/upload-url \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fileName": "avatar.jpg",
    "contentType": "image/jpeg",
    "fileSize": 1024000
  }'

# 2. Загрузка файла через presigned URL
curl -X PUT "PRESIGNED_URL_FROM_STEP_1" \
  -H "Content-Type: image/jpeg" \
  --upload-file avatar.jpg

# 3. Подтверждение загрузки
curl -X POST http://localhost:3000/api/avatars/confirm \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "storageKey": "STORAGE_KEY_FROM_STEP_1"
  }'

# 4. Получение аватара
curl -X GET http://localhost:3000/api/avatars/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🔧 Настройка CDN

### Вариант 1: Nginx как CDN
```nginx
# nginx/nginx.conf
server {
    listen 80;
    server_name cdn.taskboard.com;

    location /avatars/ {
        proxy_pass http://minio:9000/taskboard-avatars/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        
        # Кэширование
        expires 1d;
        add_header Cache-Control "public, immutable";
        
        # CORS
        add_header Access-Control-Allow-Origin "*";
        add_header Access-Control-Allow-Methods "GET, OPTIONS";
    }
}
```

### Вариант 2: AWS CloudFront
1. Создайте CloudFront Distribution
2. Origin: S3 bucket
3. Behaviors: `/avatars/*` → Cache based on query strings
4. CNAME: `cdn.taskboard.com`

## 📊 Мониторинг

### Prometheus метрики
```yaml
# prometheus/prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'taskboard-api'
    static_configs:
      - targets: ['localhost:3000']
    metrics_path: '/actuator/prometheus'
```

### Grafana дашборды
Создайте дашборды для мониторинга:
- Количество загрузок аватаров
- Размер хранилища
- Latency API
- Ошибки 4xx/5xx

## 🔒 Безопасность

### JWT валидация
```java
// Проверка прав доступа в контроллере
@PreAuthorize("isAuthenticated()")
@PreAuthorize("#userId == authentication.principal.id")
```

### CORS настройки
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://taskboard.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/avatars/**", configuration);
        return source;
    }
}
```

## 🚀 Развертывание в продакшн

### 1. Подготовка AWS S3
```bash
# Создание S3 bucket
aws s3 mb s3://taskboard-avatars-prod

# Настройка versioning
aws s3api put-bucket-versioning \
  --bucket taskboard-avatars-prod \
  --versioning-configuration Status=Enabled

# Настройка lifecycle
aws s3api put-bucket-lifecycle-configuration \
  --bucket taskboard-avatars-prod \
  --lifecycle-configuration file://lifecycle.json
```

### 2. Настройка CloudFront
```bash
# Создание CloudFront Distribution
aws cloudfront create-distribution \
  --distribution-config file://cloudfront-config.json
```

### 3. Развертывание приложения
```bash
# Сборка
mvn clean package -DskipTests

# Запуск с продакшн профилем
java -jar target/taskboard-api-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod
```

## 🔄 Rollback план

### В случае проблем:
1. **Откат к старой системе:**
   ```bash
   # Восстановление из бэкапа
   pg_restore -d taskboarddb backup.sql
   
   # Переключение на старые endpoints
   # Обновление конфигурации
   ```

2. **Восстановление файлов:**
   ```bash
   # Из S3/MinIO
   aws s3 sync s3://taskboard-avatars-backup ./local-avatars/
   ```

## 📈 Метрики и алерты

### Ключевые метрики для мониторинга:
- **Upload success rate** > 95%
- **API response time** < 500ms
- **Storage usage** < 80%
- **CDN hit ratio** > 90%

### Алерты:
- Ошибки загрузки > 5%
- Высокая латентность API
- Превышение лимитов хранилища
- Проблемы с CDN

## 🧪 Тестирование

### Unit тесты
```bash
mvn test -Dtest=AvatarServiceTest
```

### Интеграционные тесты
```bash
mvn test -Dtest=AvatarControllerIntegrationTest
```

### Load тесты
```bash
# Используя Apache Bench
ab -n 1000 -c 10 http://localhost:3000/api/avatars/me
```

## 📚 Дополнительные ресурсы

- [AWS S3 Best Practices](https://docs.aws.amazon.com/AmazonS3/latest/userguide/best-practices.html)
- [MinIO Documentation](https://docs.min.io/)
- [Spring Boot File Upload](https://spring.io/guides/gs/uploading-files/)
- [Flyway Migration Guide](https://flywaydb.org/documentation/)

## 🆘 Поддержка

При возникновении проблем:
1. Проверьте логи: `docker-compose logs -f`
2. Проверьте метрики: http://localhost:9090
3. Проверьте Grafana: http://localhost:3001
4. Создайте issue в репозитории

---

**Удачи с миграцией! 🚀** 
