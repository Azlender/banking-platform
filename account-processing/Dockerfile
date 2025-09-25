# Используем актуальный образ JDK
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Копируем собранный jar
COPY build/libs/*.jar app.jar

# Открываем порт
EXPOSE 8081

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
