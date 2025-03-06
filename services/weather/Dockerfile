# Stage 1: Build the entire project
FROM maven:3.9.6-eclipse-temurin-21 as builder
WORKDIR /build

COPY ../../ ./

# Build the project
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/services/weather/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]