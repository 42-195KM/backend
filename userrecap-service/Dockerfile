FROM eclipse-temurin:17-jdk as build
WORKDIR /workspace/app

# gradle 빌드
COPY settings.gradle .
COPY gradlew .
COPY gradle gradle
COPY userrecap-service/build.gradle userrecap-service/
COPY userrecap-service/src userrecap-service/src
COPY common/build.gradle common/
COPY common/src common/src

RUN chmod +x ./gradlew
RUN ./gradlew :userrecap-service:bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/app/userrecap-service/build/libs/*.jar app.jar

RUN apt-get update && apt-get install -y curl
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=aws" ,"app.jar"]


