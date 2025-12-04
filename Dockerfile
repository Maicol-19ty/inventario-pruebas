# Multi-stage build for optimized image size

# Stage 1: Build the application
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# Copy gradle files first for better layer caching
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

# Copy source code
COPY src ./src
COPY config ./config

# Build the application (skip tests and static analysis for faster build)
RUN ./gradlew assemble --no-daemon

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
