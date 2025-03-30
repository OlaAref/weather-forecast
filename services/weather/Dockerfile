# Stage 1: Build the entire project
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

COPY . .

# Download dependencies using Docker's cache mechanism
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B

# Build the project using local repository
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/services/weather/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]