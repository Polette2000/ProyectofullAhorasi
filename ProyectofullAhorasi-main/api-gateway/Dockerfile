# Etapa 1: compila API Gateway con Maven y JDK 21.
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src src
RUN mvn -B clean package -DskipTests

# Etapa 2: ejecuta solo el JAR final con JRE 21.
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
