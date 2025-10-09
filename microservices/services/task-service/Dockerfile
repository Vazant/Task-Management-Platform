# Multi-stage build for Task Service
FROM maven:3.9.4-openjdk-21-slim AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Download dependencies (cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM openjdk:21-jre-slim AS runtime

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Install necessary packages and clean up
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        curl \
        dumb-init && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get clean && \
    apt-get autoremove -y

# Set working directory
WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs && \
    chown -R appuser:appuser /app

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check with proper timeout and retry logic
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${SERVER_PORT:-8080}${ACTUATOR_BASE_PATH:-/actuator}/health || exit 1

# Use dumb-init for proper signal handling
ENTRYPOINT ["dumb-init", "--"]

# Run the application with JVM optimizations for containers
CMD ["java", \
     "-XX:+UseContainerSupport", \
     "-XX:MaxRAMPercentage=75.0", \
     "-XX:+UseG1GC", \
     "-XX:MaxGCPauseMillis=200", \
     "-XX:+UseStringDeduplication", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker}", \
     "-jar", "app.jar"]

