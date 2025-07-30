# Project Structure Documentation

## Overview

This document describes the new organized structure of the Task Management Platform project, which follows modern full-stack development practices with clear separation of concerns.

## 🏗️ New Project Structure

```
Task-Management-Platform/
├── .gitignore                 # Git ignore rules (updated for new structure)
├── README.md                  # Project documentation (updated)
├── package.json               # Root workspace configuration
├── LICENSE                    # MIT License
│
├── docs/                      # 📚 Documentation
│   ├── DEVELOPMENT_ROADMAP.md # Development roadmap
│   └── PROJECT_STRUCTURE.md   # This file
│
├── config/                    # ⚙️ Shared Configuration
│   ├── .editorconfig         # Editor configuration
│   ├── .eslintrc.json        # ESLint rules
│   ├── .prettierrc           # Prettier formatting
│   ├── tsconfig.json         # Base TypeScript config
│   ├── tsconfig.app.json     # Angular app TypeScript config
│   └── tsconfig.spec.json    # Test TypeScript config
│
├── scripts/                   # 🔧 Build & Utility Scripts
│   └── check-imports.js      # Import validation script
│
├── client/                    # 🎨 Angular Frontend
│   ├── package.json          # Frontend dependencies
│   ├── angular.json          # Angular CLI configuration
│   ├── package-lock.json     # Lock file
│   └── src/                  # Source code
│       ├── index.html        # Main HTML file
│       ├── main.ts           # Application entry point
│       ├── styles.scss       # Global styles
│       └── app/              # Angular application
│           ├── core/         # Singleton services, guards, interceptors
│           ├── shared/       # Reusable components, pipes, directives
│           ├── features/     # Feature modules
│           │   ├── auth/     # Authentication module
│           │   ├── dashboard/# Dashboard module
│           │   ├── projects/ # Projects module
│           │   ├── tasks/    # Tasks module
│           │   ├── profile/  # User profile module
│           │   ├── settings/ # Settings module
│           │   └── time-tracking/ # Time tracking module
│           ├── store/        # NgRx state management
│           └── home/         # Home component
│
├── server/                    # 🚀 Spring Boot Backend
│   ├── package.json          # Backend npm scripts
│   ├── pom.xml              # Maven configuration
│   └── src/                 # Source code
│       ├── main/
│       │   ├── java/
│       │   │   └── com/taskboard/
│       │   │       ├── api/ # Main API package
│       │   │       │   ├── config/      # Configuration classes
│       │   │       │   ├── controller/  # REST controllers
│       │   │       │   ├── dto/         # Data Transfer Objects
│       │   │       │   ├── exception/   # Custom exceptions
│       │   │       │   ├── model/       # JPA entities
│       │   │       │   ├── repository/  # Data access layer
│       │   │       │   ├── service/     # Business logic
│       │   │       │   └── util/        # Utility classes
│       │   │       └── user/ # User management package
│       │   │           ├── config/      # User-specific config
│       │   │           ├── controller/  # User controllers
│       │   │           ├── dto/         # User DTOs
│       │   │           ├── mapper/      # Object mappers
│       │   │           ├── model/       # User entities
│       │   │           ├── repository/  # User repositories
│       │   │           └── service/     # User services
│       │   └── resources/
│       │       ├── application.properties # Main config
│       │       ├── application-test.properties # Test config
│       │       ├── logback-spring.xml   # Logging config
│       │       ├── messages.properties  # Internationalization
│       │       └── static/              # Static resources
│       └── test/
│           └── java/
│               └── com/taskboard/
│                   └── api/
│                       ├── controller/  # Integration tests
│                       └── service/     # Unit tests
│
└── node_modules/              # 📦 Dependencies (shared)
```

## 🔄 Migration Summary

### What Was Moved

1. **Configuration Files** → `config/`
   - `.editorconfig`
   - `.eslintrc.json`
   - `.prettierrc`
   - `tsconfig*.json`

2. **Documentation** → `docs/`
   - `DEVELOPMENT_ROADMAP.md`

3. **Frontend Code** → `client/`
   - `src/` → `client/src/`
   - `angular.json`
   - `package.json` (frontend)
   - `package-lock.json`

4. **Backend Code** → `server/`
   - `client/src/backend/` → `server/src/`
   - `pom.xml` → `server/pom.xml`

5. **Scripts** → `scripts/`
   - `check-imports.js` (updated paths)

### What Was Updated

1. **`.gitignore`** - Added IDE exclusions and new path patterns
2. **`README.md`** - Complete rewrite for new structure
3. **`package.json`** - New root workspace configuration
4. **Configuration paths** - Updated to work with new structure
5. **Import paths** - Updated in scripts and configs

## 🎯 Benefits of New Structure

### 1. Clear Separation of Concerns
- **Frontend** and **Backend** are completely separated
- Each has its own dependencies and build process
- Easier to work on one part without affecting the other

### 2. Centralized Configuration
- All config files in one place (`config/`)
- Easy to find and modify settings
- Consistent across the project

### 3. Better Documentation Organization
- All docs in `docs/` folder
- Clear structure for adding new documentation
- Easy to maintain and update

### 4. Improved Development Experience
- Monorepo setup with npm workspaces
- Single command to start both frontend and backend
- Shared dependencies where appropriate

### 5. Scalability
- Easy to add new frontend or backend services
- Clear structure for new features
- Modular architecture

## 🚀 Development Workflow

### Starting Development
```bash
# Install all dependencies
npm run install:all

# Start both frontend and backend
npm run dev

# Or start individually
npm run dev:client  # Angular dev server
npm run dev:server  # Spring Boot server
```

### Building for Production
```bash
# Build both frontend and backend
npm run build

# Or build individually
npm run build:client
npm run build:server
```

### Testing
```bash
# Run all tests
npm run test

# Or test individually
npm run test:client
npm run test:server
```

## 📋 Next Steps

1. **Update IDE Settings**
   - Configure your IDE to recognize the new structure
   - Update any workspace-specific settings

2. **Update CI/CD Pipeline**
   - Modify build scripts to work with new structure
   - Update deployment configurations

3. **Team Onboarding**
   - Share this documentation with team members
   - Update development guidelines

4. **Add New Features**
   - Follow the established structure
   - Add documentation for new modules

## 🔧 Configuration Details

### TypeScript Configuration

**Base config**: `config/tsconfig.json`
```json
{
  "compilerOptions": {
    "baseUrl": "../client/src",
    "paths": {
      "@app/*": ["app/*"],
      "@core/*": ["app/core/*"],
      "@shared/*": ["app/shared/*"],
      "@features/*": ["app/features/*"],
      "@store/*": ["app/store/*"],
      "@models": ["app/core/models"],
      "@services": ["app/core/services"],
      "@utils": ["app/core/utils"],
      "@guards": ["app/core/guards"],
      "@interceptors": ["app/core/interceptors"]
    }
  }
}
```

**App config**: `config/tsconfig.app.json`
```json
{
  "extends": "./tsconfig.json",
  "compilerOptions": {
    "outDir": "../client/out-tsc/app"
  },
  "include": [
    "../client/src/**/*.ts"
  ],
  "exclude": [
    "../client/src/**/*.spec.ts"
  ]
}
```

**Test config**: `config/tsconfig.spec.json`
```json
{
  "extends": "./tsconfig.json",
  "compilerOptions": {
    "outDir": "../client/out-tsc/spec",
    "types": ["jasmine"]
  },
  "include": [
    "../client/src/**/*.ts"
  ]
}
```

### ESLint Configuration

**Main config**: `config/.eslintrc.json`
```json
{
  "root": true,
  "ignorePatterns": [
    "projects/**/*",
    "dist/**/*",
    "node_modules/**/*",
    "../client/src/app/features/settings/components/user-profile-settings/user-profile-settings.component.html"
  ],
  "parserOptions": {
    "project": [
      "./tsconfig.json",
      "./tsconfig.app.json",
      "./tsconfig.spec.json"
    ]
  }
}
```

### Angular Configuration

**Main config**: `client/angular.json`
```json
{
  "projects": {
    "taskboard-pro": {
      "architect": {
        "build": {
          "options": {
            "tsConfig": "../config/tsconfig.app.json"
          }
        },
        "test": {
          "options": {
            "tsConfig": "../config/tsconfig.spec.json"
          }
        }
      }
    }
  }
}
```

### Maven Configuration

**Main config**: `server/pom.xml`
```xml
<project>
    <groupId>com.taskboard</groupId>
    <artifactId>taskboard-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <!-- Uses default Maven source directories:
         - src/main/java (source code)
         - src/main/resources (resources)
         - src/test/java (test code)
         - src/test/resources (test resources) -->
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Prettier Configuration

**Main config**: `config/.prettierrc`
```json
{
  "semi": true,
  "trailingComma": "es5",
  "singleQuote": true,
  "printWidth": 80,
  "tabWidth": 2,
  "useTabs": false,
  "bracketSpacing": true,
  "arrowParens": "avoid",
  "endOfLine": "lf"
}
```

### Usage Examples

**TypeScript Path Aliases in Code:**
```typescript
// ✅ Good: Using path aliases
import { AuthService } from '@services';
import { User } from '@models';
import { SharedComponent } from '@shared/components';
import { ProjectListComponent } from '@features/projects/components';

// ❌ Bad: Using relative paths
import { AuthService } from '../../../core/services/auth.service';
import { User } from '../../../core/models/user.model';
```

**Angular Module Imports:**
```typescript
// ✅ Good: Using barrel exports
import { CoreModule } from '@core';
import { SharedModule } from '@shared';
import { AuthModule } from '@features/auth';

// ❌ Bad: Direct file imports
import { CoreModule } from './core/core.module';
```

### EditorConfig Configuration

**Main config**: `config/.editorconfig`
```ini
# Editor configuration, see https://editorconfig.org
root = true

[*]
charset = utf-8
indent_style = space
indent_size = 2
insert_final_newline = true
trim_trailing_whitespace = true

[*.ts]
quote_type = single
ij_typescript_use_double_quotes = false

[*.md]
max_line_length = off
trim_trailing_whitespace = false
```

## 📋 File Naming Conventions

### Frontend (Angular)
- **Components**: `kebab-case.component.ts` (e.g., `user-profile.component.ts`)
- **Services**: `kebab-case.service.ts` (e.g., `auth.service.ts`)
- **Models**: `PascalCase.model.ts` (e.g., `User.model.ts`)
- **Modules**: `kebab-case.module.ts` (e.g., `auth.module.ts`)
- **Guards**: `kebab-case.guard.ts` (e.g., `auth.guard.ts`)
- **Interceptors**: `kebab-case.interceptor.ts` (e.g., `auth.interceptor.ts`)

### Backend (Spring Boot)
- **Controllers**: `PascalCaseController.java` (e.g., `AuthController.java`)
- **Services**: `PascalCaseService.java` (e.g., `AuthService.java`)
- **Repositories**: `PascalCaseRepository.java` (e.g., `UserRepository.java`)
- **Models**: `PascalCase.java` (e.g., `User.java`)
- **DTOs**: `PascalCaseDto.java` (e.g., `UserDto.java`)

### Configuration Files
- **TypeScript**: `tsconfig.json`, `tsconfig.app.json`, `tsconfig.spec.json`
- **ESLint**: `.eslintrc.json`
- **Prettier**: `.prettierrc`
- **EditorConfig**: `.editorconfig`
- **Maven**: `pom.xml`
- **Angular**: `angular.json`

## 🎉 Conclusion

The new project structure provides:
- **Better organization** and **maintainability**
- **Clearer separation** between frontend and backend
- **Easier onboarding** for new developers
- **Scalable architecture** for future growth
- **Modern development practices** with monorepo setup

This structure follows industry best practices and will make the project much easier to maintain and extend in the future. 