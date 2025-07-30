import type { Meta, StoryObj } from '@storybook/angular';
import { ProjectCardComponent } from './project-card.component';
import { Project } from '@models';

const meta: Meta<ProjectCardComponent> = {
  title: 'Features/Projects/ProjectCard',
  component: ProjectCardComponent,
  parameters: {
    layout: 'centered',
    docs: {
      description: {
        component: `
Компонент карточки проекта для отображения информации о проекте.

## Особенности:
- Адаптивный дизайн
- Поддержка различных состояний (выбран, наведен, загрузка)
- Настраиваемые элементы интерфейса
- Анимации и переходы
- Доступность (ARIA, клавиатурная навигация)

## Использование:
\`\`\`html
<app-project-card
  [project]="project"
  [isSelected]="false"
  [showActions]="true"
  [showSelection]="false"
  (click)="onProjectClick($event)"
  (action)="onProjectAction($event)">
</app-project-card>
\`\`\`
        `
      }
    }
  },
  argTypes: {
    project: {
      description: 'Объект проекта для отображения',
      control: 'object'
    },
    isSelected: {
      description: 'Выбран ли проект',
      control: 'boolean'
    },
    isHovered: {
      description: 'Наведен ли курсор на карточку',
      control: 'boolean'
    },
    showActions: {
      description: 'Показывать ли меню действий',
      control: 'boolean'
    },
    showSelection: {
      description: 'Показывать ли чекбокс выбора',
      control: 'boolean'
    },
    showStatus: {
      description: 'Показывать ли статус проекта',
      control: 'boolean'
    },
    showMembers: {
      description: 'Показывать ли количество участников',
      control: 'boolean'
    },
    showDates: {
      description: 'Показывать ли даты',
      control: 'boolean'
    },
    loading: {
      description: 'Состояние загрузки',
      control: 'boolean'
    },
    disabled: {
      description: 'Отключена ли карточка',
      control: 'boolean'
    },
    click: {
      description: 'Событие клика по карточке',
      action: 'clicked'
    },
    action: {
      description: 'Событие действия с проектом',
      action: 'action'
    },
    select: {
      description: 'Событие выбора проекта',
      action: 'selected'
    },
    hover: {
      description: 'Событие наведения',
      action: 'hovered'
    }
  }
};

export default meta;
type Story = StoryObj<ProjectCardComponent>;

// Моковые данные
const mockProject: Project = {
  id: '1',
  name: 'Веб-приложение TaskBoard',
  description: 'Современная платформа для управления задачами и проектами с поддержкой командной работы, аналитики и интеграций.',
  status: 'active',
  ownerId: 'user1',
  members: ['user1', 'user2', 'user3', 'user4'],
  settings: {
    allowGuestAccess: false,
    defaultTaskPriority: 'medium',
    autoAssignTasks: true,
    requireTimeTracking: true
  },
  createdAt: new Date('2024-01-15'),
  updatedAt: new Date('2024-03-20')
};

const mockArchivedProject: Project = {
  ...mockProject,
  id: '2',
  name: 'Мобильное приложение',
  description: 'Мобильное приложение для iOS и Android с нативным интерфейсом.',
  status: 'archived',
  createdAt: new Date('2023-06-10'),
  updatedAt: new Date('2024-01-05')
};

const mockCompletedProject: Project = {
  ...mockProject,
  id: '3',
  name: 'API Gateway',
  description: 'Микросервисная архитектура с API Gateway для масштабируемости.',
  status: 'completed',
  createdAt: new Date('2023-09-01'),
  updatedAt: new Date('2024-02-28')
};

const mockOnHoldProject: Project = {
  ...mockProject,
  id: '4',
  name: 'Система аналитики',
  description: 'Система сбора и анализа данных в реальном времени.',
  status: 'on-hold',
  createdAt: new Date('2024-02-01'),
  updatedAt: new Date('2024-03-15')
};

// Базовые истории
export const Default: Story = {
  args: {
    project: mockProject,
    isSelected: false,
    isHovered: false,
    showActions: true,
    showSelection: false,
    showStatus: true,
    showMembers: true,
    showDates: true,
    loading: false,
    disabled: false
  }
};

export const Selected: Story = {
  args: {
    ...Default.args,
    isSelected: true,
    showSelection: true
  }
};

export const Hovered: Story = {
  args: {
    ...Default.args,
    isHovered: true
  }
};

export const Loading: Story = {
  args: {
    ...Default.args,
    loading: true
  }
};

export const Disabled: Story = {
  args: {
    ...Default.args,
    disabled: true
  }
};

// Истории с разными статусами
export const ActiveProject: Story = {
  args: {
    ...Default.args,
    project: mockProject
  }
};

export const ArchivedProject: Story = {
  args: {
    ...Default.args,
    project: mockArchivedProject
  }
};

export const CompletedProject: Story = {
  args: {
    ...Default.args,
    project: mockCompletedProject
  }
};

export const OnHoldProject: Story = {
  args: {
    ...Default.args,
    project: mockOnHoldProject
  }
};

// Истории с разными конфигурациями
export const WithSelection: Story = {
  args: {
    ...Default.args,
    showSelection: true
  }
};

export const WithoutActions: Story = {
  args: {
    ...Default.args,
    showActions: false
  }
};

export const WithoutStatus: Story = {
  args: {
    ...Default.args,
    showStatus: false
  }
};

export const WithoutMembers: Story = {
  args: {
    ...Default.args,
    showMembers: false
  }
};

export const WithoutDates: Story = {
  args: {
    ...Default.args,
    showDates: false
  }
};

export const Minimal: Story = {
  args: {
    ...Default.args,
    showActions: false,
    showStatus: false,
    showMembers: false,
    showDates: false
  }
};

// История с длинным названием и описанием
export const LongContent: Story = {
  args: {
    ...Default.args,
    project: {
      ...mockProject,
      name: 'Очень длинное название проекта которое может не поместиться в одну строку и потребует обрезки',
      description: 'Это очень длинное описание проекта, которое содержит много текста и может занимать несколько строк. Описание включает в себя детальную информацию о функциональности, технологиях, целях и задачах проекта. Такое описание помогает пользователям лучше понять суть проекта и его назначение.'
    }
  }
};

// История без описания
export const NoDescription: Story = {
  args: {
    ...Default.args,
    project: {
      ...mockProject,
      description: ''
    }
  }
};

// История с минимальными настройками
export const MinimalSettings: Story = {
  args: {
    ...Default.args,
    project: {
      ...mockProject,
      settings: {
        allowGuestAccess: false,
        defaultTaskPriority: 'low',
        autoAssignTasks: false,
        requireTimeTracking: false
      }
    }
  }
};

// История с большим количеством участников
export const ManyMembers: Story = {
  args: {
    ...Default.args,
    project: {
      ...mockProject,
      members: Array.from({ length: 25 }, (_, i) => `user${i + 1}`)
    }
  }
};

// История без участников
export const NoMembers: Story = {
  args: {
    ...Default.args,
    project: {
      ...mockProject,
      members: []
    }
  }
};

// История с недавно созданным проектом
export const RecentlyCreated: Story = {
  args: {
    ...Default.args,
    project: {
      ...mockProject,
      createdAt: new Date(),
      updatedAt: new Date()
    }
  }
};

// История со старым проектом
export const OldProject: Story = {
  args: {
    ...Default.args,
    project: {
      ...mockProject,
      createdAt: new Date('2020-01-01'),
      updatedAt: new Date('2020-12-31')
    }
  }
};

// История для демонстрации всех состояний
export const AllStates: Story = {
  render: () => ({
    template: `
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 16px; padding: 16px;">
        <app-project-card
          [project]="activeProject"
          [isSelected]="false"
          [showActions]="true"
          [showSelection]="true">
        </app-project-card>

        <app-project-card
          [project]="archivedProject"
          [isSelected]="true"
          [showActions]="true"
          [showSelection]="true">
        </app-project-card>

        <app-project-card
          [project]="completedProject"
          [isSelected]="false"
          [showActions]="true"
          [showSelection]="true">
        </app-project-card>

        <app-project-card
          [project]="onHoldProject"
          [isSelected]="false"
          [showActions]="true"
          [showSelection]="true">
        </app-project-card>
      </div>
    `,
    props: {
      activeProject: mockProject,
      archivedProject: mockArchivedProject,
      completedProject: mockCompletedProject,
      onHoldProject: mockOnHoldProject
    }
  }),
  parameters: {
    layout: 'fullscreen'
  }
};
