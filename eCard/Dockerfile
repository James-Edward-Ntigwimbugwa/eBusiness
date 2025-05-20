# 1. Build stage: compile & package the app
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy source code
COPY . .

# Use -DskipTests to bypass test execution (tests still compile)
RUN mvn clean package -DskipTests

# 2. Runtime stage: minimal JRE image
FROM eclipse-temurin:21.0.2_13-jdk-alpine
WORKDIR /app

# Copy the fat JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose default port (if your app listens on 8080, adjust as needed)
EXPOSE 8080

# Launch the application
ENTRYPOINT ["java", "-jar", "app.jar"]
