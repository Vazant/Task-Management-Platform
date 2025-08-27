## NgRx Entity Adapter: Patterns and Usage

### Overview
NgRx Entity provides helpers for managing collections in the store by normalizing state into `ids` and `entities`. It reduces boilerplate for CRUD operations and improves selector performance.

### Core Concepts
- EntityState<T>: `{ ids: string[] | number[]; entities: Dictionary<T>; }`
- EntityAdapter<T>: Generates reducers and selectors for collections.
- Normalization: Entities stored by id; selectors derive arrays.

### Setup
```ts
import { EntityState, createEntityAdapter } from '@ngrx/entity';

interface TasksState extends EntityState<Task> {
  loading: boolean;
  error: string | null;
}

export const tasksAdapter = createEntityAdapter<Task>({
  selectId: (task) => task.id,
  sortComparer: false,
});

export const initialState: TasksState = tasksAdapter.getInitialState({
  loading: false,
  error: null,
});
```

### CRUD Reducers
```ts
on(loadSuccess, (state, { tasks }) => tasksAdapter.setAll(tasks, state));
on(createSuccess, (state, { task }) => tasksAdapter.addOne(task, state));
on(updateSuccess, (state, { task }) => tasksAdapter.upsertOne(task, state));
on(deleteSuccess, (state, { taskId }) => tasksAdapter.removeOne(taskId, state));
```

### Selectors
```ts
const { selectAll, selectEntities, selectIds, selectTotal } = tasksAdapter.getSelectors();
export const selectAllTasks = createSelector(selectTasksState, selectAll);
export const selectTasksEntities = createSelector(selectTasksState, selectEntities);
export const selectTasksIds = createSelector(selectTasksState, selectIds);
export const selectTasksTotal = createSelector(selectTasksState, selectTotal);
```

### Filtering and Sorting
Apply UI filters in memoized selectors on top of `selectAllTasks` for performance and testability.

### Best Practices
- Keep `sortComparer` false; perform sort in selectors based on UI state.
- Use OnPush change detection and `trackBy` functions in lists.
- Handle errors in effects with typed failure actions.

