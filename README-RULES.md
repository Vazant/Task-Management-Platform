# Cursor AI Rules - Руководство по использованию

## Обзор

Этот документ объясняет, как использовать правила Cursor AI для проекта Task Management Platform. Правила автоматически применяются при работе с соответствующими файлами и помогают обеспечить качество кода, безопасность и соответствие лучшим практикам.

## Структура правил

### 🔒 Безопасность
- **`java-security.mdc`** - OWASP Top 10 защита для Java Spring Boot приложений
- **`security.mdc`** - Общие правила безопасности для всего приложения

### 🐳 Инфраструктура и развертывание
- **`docker-best-practices.mdc`** - Безопасная контейнеризация с multi-stage builds
- **`kubernetes-best-practices.mdc`** - Безопасное развертывание с RBAC и Pod Security Standards
- **`kafka-best-practices.mdc`** - Kafka messaging: события, producers, consumers

### 🧪 Тестирование
- **`testing-unit-mockito.mdc`** - Unit тестирование с JUnit 5 и Mockito
- **`testing-integration-testcontainers.mdc`** - Интеграционное тестирование с Testcontainers
- **`testing.mdc`** - Общие правила тестирования

### 🏗️ Архитектура
- **`microservices-architecture.mdc`** - Микросервисная архитектура и паттерны
- **`microservices-best-practices.mdc`** - Лучшие практики микросервисов
- **`java-microservices-best-practices.mdc`** - Java микросервисы: Spring Boot, DTOs, события

### 💻 Разработка
- **`java-spring-boot.mdc`** - Правила для Java Spring Boot бэкенда
- **`angular-typescript.mdc`** - Правила для Angular TypeScript фронтенда
- **`configuration-constants.mdc`** - Константы и конфигурация

### 📊 Мониторинг и качество
- **`monitoring-logging.mdc`** - Мониторинг и логирование: Prometheus, Grafana, Jaeger
- **`performance.mdc`** - Правила оптимизации производительности
- **`documentation.mdc`** - Стандарты документации и API
- **`readme-best-practices.mdc`** - Лучшие практики написания README файлов

### 🚀 DevOps и управление
- **`git-workflow.mdc`** - Git workflow и управление версиями
- **`project-overview.mdc`** - Общие принципы и архитектура проекта
- **`daily-logging.mdc`** - Лучшие практики ведения ежедневных логов

## Как использовать правила

### Автоматическое применение

Правила автоматически применяются Cursor AI при работе с соответствующими файлами проекта. Каждое правило содержит:

- `globs` - паттерны файлов, к которым применяется правило
- `alwaysApply` - флаг автоматического применения
- `description` - описание назначения правила

### Ручное применение

Вы можете ссылаться на конкретные правила в чате с Cursor AI:

#### Безопасность
```
@java-security.mdc Создай безопасный контроллер для управления пользователями
@security.mdc Настрой аутентификацию и авторизацию
```

#### Инфраструктура
```
@docker-best-practices.mdc Создай Dockerfile для нового сервиса
@kubernetes-best-practices.mdc Создай Kubernetes манифесты для развертывания
@kafka-best-practices.mdc Настрой Kafka для асинхронного взаимодействия
```

#### Тестирование
```
@testing-unit-mockito.mdc Напиши unit тесты для TaskService
@testing-integration-testcontainers.mdc Создай интеграционные тесты с PostgreSQL
@testing.mdc Напиши E2E тесты для пользовательских сценариев
```

#### Архитектура
```
@microservices-architecture.mdc Создай новый микросервис для управления задачами
@microservices-best-practices.mdc Настрой взаимодействие между сервисами
@java-microservices-best-practices.mdc Создай контроллер для управления пользователями
```

#### Разработка
```
@java-spring-boot.mdc Создай сервис для работы с задачами
@angular-typescript.mdc Создай компонент для отображения списка задач
@configuration-constants.mdc Создай константы для API endpoints
```

#### Мониторинг и документация
```
@monitoring-logging.mdc Настрой мониторинг и логирование
@performance.mdc Оптимизируй производительность приложения
@documentation.mdc Создай API документацию
@readme-best-practices.mdc Создай качественный README файл
@daily-logging.mdc Создай структурированный ежедневный лог
```

## Примеры использования

### Создание безопасного микросервиса

```
@java-security.mdc @microservices-architecture.mdc @docker-best-practices.mdc

Создай новый микросервис для управления задачами со следующими требованиями:
- Защита от OWASP Top 10
- Следование принципам 12-Factor App
- Multi-stage Docker build
- Health checks и метрики
- Интеграция с Kafka для событий
```

### Написание тестов

```
@testing-unit-mockito.mdc @testing-integration-testcontainers.mdc

Напиши тесты для TaskService:
- Unit тесты с моками для всех зависимостей
- Интеграционные тесты с Testcontainers
- Покрытие всех методов и edge cases
- Проверка производительности
```

### Настройка инфраструктуры

```
@kubernetes-best-practices.mdc @monitoring-logging.mdc

Создай Kubernetes манифесты для развертывания микросервиса:
- Pod Security Standards
- RBAC с минимальными правами
- Resource limits и requests
- Health checks и probes
- Мониторинг с Prometheus
```

### Ведение ежедневных логов

```
@daily-logging.mdc

Создай ежедневный лог для архитектурного решения:
- Переход на микросервисную архитектуру
- Рассмотрение различных вариантов
- Обоснование выбора решения
- План реализации и следующие шаги
```

## Комбинирование правил

Вы можете комбинировать несколько правил для комплексного решения:

```
@java-security.mdc @testing-unit-mockito.mdc @docker-best-practices.mdc

Создай безопасный REST API для управления задачами:
- Защита от OWASP Top 10 уязвимостей
- Полное покрытие unit тестами
- Безопасный Docker контейнер
- Валидация входных данных
- Обработка ошибок
```

## Обновление правил

### Когда обновлять
- При изменении архитектуры проекта
- При добавлении новых технологий
- При изменении стандартов кодирования
- При получении обратной связи от команды
- При обнаружении новых уязвимостей безопасности

### Как обновлять
1. Отредактируйте соответствующий `.mdc` файл в `.cursor/rules/`
2. Проверьте синтаксис и форматирование
3. Протестируйте правила на примерах кода
4. Зафиксируйте изменения в Git
5. Обновите этот README при необходимости

## Структура файла правил

Каждое правило следует стандартной структуре:

```markdown
---
description: "Описание правила"
globs: ["**/*.java", "**/*.ts"]
alwaysApply: true
---

<!--
Sources: [ссылки на источники]
Last Updated: [дата]
Maintainer: [команда]
-->

# Название правила

## Назначение и область применения
[Описание что правило делает]

## Когда применять
[Триггеры для применения]

## ✅ DO: Правильные практики
[Примеры правильного кода]

## ❌ DON'T: Неправильные практики
[Примеры неправильного кода]

## Проверка соответствия
[Команды для проверки]

## Чеклист
[Список проверок]

## Примеры использования
[Практические примеры]

## Автоматизация
[CI/CD интеграция]

## Ссылки
[Полезные ресурсы]
```

## Лучшие практики

### Создание правил
- Пишите четкие и конкретные инструкции
- Приводите примеры кода
- Используйте структурированный формат
- Группируйте связанные правила
- Включайте команды для проверки

### Поддержка правил
- Регулярно проверяйте актуальность
- Обновляйте примеры кода
- Удаляйте устаревшие правила
- Документируйте изменения
- Собирайте обратную связь от команды

### Использование правил
- Комбинируйте правила для комплексных решений
- Используйте конкретные ссылки на правила
- Проверяйте результат применения правил
- Предоставляйте обратную связь по правилам
- Делитесь опытом использования с командой

## Troubleshooting

### Правила не применяются
1. Проверьте, что файл соответствует glob паттерну
2. Убедитесь, что `alwaysApply: true` установлен
3. Проверьте синтаксис YAML в заголовке
4. Перезапустите Cursor AI

### Конфликт правил
1. Проверьте приоритет правил
2. Убедитесь, что правила не противоречат друг другу
3. Используйте более специфичные правила
4. Обратитесь к команде за помощью

### Неточные результаты
1. Уточните запрос с конкретными правилами
2. Предоставьте больше контекста
3. Используйте примеры из правил
4. Проверьте актуальность правил

## Полезные ссылки

### Cursor AI
- [Cursor Rules Documentation](https://docs.cursor.com/en/context/rules)
- [Cursor Directory Rules](https://cursor.directory/rules)

### Технологии проекта
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Angular Documentation](https://angular.io/docs)
- [Docker Documentation](https://docs.docker.com/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)

### Безопасность
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP Java Security Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Java_Security_Cheat_Sheet.html)

### Тестирование
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Testcontainers Documentation](https://www.testcontainers.org/)
- [Cypress Documentation](https://docs.cypress.io/)

### Мониторинг
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Jaeger Documentation](https://www.jaegertracing.io/docs/)

## Обратная связь

Если у вас есть предложения по улучшению правил или вы обнаружили проблемы:

1. Создайте Issue в репозитории
2. Предложите Pull Request с изменениями
3. Свяжитесь с командой разработки
4. Поделитесь опытом использования

---

*Правила обновлены: 2024-12-19*  
*Версия: 2.0.0*
