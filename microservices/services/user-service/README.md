# User Service

A comprehensive microservice for user management in the Task Management Platform, built with Spring Boot and following Domain-Driven Design principles.

## 🚀 Overview

The User Service is responsible for managing user accounts, authentication, authorization, and user-related statistics in the Task Management Platform. It provides a robust, scalable, and secure foundation for user management with comprehensive event processing capabilities.

## 🏗️ Architecture

### Hexagonal Architecture

The service follows the Hexagonal Architecture pattern, ensuring clear separation of concerns and high testability:

```
┌─────────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                     │
├─────────────────────────────────────────────────────────────┤
│  Web Controllers  │  Messaging  │  Database  │  Security   │
├─────────────────────────────────────────────────────────────┤
│                    Application Layer                        │
├─────────────────────────────────────────────────────────────┤
│  Use Cases  │  DTOs  │  Services  │  Event Handlers        │
├─────────────────────────────────────────────────────────────┤
│                      Domain Layer                           │
├─────────────────────────────────────────────────────────────┤
│  Entities  │  Value Objects  │  Repositories  │  Events    │
└─────────────────────────────────────────────────────────────┘
```

### Key Components

- **Domain Layer**: Core business logic and entities
- **Application Layer**: Use cases and application services
- **Infrastructure Layer**: External concerns (database, messaging, web)
- **Event Processing**: Comprehensive event handling with deduplication and retry mechanisms

## 🛠️ Technology Stack

### Core Technologies
- **Java 17**: Modern Java features and performance
- **Spring Boot 3.x**: Rapid application development framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data access layer
- **PostgreSQL**: Primary database
- **Flyway**: Database migration management

### Additional Libraries
- **MapStruct**: Object mapping
- **JWT**: Token-based authentication
- **BCrypt**: Password hashing
- **Testcontainers**: Integration testing
- **WireMock**: API mocking
- **OpenAPI/Swagger**: API documentation

## 📋 Features

### User Management
- ✅ User registration and authentication
- ✅ JWT-based authentication
- ✅ Role-based authorization (USER, ADMIN)
- ✅ User profile management
- ✅ Password management
- ✅ User statistics tracking

### Event Processing
- ✅ Incoming event processing from other services
- ✅ Event deduplication
- ✅ Retry mechanisms with exponential backoff
- ✅ Event validation and schema evolution
- ✅ Dead letter queue support
- ✅ Comprehensive error handling

### Security
- ✅ JWT token authentication
- ✅ Role-based access control
- ✅ Password encryption with BCrypt
- ✅ CSRF protection
- ✅ Input validation
- ✅ SQL injection prevention

### Testing
- ✅ Unit tests (95%+ coverage)
- ✅ Integration tests with Testcontainers
- ✅ Contract tests for API
- ✅ End-to-end tests
- ✅ Performance and load tests
- ✅ Security tests

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 13+
- Docker (for integration tests)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd user-service
   ```

2. **Configure database**
   ```bash
   # Create PostgreSQL database
   createdb user_service
   
   # Update application.properties with your database credentials
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Health Check: http://localhost:8080/actuator/health

### Docker Support

```bash
# Build Docker image
docker build -t user-service .

# Run with Docker Compose
docker-compose up -d
```

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123",
  "firstName": "New",
  "lastName": "User"
}
```

#### Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "newuser",
  "password": "password123"
}
```

#### Refresh Token
```http
POST /api/v1/auth/refresh
Authorization: Bearer <token>
```

### User Management Endpoints

#### Get Current User Profile
```http
GET /api/v1/users/profile
Authorization: Bearer <token>
```

#### Update User Profile
```http
PUT /api/v1/users/profile
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "Updated",
  "lastName": "Name",
  "email": "updated@example.com"
}
```

#### Change Password
```http
PUT /api/v1/users/password
Authorization: Bearer <token>
Content-Type: application/json

{
  "currentPassword": "oldPassword123",
  "newPassword": "newPassword123"
}
```

#### Get All Users (Admin Only)
```http
GET /api/v1/users
Authorization: Bearer <admin-token>
```

#### Create User (Admin Only)
```http
POST /api/v1/users
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "username": "adminuser",
  "email": "admin@example.com",
  "password": "password123",
  "firstName": "Admin",
  "lastName": "User",
  "role": "ADMIN"
}
```

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run unit tests only
mvn test -Dtest="*Test"

# Run integration tests only
mvn test -Dtest="*IntegrationTest"

# Run performance tests
mvn test -Dtest="*PerformanceTest"

# Run with coverage
mvn test jacoco:report
```

### Test Categories

- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test component interactions with real dependencies
- **Contract Tests**: Verify API contracts and responses
- **E2E Tests**: Test complete user workflows
- **Performance Tests**: Validate performance under load
- **Security Tests**: Verify security measures

### Test Coverage

The service maintains high test coverage:
- **Unit Tests**: 95%+ coverage
- **Integration Tests**: 90%+ coverage
- **E2E Tests**: 85%+ coverage

## 🔧 Configuration

### Application Properties

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/user_service
spring.datasource.username=user
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

# JWT Configuration
app.jwt.secret=your-secret-key
app.jwt.expiration=3600000

# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=user-service
spring.kafka.consumer.auto-offset-reset=earliest

# Logging Configuration
logging.level.com.taskboard.userservice=INFO
logging.level.org.springframework.security=DEBUG
```

### Environment Variables

```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=user_service
DB_USERNAME=user
DB_PASSWORD=password

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=3600000

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_CONSUMER_GROUP_ID=user-service

# Server
SERVER_PORT=8080
```

## 📊 Monitoring and Observability

### Health Checks

The service provides comprehensive health checks:

```http
GET /actuator/health
```

### Metrics

Available metrics include:
- Request count and duration
- Database connection pool status
- JVM memory and GC metrics
- Custom business metrics

### Logging

Structured logging with correlation IDs for request tracing:

```json
{
  "timestamp": "2024-01-01T10:00:00.000Z",
  "level": "INFO",
  "logger": "com.taskboard.userservice.application.service.UserService",
  "message": "User created successfully",
  "correlationId": "abc123",
  "userId": 1,
  "username": "newuser"
}
```

## 🔒 Security

### Authentication

- JWT-based authentication
- Token expiration and refresh
- Secure password storage with BCrypt

### Authorization

- Role-based access control (RBAC)
- Method-level security
- Resource-level permissions

### Data Protection

- Input validation and sanitization
- SQL injection prevention
- XSS protection
- CSRF protection

## 🚀 Deployment

### Production Deployment

1. **Build the application**
   ```bash
   mvn clean package -Pproduction
   ```

2. **Run database migrations**
   ```bash
   java -jar target/user-service.jar --spring.profiles.active=production
   ```

3. **Deploy to container platform**
   ```bash
   docker build -t user-service:latest .
   docker run -d -p 8080:8080 user-service:latest
   ```

### Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
    spec:
      containers:
      - name: user-service
        image: user-service:latest
        ports:
        - containerPort: 8080
        env:
        - name: DB_HOST
          value: "postgres-service"
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: user-service-secrets
              key: jwt-secret
```

## 🔄 Event Processing

### Incoming Events

The service processes events from other microservices:

- **Task Events**: Task creation, updates, deletion
- **Project Events**: Project creation, member management
- **User Events**: User registration, updates, deletion

### Event Processing Pipeline

```
Kafka Message → Validation → Deduplication → Handler → Retry → Statistics → Notification
```

### Event Types

#### Task Events
- `task.created`: New task created
- `task.updated`: Task updated
- `task.deleted`: Task deleted

#### Project Events
- `project.created`: New project created
- `project.member.added`: User added to project

### Event Processing Features

- **Deduplication**: Prevents duplicate event processing
- **Retry Mechanism**: Exponential backoff for failed events
- **Dead Letter Queue**: Handles permanently failed events
- **Schema Evolution**: Supports event schema changes
- **Monitoring**: Comprehensive event processing metrics

## 🐛 Troubleshooting

### Common Issues

#### Database Connection Issues
```bash
# Check database connectivity
telnet localhost 5432

# Verify database credentials
psql -h localhost -U user -d user_service
```

#### JWT Token Issues
```bash
# Verify JWT secret configuration
echo $JWT_SECRET

# Check token expiration
jwt decode <token>
```

#### Kafka Connection Issues
```bash
# Check Kafka connectivity
telnet localhost 9092

# Verify Kafka topics
kafka-topics --list --bootstrap-server localhost:9092
```

### Debug Mode

Enable debug logging:

```properties
logging.level.com.taskboard.userservice=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
```

### Performance Issues

Monitor key metrics:
- Response times
- Database connection pool
- JVM memory usage
- Event processing latency

## 🤝 Contributing

### Development Setup

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Make your changes**
4. **Run tests**
   ```bash
   mvn test
   ```
5. **Commit your changes**
   ```bash
   git commit -m "Add your feature"
   ```
6. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```
7. **Create a Pull Request**

### Code Standards

- Follow Java coding conventions
- Write comprehensive tests
- Update documentation
- Use meaningful commit messages
- Follow the existing architecture patterns

### Pull Request Process

1. Ensure all tests pass
2. Update documentation if needed
3. Add tests for new functionality
4. Follow the existing code style
5. Provide a clear description of changes

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

### Getting Help

- **Documentation**: Check this README and API documentation
- **Issues**: Create an issue in the repository
- **Discussions**: Use GitHub Discussions for questions
- **Email**: Contact the development team

### Reporting Bugs

When reporting bugs, please include:
- Steps to reproduce
- Expected behavior
- Actual behavior
- Environment details
- Log files (if applicable)

## 🗺️ Roadmap

### Upcoming Features

- [ ] Advanced user analytics
- [ ] Multi-factor authentication
- [ ] User preferences management
- [ ] Advanced event processing
- [ ] GraphQL API support
- [ ] Advanced monitoring and alerting

### Version History

- **v1.0.0**: Initial release with core functionality
- **v1.1.0**: Added event processing capabilities
- **v1.2.0**: Enhanced security and monitoring
- **v2.0.0**: Planned major architecture improvements

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- PostgreSQL team for the robust database
- All contributors and maintainers
- The open-source community

---

**User Service** - Built with ❤️ for the Task Management Platform
