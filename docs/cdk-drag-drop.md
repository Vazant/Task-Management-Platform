## Angular CDK Drag & Drop — Конспект

### Основы
- Модуль: `DragDropModule` из `@angular/cdk/drag-drop`.
- Директивы: `cdkDrag`, `cdkDropList`.
- События: `cdkDropListDropped(event: CdkDragDrop<T[]>)`.

### Перемещение в пределах списка
```ts
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';

drop(event: CdkDragDrop<string[]>) {
  if (event.previousIndex !== event.currentIndex) {
    moveItemInArray(this.items, event.previousIndex, event.currentIndex);
  }
}
```

### Перенос между списками
```ts
import { transferArrayItem } from '@angular/cdk/drag-drop';

drop(event: CdkDragDrop<string[]>) {
  if (event.previousContainer === event.container) {
    moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
  } else {
    transferArrayItem(
      event.previousContainer.data,
      event.container.data,
      event.previousIndex,
      event.currentIndex,
    );
  }
}
```

### Best practices
- Включайте `cdkDropListSortingDisabled` для более сложного сортирования на стороне стора.
- Обновляйте порядок через NgRx действия, не мутируйте напрямую.
- Стабилизируйте `trackBy` и используйте `ChangeDetectionStrategy.OnPush`.
- Ограничивайте перетаскивание через `cdkDragDisabled` и предикаты `cdkDropListEnterPredicate`.


