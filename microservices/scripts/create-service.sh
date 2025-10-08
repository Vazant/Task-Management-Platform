#!/bin/bash

# Script to create a new microservice
# Usage: ./create-service.sh <service-name> <service-display-name> <service-description>

if [ $# -ne 3 ]; then
    echo "Usage: $0 <service-name> <service-display-name> <service-description>"
    echo "Example: $0 time-service 'Time Service' 'Time Tracking Microservice'"
    exit 1
fi

SERVICE_NAME=$1
SERVICE_DISPLAY_NAME=$2
SERVICE_DESCRIPTION=$3

echo "Creating microservice: $SERVICE_NAME"
echo "Display name: $SERVICE_DISPLAY_NAME"
echo "Description: $SERVICE_DESCRIPTION"

# Create service directory
mkdir -p "services/$SERVICE_NAME"

# Copy Dockerfile template
cp "shared/common/Dockerfile.template" "services/$SERVICE_NAME/Dockerfile"

# Copy and customize pom.xml template
cp "shared/common/pom.xml.template" "services/$SERVICE_NAME/pom.xml"

# Replace placeholders in pom.xml
sed -i.bak "s/{{SERVICE_NAME}}/$SERVICE_NAME/g" "services/$SERVICE_NAME/pom.xml"
sed -i.bak "s/{{SERVICE_DISPLAY_NAME}}/$SERVICE_DISPLAY_NAME/g" "services/$SERVICE_NAME/pom.xml"
sed -i.bak "s/{{SERVICE_DESCRIPTION}}/$SERVICE_DESCRIPTION/g" "services/$SERVICE_NAME/pom.xml"

# Remove backup file
rm "services/$SERVICE_NAME/pom.xml.bak"

# Create basic source structure
mkdir -p "services/$SERVICE_NAME/src/main/java/com/taskboard/$SERVICE_NAME"
mkdir -p "services/$SERVICE_NAME/src/main/resources"
mkdir -p "services/$SERVICE_NAME/src/test/java/com/taskboard/$SERVICE_NAME"

# Create application.properties
cat > "services/$SERVICE_NAME/src/main/resources/application.properties" << EOF
# $SERVICE_DISPLAY_NAME Configuration
spring.application.name=$SERVICE_NAME
server.port=8080

# Database Configuration
spring.datasource.url=\${DATABASE_URL:jdbc:postgresql://localhost:5432/taskboard}
spring.datasource.username=\${DATABASE_USERNAME:taskboard_user}
spring.datasource.password=\${DATABASE_PASSWORD:taskboard_password}

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Redis Configuration
spring.data.redis.host=\${REDIS_HOST:localhost}
spring.data.redis.port=\${REDIS_PORT:6379}

# Kafka Configuration
spring.kafka.bootstrap-servers=\${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always

# Logging Configuration
logging.level.com.taskboard.$SERVICE_NAME=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
EOF

# Create main application class
cat > "services/$SERVICE_NAME/src/main/java/com/taskboard/$SERVICE_NAME/${SERVICE_NAME^}Application.java" << EOF
package com.taskboard.$SERVICE_NAME;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ${SERVICE_NAME^}Application {

    public static void main(String[] args) {
        SpringApplication.run(${SERVICE_NAME^}Application.class, args);
    }
}
EOF

echo "✅ Microservice '$SERVICE_NAME' created successfully!"
echo ""
echo "Next steps:"
echo "1. cd services/$SERVICE_NAME"
echo "2. mvn clean install"
echo "3. Add your business logic"
echo "4. Update docker-compose.yml to include the new service"
echo "5. Update Kubernetes manifests if needed"