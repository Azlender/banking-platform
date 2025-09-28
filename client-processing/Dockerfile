# Используем JDK 21
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Копируем собранный jar
COPY build/libs/*.jar app.jar

# Открываем порт приложения
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
