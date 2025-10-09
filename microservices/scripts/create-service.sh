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

# Copy configuration files
cp "shared/config/application.yml" "services/$SERVICE_NAME/src/main/resources/"
cp "shared/config/application-docker.yml" "services/$SERVICE_NAME/src/main/resources/"
cp "shared/config/application-kubernetes.yml" "services/$SERVICE_NAME/src/main/resources/"
cp "shared/config/application-prod.yml" "services/$SERVICE_NAME/src/main/resources/"

# Create service-specific application.yml
cat > "services/$SERVICE_NAME/src/main/resources/application-local.yml" << EOF
# $SERVICE_DISPLAY_NAME Local Configuration
spring:
  application:
    name: $SERVICE_NAME
  profiles:
    active: local

# Service URLs for local development
service:
  urls:
    user-service: http://localhost:8081
    task-service: http://localhost:8082
    project-service: http://localhost:8083
    time-service: http://localhost:8084
    analytics-service: http://localhost:8085
    notification-service: http://localhost:8086
    search-service: http://localhost:8087
    file-service: http://localhost:8088
    gateway-service: http://localhost:8000

# External Services for local development
external:
  services:
    prometheus: http://localhost:9090
    jaeger: http://localhost:16686
    kafka-ui: http://localhost:8080
    grafana: http://localhost:3000
EOF

# Create main application class
SERVICE_CLASS_NAME=$(echo $SERVICE_NAME | sed 's/-\([a-z]\)/\U\1/g' | sed 's/^\([a-z]\)/\U\1/' | sed 's/-//g')
cat > "services/$SERVICE_NAME/src/main/java/com/taskboard/$SERVICE_NAME/${SERVICE_CLASS_NAME}Application.java" << EOF
package com.taskboard.$SERVICE_NAME;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ${SERVICE_CLASS_NAME}Application {

    public static void main(String[] args) {
        SpringApplication.run(${SERVICE_CLASS_NAME}Application.class, args);
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