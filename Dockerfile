# syntax=docker/dockerfile:1

# --- Build stage ---
FROM eclipse-temurin:21.0.2_13-jdk AS build
WORKDIR /app

# Copy Maven wrapper, Maven config, and pom.xml (use --link for buildkit parallelism)
COPY --link eCard/mvnw .
COPY --link eCard/.mvn .mvn
COPY --link eCard/pom.xml .

# Make mvnw executable and download dependencies (cacheable)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code (after dependencies for better cache)
COPY --link eCard/src ./src

# Build the application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests -B

# --- Runtime stage ---
FROM eclipse-temurin:21.0.2_13-jre
WORKDIR /app

# Create non-root user and group
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Copy built jar from build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Set permissions
RUN chown appuser:appgroup /app/app.jar

USER appuser

EXPOSE 8080

# JVM options: container-aware memory, GC tuning (optional, can be adjusted)
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=80.0", "-XX:+UseContainerSupport", "-jar", "app.jar"]
