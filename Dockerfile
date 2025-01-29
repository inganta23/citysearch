# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build

# Set working directory
WORKDIR /app

# Copy only necessary files to leverage Docker caching
COPY pom.xml .
RUN mvn dependency:go-offline  # Pre-download dependencies for faster builds

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:17.0.1-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from the first stage
COPY --from=build /app/target/citysearch-0.0.1-SNAPSHOT.jar citysearch.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "citysearch.jar"]
