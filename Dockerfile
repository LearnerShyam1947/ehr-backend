# Stage 1: Build the application
FROM maven:3.9.11-eclipse-temurin-21 AS BUILD

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests

# Stage 2: Create a lightweight image
FROM openjdk:21-rc-slim-bullseye

WORKDIR /app
COPY --from=BUILD /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]