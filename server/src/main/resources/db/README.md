# Database Migrations

Этот каталог содержит миграции базы данных для Task Management Platform.

## Структура

```
db/
├── changelog/
│   ├── db.changelog-master.xml          # Главный файл миграций
│   └── changesets/                       # Отдельные миграции
│       ├── 001-create-users-table.xml
│       ├── 002-create-user-avatars-table.xml
│       ├── 003-create-webauthn-credentials-table.xml
│       ├── 004-create-projects-table.xml
│       ├── 005-create-tasks-table.xml
│       ├── 006-create-task-comments-table.xml
│       ├── 007-create-task-attachments-table.xml
│       ├── 008-create-time-entries-table.xml
│       ├── 009-create-notifications-table.xml
│       └── 010-create-password-reset-tokens-table.xml
└── README.md
```

## Использование

### Development
```bash
# Запуск с H2 базой данных
./mvnw spring-boot:run
```

### Production
```bash
# Запуск с PostgreSQL
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

### Ручной запуск миграций
```bash
# Проверка статуса миграций
./mvnw liquibase:status

# Применение миграций
./mvnw liquibase:update

# Откат миграций
./mvnw liquibase:rollback -Dliquibase.rollbackCount=1
```

## Создание новых миграций

1. Создайте новый файл в `changesets/` с номером больше последнего
2. Добавьте include в `db.changelog-master.xml`
3. Используйте правильный формат:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.20.xsd">

    <changeSet id="011-new-feature" author="developer">
        <!-- Ваши изменения -->
    </changeSet>

</databaseChangeLog>
```

## Правила

- **Никогда не изменяйте** уже примененные миграции
- **Всегда создавайте новые** changesets для изменений
- **Используйте осмысленные** ID и автора
- **Тестируйте миграции** на копии продакшен данных
- **Делайте бэкапы** перед применением миграций

## Поддерживаемые БД

- **Development**: H2 (in-memory)
- **Production**: PostgreSQL
- **Test**: H2 (in-memory)

## Конфигурация

### Development (application.properties)
```properties
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
spring.jpa.hibernate.ddl-auto=validate
```

### Production (application-prod.properties)
```properties
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
spring.liquibase.contexts=production
spring.jpa.hibernate.ddl-auto=validate
```
