FROM eclipse-temurin:17-jdk as build
WORKDIR /workspace/app

# gradle 빌드
COPY settings.gradle .
COPY gradlew .
COPY gradle gradle
COPY gateway/build.gradle gateway/
COPY gateway/src gateway/src

RUN chmod +x gradlew
RUN ./gradlew :gateway:bootJar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/app/gateway/build/libs/*.jar app.jar

RUN apt-get update && apt-get install -y curl
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=aws" ,"app.jar"]
