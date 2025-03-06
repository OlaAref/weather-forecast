# Stage 1: Build the entire project
FROM maven:3.9.6-eclipse-temurin-21 as builder
WORKDIR /build

# Mount local Maven repository cache and copy POM files
COPY ./pom.xml ./pom.xml
COPY ./libraries/common-lib/pom.xml ./libraries/common-lib/pom.xml
COPY ./services/weather/pom.xml ./services/weather/pom.xml

# Create a directory structure for source files to prevent mvn failure
RUN mkdir -p ./libraries/common-lib/src/main/java && \
    mkdir -p ./services/weather/src/main/java

# Download dependencies using Docker's cache mechanism
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B

# Copy source files
COPY ./libraries/common-lib/src ./libraries/common-lib/src
COPY ./services/weather/src ./services/weather/src

# Build the project using local repository
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/services/weather/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]