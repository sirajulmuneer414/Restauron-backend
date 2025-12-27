# --- Stage 1: Build the Application ---
# We use a Maven image to compile the code
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the dependency file first (for caching optimization)
COPY pom.xml .
# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline

# Copy the actual source code
COPY src ./src

# Build the application (Skipping tests to speed up container builds)
RUN mvn clean package -DskipTests

# --- Stage 2: Create the Runtime Image ---
# We use a lightweight Alpine JRE just for running the app
FROM eclipse-temurin:17-jre-alpine

WORKDIR /restauron-backend

RUN mkdir -p /restauron-backend/logs


# Copy the JAR file from the 'build' stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8081

# The command to start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
