# CANDIDATES - Кандидаты на удаление

## 🧪 Тестовые компоненты

### TestStoreDialogComponent
- **Файл**: `client/src/app/test-store-dialog.component.ts`
- **Тип**: Тестовый компонент для проверки Store injection
- **Действие**: Удалить полностью
- **Риск**: Низкий (не используется в production)

## 🐛 Debug-логи в runtime коде

### Проекты
- `client/src/app/features/projects/components/project-list/project-list.component.ts:157` - `console.log('Archive project:', project)`
- `client/src/app/features/projects/components/project-list/project-list.component.ts:162` - `console.log('Duplicate project:', project)`
- `client/src/app/features/projects/components/project-dashboard/project-dashboard.component.ts:599` - `console.log('Create project')`
- `client/src/app/features/projects/components/project-dashboard/project-dashboard.component.ts:604` - `console.log('Create task')`
- `client/src/app/features/projects/components/project-dashboard/project-dashboard.component.ts:609` - `console.log('View analytics')`
- `client/src/app/features/projects/components/projects/projects.component.ts:72` - `console.log('Project selected:', project)`
- `client/src/app/features/projects/components/projects/projects.component.ts:78` - `console.log('Project action:', action)`

### Задачи
- `client/src/app/features/tasks/components/kanban-board/kanban-board.component.ts:121` - `console.log('Task updated successfully')`
- `client/src/app/features/tasks/components/kanban-board/kanban-board.component.ts:154` - `console.log('Task created successfully')`

### Core сервисы
- `client/src/app/core/services/api.service.ts:65` - `console.log('API POST request:', ...)`
- `client/src/app/core/services/api.service.ts:106` - `console.error('API Error:', error)`
- `client/src/app/core/services/auth.service.ts:48` - `console.log('Sending registration data:', userData)`
- `client/src/app/core/components/notification-bell/notification-bell.component.ts:279` - `console.log('Navigate to notifications page')`

## 📝 Временные TODO комментарии

### Смена пароля (дублируется в нескольких местах)
- `client/src/app/home/home.component.ts:476-477` - TODO + заглушка
- `client/src/app/shared/components/layout/header.component.ts:293-294` - TODO + заглушка
- `client/src/app/features/profile/profile.component.ts:349` - TODO
- `client/src/app/features/dashboard/components/dashboard/dashboard.component.ts:57` - TODO
- `client/src/app/features/tasks/components/tasks/tasks.component.ts:93` - TODO
- `client/src/app/features/time-tracking/components/time-tracking/time-tracking.component.ts:58` - TODO

### Проекты
- `client/src/app/features/projects/components/project-list/project-list.component.ts:156` - TODO: Implement archive functionality
- `client/src/app/features/projects/components/project-list/project-list.component.ts:161` - TODO: Implement duplicate functionality
- `client/src/app/features/projects/components/projects/projects.component.ts:52` - TODO: Implement change password functionality
- `client/src/app/features/projects/components/projects/projects.component.ts:73` - TODO: Переход на страницу проекта
- `client/src/app/features/projects/components/projects/projects.component.ts:79` - TODO: Обработка действий с проектом

### Задачи
- `client/src/app/features/tasks/components/task-dialog/task-dialog.component.ts:226` - TODO: Get from current project
- `client/src/app/features/tasks/components/task-dialog/task-dialog.component.ts:227` - TODO: Get from auth service

## 🎯 Рекомендуемые действия

### Высокий приоритет (удалить сразу)
1. **TestStoreDialogComponent** - полностью удалить файл
2. **Debug console.log** - удалить из runtime кода
3. **TODO комментарии** - удалить временные заглушки

### Средний приоритет (проверить)
1. **Error console.error** - оставить для production логирования
2. **API console.log** - возможно нужен для debugging

### Низкий приоритет (оставить)
1. **Сервисные console.error** - могут быть нужны для production
2. **WebSocket console.log** - могут быть нужны для debugging

## 📊 Статистика

- **Тестовых компонентов**: 1
- **Debug console.log**: 12
- **TODO комментариев**: 15
- **Всего кандидатов**: 28

## ⚠️ Внимание

Некоторые console.error могут быть нужны для production логирования. Рекомендуется:
1. Удалить console.log из runtime кода
2. Оставить console.error для критических ошибок
3. Добавить proper logging service для production
