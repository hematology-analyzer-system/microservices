# Multi-stage Dockerfile for Spring Boot Patient Service
# Stage 1: Build stage
FROM maven:3.9.4-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and pre-fetch dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the app
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime

# Install dumb-init and wget for healthcheck
RUN apk add --no-cache dumb-init wget

# Create non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# Copy built JAR from build stage
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

USER appuser

# Expose app port
EXPOSE 8081

# Health check endpoint
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/patient/actuator/health || exit 1

ENTRYPOINT ["dumb-init", "--"]
CMD ["java", \
     "-server", \
     "-XX:+UseContainerSupport", \
     "-XX:MaxRAMPercentage=75.0", \
     "-XX:+UseG1GC", \
     "-XX:+UseStringDeduplication", \
     "-XX:+OptimizeStringConcat", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-jar", "app.jar"]
