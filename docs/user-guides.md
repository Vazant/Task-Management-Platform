# User Guides and Technical Documentation

## Table of Contents

1. [Installation Guide](#installation-guide)
2. [Getting Started](#getting-started)
3. [User Manual](#user-manual)
4. [Configuration Guide](#configuration-guide)
5. [Troubleshooting](#troubleshooting)
6. [API Reference](#api-reference)

## Installation Guide

### Prerequisites

Before installing the Task Management Platform, ensure you have the following:

- **Node.js** 18.0.0 or higher
- **npm** 9.0.0 or higher
- **Java** 17 or higher
- **Maven** 3.8.0 or higher
- **Git** 2.30.0 or higher

### System Requirements

#### Minimum Requirements
- **RAM**: 4GB
- **Storage**: 2GB free space
- **OS**: Windows 10, macOS 10.15, or Ubuntu 20.04

#### Recommended Requirements
- **RAM**: 8GB or higher
- **Storage**: 5GB free space
- **OS**: Latest stable version

### Installation Steps

#### 1. Clone the Repository

```bash
git clone https://github.com/your-org/task-management-platform.git
cd task-management-platform
```

#### 2. Install Dependencies

```bash
# Install root dependencies
npm install

# Install frontend dependencies
cd client
npm install

# Install backend dependencies
cd ../server
mvn clean install
```

#### 3. Environment Configuration

Create environment files:

**Frontend (.env)**
```bash
# client/.env
API_BASE_URL=http://localhost:8080/api
ENVIRONMENT=development
ENABLE_ANALYTICS=false
```

**Backend (application.properties)**
```properties
# server/src/main/resources/application.properties
spring.datasource.url=jdbc:h2:mem:taskdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
```

#### 4. Start the Application

```bash
# Start both frontend and backend
npm run dev

# Or start separately
npm run start:client  # Frontend on http://localhost:4200
npm run start:server  # Backend on http://localhost:8080
```

#### 5. Verify Installation

1. Open browser and navigate to `http://localhost:4200`
2. You should see the application login page
3. Access H2 console at `http://localhost:8080/h2-console`

## Getting Started

### First Time Setup

#### 1. Create Admin Account

1. Navigate to the registration page
2. Fill in the required information:
   - **Email**: Your email address
   - **Password**: Strong password (min 8 characters)
   - **Full Name**: Your full name
   - **Organization**: Your organization name

#### 2. Initial Configuration

1. **Create Your First Project**
   - Click "New Project" button
   - Enter project name and description
   - Select project template (if available)

2. **Invite Team Members**
   - Go to Project Settings
   - Click "Invite Members"
   - Enter email addresses of team members

3. **Set Up Task Categories**
   - Go to Project Settings
   - Navigate to "Categories"
   - Create default categories (e.g., "Bug", "Feature", "Improvement")

### Basic Workflow

#### Creating Tasks

1. **From Project Dashboard**
   - Click "New Task" button
   - Fill in task details:
     - Title (required)
     - Description
     - Priority (Low, Medium, High, Urgent)
     - Assignee
     - Due Date
     - Labels/Tags

2. **From Task List**
   - Click "Add Task" button
   - Use the quick task form for simple tasks

#### Managing Tasks

1. **View Tasks**
   - Use different views: List, Kanban, Calendar
   - Filter by status, priority, assignee
   - Search tasks by title or description

2. **Update Task Status**
   - Drag and drop in Kanban view
   - Use status dropdown in task card
   - Update from task detail page

3. **Add Comments**
   - Open task detail page
   - Scroll to comments section
   - Type your comment and click "Add Comment"

#### Time Tracking

1. **Start Timer**
   - Open task detail page
   - Click "Start Timer" button
   - Timer will track time spent on task

2. **Stop Timer**
   - Click "Stop Timer" when done
   - Add time log description
   - Save time entry

## User Manual

### Dashboard Overview

#### Project Dashboard

The project dashboard provides a comprehensive overview of your project:

- **Task Statistics**: Total, completed, in-progress, and pending tasks
- **Recent Activity**: Latest task updates and comments
- **Team Performance**: Individual and team productivity metrics
- **Quick Actions**: Create tasks, view reports, manage team

#### Analytics Dashboard

Access detailed analytics and reporting:

- **Task Completion Trends**: Visual charts showing task completion over time
- **Team Performance**: Individual and team productivity analysis
- **Time Tracking Reports**: Time spent on tasks and projects
- **Custom Reports**: Generate custom reports based on your needs

### Task Management

#### Task Views

1. **List View**
   - Traditional table format
   - Sortable columns
   - Bulk operations
   - Advanced filtering

2. **Kanban View**
   - Drag and drop interface
   - Visual workflow representation
   - Quick status updates
   - Swimlane organization

3. **Calendar View**
   - Monthly/weekly calendar
   - Due date visualization
   - Timeline planning
   - Resource allocation

#### Task Operations

1. **Creating Tasks**
   ```typescript
   // Example: Creating a task programmatically
   const newTask = {
     title: 'Implement user authentication',
     description: 'Add JWT-based authentication',
     priority: 'high',
     assigneeId: 'user-123',
     dueDate: new Date('2024-02-01')
   };
   ```

2. **Updating Tasks**
   - Edit task details inline
   - Use task dialog for comprehensive editing
   - Bulk update multiple tasks

3. **Deleting Tasks**
   - Soft delete (moves to archive)
   - Permanent delete (irreversible)
   - Bulk delete with confirmation

### Team Collaboration

#### User Management

1. **User Roles**
   - **Admin**: Full system access
   - **Manager**: Project management capabilities
   - **Member**: Basic task management
   - **Viewer**: Read-only access

2. **Permissions**
   - Task creation and editing
   - Project management
   - User management
   - System configuration

#### Communication

1. **Comments**
   - Add comments to tasks
   - Mention team members (@username)
   - Attach files to comments

2. **Notifications**
   - Email notifications
   - In-app notifications
   - Push notifications (PWA)

### Advanced Features

#### Automation

1. **Workflow Rules**
   - Automatic status updates
   - Task assignment rules
   - Due date reminders

2. **Templates**
   - Task templates
   - Project templates
   - Workflow templates

#### Integration

1. **External Tools**
   - GitHub integration
   - Slack notifications
   - Email integration

2. **API Access**
   - RESTful API
   - Webhook support
   - Custom integrations

## Configuration Guide

### Application Configuration

#### Frontend Configuration

**Environment Variables**
```bash
# Production configuration
API_BASE_URL=https://api.yourdomain.com
ENVIRONMENT=production
ENABLE_ANALYTICS=true
ENABLE_PWA=true
```

**Angular Configuration**
```json
// angular.json
{
  "projects": {
    "client": {
      "architect": {
        "build": {
          "options": {
            "optimization": true,
            "outputHashing": "all",
            "sourceMap": false,
            "namedChunks": false
          }
        }
      }
    }
  }
}
```

#### Backend Configuration

**Database Configuration**
```properties
# PostgreSQL (Production)
spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
spring.datasource.username=taskuser
spring.datasource.password=securepassword
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# MySQL (Alternative)
spring.datasource.url=jdbc:mysql://localhost:3306/taskdb
spring.datasource.username=taskuser
spring.datasource.password=securepassword
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

**Security Configuration**
```properties
# JWT Configuration
jwt.secret=your-secret-key
jwt.expiration=86400000

# CORS Configuration
cors.allowed-origins=http://localhost:4200,https://yourdomain.com
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed-headers=*
```

### Customization

#### Theme Configuration

**Custom Colors**
```scss
// src/styles/variables.scss
$primary-color: #1976d2;
$secondary-color: #dc004e;
$success-color: #4caf50;
$warning-color: #ff9800;
$error-color: #f44336;
```

**Custom Components**
```typescript
// Custom component configuration
@Component({
  selector: 'app-custom-task-card',
  templateUrl: './custom-task-card.component.html',
  styleUrls: ['./custom-task-card.component.scss']
})
export class CustomTaskCardComponent {
  // Custom implementation
}
```

#### Workflow Configuration

**Custom Statuses**
```typescript
// Define custom task statuses
export const CUSTOM_TASK_STATUSES = [
  'draft',
  'in-review',
  'approved',
  'in-development',
  'testing',
  'deployed'
] as const;
```

**Custom Fields**
```typescript
// Add custom fields to tasks
export interface CustomTask extends Task {
  customField1?: string;
  customField2?: number;
  customField3?: boolean;
}
```

## Troubleshooting

### Common Issues

#### Frontend Issues

1. **Application Won't Start**
   ```bash
   # Check Node.js version
   node --version
   
   # Clear npm cache
   npm cache clean --force
   
   # Reinstall dependencies
   rm -rf node_modules package-lock.json
   npm install
   ```

2. **Build Errors**
   ```bash
   # Check TypeScript errors
   npm run lint
   
   # Fix auto-fixable issues
   npm run lint:fix
   
   # Check for missing dependencies
   npm audit
   ```

3. **Runtime Errors**
   - Check browser console for errors
   - Verify API endpoints are accessible
   - Check network connectivity

#### Backend Issues

1. **Database Connection**
   ```bash
   # Check database status
   mvn spring-boot:run --debug
   
   # Verify database configuration
   # Check application.properties
   ```

2. **Port Conflicts**
   ```bash
   # Check if port is in use
   netstat -an | grep 8080
   
   # Change port in application.properties
   server.port=8081
   ```

3. **Memory Issues**
   ```bash
   # Increase JVM memory
   export JAVA_OPTS="-Xmx2g -Xms1g"
   mvn spring-boot:run
   ```

### Performance Issues

#### Frontend Performance

1. **Slow Loading**
   - Enable production build
   - Use lazy loading
   - Optimize bundle size

2. **Memory Leaks**
   - Check for unsubscribed observables
   - Review component lifecycle
   - Monitor memory usage

#### Backend Performance

1. **Database Queries**
   - Enable query logging
   - Optimize database indexes
   - Use pagination

2. **API Response Time**
   - Enable caching
   - Optimize database queries
   - Use async processing

### Security Issues

1. **Authentication Problems**
   - Check JWT configuration
   - Verify user credentials
   - Review security settings

2. **Authorization Issues**
   - Check user roles and permissions
   - Verify API endpoints
   - Review security policies

## API Reference

### Authentication

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user-123",
    "email": "user@example.com",
    "name": "John Doe",
    "role": "MEMBER"
  }
}
```

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "newuser@example.com",
  "password": "password123",
  "name": "New User",
  "organization": "My Company"
}
```

### Tasks API

#### Get All Tasks
```http
GET /api/tasks?status=in-progress&priority=high
Authorization: Bearer <token>
```

#### Create Task
```http
POST /api/tasks
Content-Type: application/json
Authorization: Bearer <token>

{
  "title": "New Task",
  "description": "Task description",
  "priority": "high",
  "assigneeId": "user-123",
  "dueDate": "2024-02-01T00:00:00Z"
}
```

#### Update Task
```http
PATCH /api/tasks/{taskId}
Content-Type: application/json
Authorization: Bearer <token>

{
  "status": "completed",
  "description": "Updated description"
}
```

#### Delete Task
```http
DELETE /api/tasks/{taskId}
Authorization: Bearer <token>
```

### Projects API

#### Get All Projects
```http
GET /api/projects
Authorization: Bearer <token>
```

#### Create Project
```http
POST /api/projects
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "New Project",
  "description": "Project description",
  "startDate": "2024-01-01T00:00:00Z",
  "endDate": "2024-12-31T00:00:00Z"
}
```

### Error Responses

#### Standard Error Format
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/tasks",
  "details": [
    {
      "field": "title",
      "message": "Title is required"
    }
  ]
}
```

#### Common HTTP Status Codes

- **200**: Success
- **201**: Created
- **400**: Bad Request
- **401**: Unauthorized
- **403**: Forbidden
- **404**: Not Found
- **500**: Internal Server Error

### WebSocket Events

#### Real-time Updates

```typescript
// Connect to WebSocket
const socket = new WebSocket('ws://localhost:8080/ws');

// Listen for task updates
socket.onmessage = (event) => {
  const data = JSON.parse(event.data);
  
  switch (data.type) {
    case 'TASK_CREATED':
      // Handle task creation
      break;
    case 'TASK_UPDATED':
      // Handle task update
      break;
    case 'TASK_DELETED':
      // Handle task deletion
      break;
  }
};
```

This comprehensive documentation provides users with all the information they need to install, configure, and use the Task Management Platform effectively.
