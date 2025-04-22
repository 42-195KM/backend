FROM eclipse-temurin:17-jdk as build
WORKDIR /workspace/app

# gradle 빌드
COPY settings.gradle .
COPY gradlew .
COPY gradle gradle
COPY server/build.gradle server/
COPY server/src server/src

RUN chmod +x gradlew
RUN ./gradlew :server:bootJar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/app/server/build/libs/*.jar app.jar

RUN apt-get update && apt-get install -y curl
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
