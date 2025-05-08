FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew /app/
COPY gradle /app/gradle

RUN ./gradlew --no-daemon dependencies

COPY src /app/src

RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/application.jar

CMD ["java", "-jar", "application.jar"]

EXPOSE 8080
