# Task Management Platform

Full-stack task management platform with Angular frontend and Spring Boot backend.

## 🏗️ Project Structure

```
/
├── .gitignore
├── README.md
├── package.json              # Root workspace configuration
│
├── docs/                     # Documentation
│   └── DEVELOPMENT_ROADMAP.md
│
├── config/                   # Shared configuration files
│   ├── .editorconfig
│   ├── .eslintrc.json
│   ├── .prettierrc
│   ├── tsconfig.json
│   ├── tsconfig.app.json
│   └── tsconfig.spec.json
│
├── scripts/                  # Build and utility scripts
│   └── check-imports.js
│
├── client/                   # Angular Frontend
│   ├── package.json
│   ├── angular.json
│   └── src/
│       ├── index.html
│       ├── main.ts
│       ├── styles.scss
│       └── app/
│           ├── core/         # Singleton services, guards, interceptors
│           ├── shared/       # Reusable components, pipes, directives
│           └── features/     # Feature modules (projects, auth, profile, etc.)
│
├── server/                   # Spring Boot Backend
│   ├── package.json
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/taskboard/
│       │   │       ├── api/          # Controllers, services, models
│       │   │       └── user/         # User management
│       │   └── resources/
│       └── test/
│
└── backend/                  # Legacy backend (to be migrated)
    └── src/
        └── main/
            └── java/
                └── com/taskboard/
```

## 🚀 Quick Start

### Prerequisites

- Node.js >= 18.0.0
- npm >= 9.0.0
- Java 17+ (for Spring Boot backend)
- Maven or Gradle

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/task-management-platform.git
   cd task-management-platform
   ```

2. **Install dependencies**
   ```bash
   # Install root dependencies
   npm install
   
   # Install client dependencies
   npm install --workspace=client
   
   # Install server dependencies (if any)
   npm install --workspace=server
   ```

3. **Start development servers**
   ```bash
   # Start both frontend and backend
   npm run dev
   
   # Or start individually
   npm run dev:client    # Angular dev server (http://localhost:4200)
   npm run dev:server    # Spring Boot server (http://localhost:8080)
   ```

## 📦 Available Scripts

### Root Level (Monorepo)

| Script | Description |
|--------|-------------|
| `npm run dev` | Start both frontend and backend in development mode |
| `npm run dev:client` | Start Angular development server |
| `npm run dev:server` | Start Spring Boot development server |
| `npm run build` | Build both frontend and backend |
| `npm run test` | Run tests for both frontend and backend |
| `npm run lint` | Run linting for both frontend and backend |
| `npm run format` | Format all code with Prettier |
| `npm run install:all` | Install dependencies for all workspaces |

### Client (Angular)

| Script | Description |
|--------|-------------|
| `npm run start` | Start Angular development server |
| `npm run build` | Build Angular application for production |
| `npm run test` | Run Angular unit tests |
| `npm run lint` | Run ESLint on Angular code |
| `npm run storybook` | Start Storybook development server |

### Server (Spring Boot)

| Script | Description |
|--------|-------------|
| `npm run start` | Start Spring Boot application |
| `npm run build` | Build Spring Boot application |
| `npm run test` | Run Spring Boot tests |

## 🛠️ Development

### Frontend Development

The Angular application is located in the `client/` directory and follows a modular architecture:

- **Core Module**: Singleton services, guards, interceptors, and core utilities
- **Shared Module**: Reusable components, pipes, directives, and utilities
- **Feature Modules**: Organized by business features (projects, auth, profile, etc.)

### Backend Development

The Spring Boot application follows Clean Architecture principles:

- **Controllers**: Handle HTTP requests and responses
- **Services**: Business logic and application services
- **Repositories**: Data access layer
- **Models**: Domain entities and DTOs

### Configuration

All configuration files are centralized in the `config/` directory:

- **TypeScript**: `tsconfig.json`, `tsconfig.app.json`, `tsconfig.spec.json`
- **ESLint**: `.eslintrc.json`
- **Prettier**: `.prettierrc`
- **EditorConfig**: `.editorconfig`

## 🧪 Testing

### Frontend Testing
```bash
# Run unit tests
npm run test --workspace=client

# Run tests with coverage
npm run test --workspace=client -- --coverage

# Run e2e tests
npm run e2e --workspace=client
```

### Backend Testing
```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Run tests with coverage
mvn jacoco:report
```

## 📚 Documentation

- [NgRx Entity](docs/ngrx-entity.md)
- [Tasks Architecture](docs/tasks-architecture.md)
- [Development Roadmap](docs/DEVELOPMENT_ROADMAP.md)
- [API Documentation](docs/api/README.md)
- [Architecture Guide](docs/architecture/README.md)
- [Angular Documentation Patterns](docs/angular-documentation.md)
- [User Guides and Technical Documentation](docs/user-guides.md)
- [Angular Security](docs/angular-security.md)
- [CI/CD Pipeline and Docker](docs/ci-cd-pipeline.md)
- [Final Testing Strategies](docs/final-testing-strategies.md)
- [Project Handover](docs/project-handover.md)
- [Release Notes](RELEASE_NOTES.md)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

If you encounter any issues or have questions:

1. Check the [documentation](docs/)
2. Search existing [issues](https://github.com/your-username/task-management-platform/issues)
3. Create a new issue with detailed information

---

**Happy Coding! 🎉**
