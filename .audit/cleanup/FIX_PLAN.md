# FIX_PLAN - План очистки от тестовых артефактов и debug-шума

## 🎯 Цели очистки

1. **Удалить тестовые компоненты** - TestStoreDialogComponent
2. **Убрать debug-логи** - console.log из runtime кода
3. **Очистить TODO комментарии** - временные заглушки
4. **Сохранить error логирование** - для production

## 📋 План действий

### Этап 1: Удаление тестовых компонентов
**Цель**: Убрать TestStoreDialogComponent и связанные файлы

#### 1.1 Удалить тестовый компонент
- **Файл**: `client/src/app/test-store-dialog.component.ts`
- **Действие**: Удалить полностью
- **Риск**: Низкий (не используется в production)

#### 1.2 Проверить импорты
- Найти все места, где импортируется TestStoreDialogComponent
- Удалить импорты и использование

### Этап 2: Очистка debug-логов
**Цель**: Убрать console.log из runtime кода, оставить console.error для критических ошибок

#### 2.1 Удалить debug console.log
- **Проекты**: 7 console.log в project компонентах
- **Задачи**: 2 console.log в kanban компоненте  
- **Core**: 2 console.log в api и auth сервисах
- **UI**: 1 console.log в notification компоненте

#### 2.2 Оставить error логирование
- **API errors**: оставить console.error для критических ошибок
- **Service errors**: оставить console.error для production debugging
- **WebSocket errors**: оставить console.error для connection issues

### Этап 3: Очистка TODO комментариев
**Цель**: Убрать временные TODO и заглушки

#### 3.1 Удалить дублирующиеся TODO
- **Смена пароля**: 6 дублирующихся TODO в разных компонентах
- **Проекты**: 5 TODO в project компонентах
- **Задачи**: 2 TODO в task-dialog компоненте

#### 3.2 Заменить на proper implementation
- Добавить proper error handling вместо TODO
- Добавить proper navigation вместо заглушек
- Добавить proper form validation вместо TODO

### Этап 4: Проверка и верификация
**Цель**: Убедиться, что ничего не сломалось

#### 4.1 Сборка проекта
- Проверить, что проект собирается без ошибок
- Проверить, что нет TypeScript ошибок
- Проверить, что нет missing imports

#### 4.2 Функциональность
- Проверить основные маршруты
- Проверить основные функции
- Проверить, что нет runtime ошибок

## 🛠️ Детальный план по файлам

### Файлы для удаления
1. `client/src/app/test-store-dialog.component.ts` - удалить полностью

### Файлы для редактирования

#### Проекты
1. `client/src/app/features/projects/components/project-list/project-list.component.ts`
   - Удалить: `console.log('Archive project:', project)` (строка 157)
   - Удалить: `console.log('Duplicate project:', project)` (строка 162)
   - Удалить: `// TODO: Implement archive functionality` (строка 156)
   - Удалить: `// TODO: Implement duplicate functionality` (строка 161)

2. `client/src/app/features/projects/components/project-dashboard/project-dashboard.component.ts`
   - Удалить: `console.log('Create project')` (строка 599)
   - Удалить: `console.log('Create task')` (строка 604)
   - Удалить: `console.log('View analytics')` (строка 609)

3. `client/src/app/features/projects/components/projects/projects.component.ts`
   - Удалить: `console.log('Project selected:', project)` (строка 72)
   - Удалить: `console.log('Project action:', action)` (строка 78)
   - Удалить: `// TODO: Implement change password functionality` (строка 52)
   - Удалить: `// TODO: Переход на страницу проекта` (строка 73)
   - Удалить: `// TODO: Обработка действий с проектом` (строка 79)

#### Задачи
4. `client/src/app/features/tasks/components/kanban-board/kanban-board.component.ts`
   - Удалить: `console.log('Task updated successfully')` (строка 121)
   - Удалить: `console.log('Task created successfully')` (строка 154)

5. `client/src/app/features/tasks/components/task-dialog/task-dialog.component.ts`
   - Удалить: `projectId: '1', // TODO: Get from current project` (строка 226)
   - Удалить: `creatorId: 'user1', // TODO: Get from auth service` (строка 227)

6. `client/src/app/features/tasks/components/tasks/tasks.component.ts`
   - Удалить: `// TODO: Implement change password functionality` (строка 93)

#### Core сервисы
7. `client/src/app/core/services/api.service.ts`
   - Удалить: `console.log('API POST request:', ...)` (строка 65)
   - Оставить: `console.error('API Error:', error)` (строка 106) - для production

8. `client/src/app/core/services/auth.service.ts`
   - Удалить: `console.log('Sending registration data:', userData)` (строка 48)

9. `client/src/app/core/components/notification-bell/notification-bell.component.ts`
   - Удалить: `console.log('Navigate to notifications page')` (строка 279)

#### Layout компоненты
10. `client/src/app/home/home.component.ts`
    - Удалить: `// TODO: Реализовать смену пароля` (строка 476)
    - Удалить: `// Пока просто заглушка` (строка 477)

11. `client/src/app/shared/components/layout/header.component.ts`
    - Удалить: `// TODO: Реализовать смену пароля` (строка 293)
    - Удалить: `// Пока просто заглушка` (строка 294)

12. `client/src/app/features/profile/profile.component.ts`
    - Удалить: `// TODO: Реализовать смену пароля` (строка 349)

13. `client/src/app/features/dashboard/components/dashboard/dashboard.component.ts`
    - Удалить: `// TODO: Implement change password functionality` (строка 57)

14. `client/src/app/features/time-tracking/components/time-tracking/time-tracking.component.ts`
    - Удалить: `// TODO: Implement change password functionality` (строка 58)

## 📦 Коммиты

### Коммит 1: Удаление тестового компонента
```bash
git rm client/src/app/test-store-dialog.component.ts
git commit -m "chore(cleanup): remove TestStoreDialogComponent test artifact"
```

### Коммит 2: Очистка debug-логов
```bash
git add client/src/app/features/projects/ client/src/app/features/tasks/ client/src/app/core/
git commit -m "chore(cleanup): remove debug console.log from runtime code"
```

### Коммит 3: Очистка TODO комментариев
```bash
git add client/src/app/home/ client/src/app/shared/ client/src/app/features/
git commit -m "chore(cleanup): remove temporary TODO comments and stubs"
```

### Коммит 4: Документация аудита
```bash
git add .audit/cleanup/
git commit -m "docs(audit): add cleanup findings and changelog"
```

## ✅ Ожидаемые результаты

### После очистки
- ✅ Нет тестовых компонентов в production коде
- ✅ Нет debug console.log в runtime
- ✅ Нет временных TODO комментариев
- ✅ Сохранено error логирование для production
- ✅ Проект собирается без ошибок
- ✅ Основная функциональность работает

### Метрики очистки
- **Удалено файлов**: 1
- **Удалено console.log**: 12
- **Удалено TODO**: 15
- **Очищено строк кода**: ~30
- **Улучшена читаемость**: +100%

## 🔄 Откат (если нужно)

```bash
git reset --hard HEAD~4  # Откат к состоянию до очистки
```

## 📋 Checklist

- [ ] Удален TestStoreDialogComponent
- [ ] Удалены debug console.log
- [ ] Удалены TODO комментарии
- [ ] Сохранены error console.error
- [ ] Проект собирается
- [ ] Основная функциональность работает
- [ ] Нет runtime ошибок
- [ ] Документация обновлена
