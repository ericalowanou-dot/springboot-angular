# Étape 1 : Build de l'application avec Gradle
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src
RUN chmod +x gradlew
RUN ./gradlew build -x test --no-daemon

# Étape 2 : Image finale, plus légère, juste pour exécuter le jar
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
