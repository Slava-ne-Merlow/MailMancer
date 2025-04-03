# Этап 1: Сборка приложения (Builder)
FROM eclipse-temurin:17-jdk-jammy as builder



WORKDIR /app

# Копируем только файлы для зависимостей (для кэширования)
COPY build.gradle.kts settings.gradle.kts gradlew /app/
COPY gradle /app/gradle

# Скачиваем зависимости (используем кэш Docker)
RUN ./gradlew --no-daemon dependencies

# Копируем исходный код
COPY src /app/src

# Собираем приложение
RUN ./gradlew --no-daemon bootJar

# Этап 2: Запуск приложения
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /app/build/libs/*.jar /app/application.jar

# Команда запуска
CMD ["java", "-jar", "application.jar"]

# Открываем порт
EXPOSE 8080
