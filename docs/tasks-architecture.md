## Tasks Feature Architecture

### Modules & Routing
- `TasksModule` lazy-loaded at `/tasks`.
- `TasksComponent` uses NgRx to load and render tasks.
- `TaskListComponent` renders a Material list with OnPush and trackBy.

### State Management (NgRx)
- `TasksState` extends `EntityState<Task>`.
- CRUD handled via `tasksAdapter` methods: `setAll`, `addOne`, `upsertOne`, `removeOne`.
- Selectors memoize filtered and sorted task lists.

### Patterns
- Keep UI filters in state; apply in selectors.
- Effects encapsulate side-effects and user notifications.
- Components are presentational and subscribe to selectors.

