# Compilation
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Telechargement des dependances dans un autre couche
RUN mvn -B dependency:go-offline
COPY src ./src
# Run des tests
RUN mvn -B package -DskipTests

# Execution
FROM eclipse-temurin:21-jre
WORKDIR /app
# Ne pas tourner en root dans le conteneur
RUN groupadd -r app && useradd -r -g app app
COPY --from=build /app/target/*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "app.jar" ]