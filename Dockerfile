FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY gradlew build.gradle settings.gradle /workspace/
COPY gradle /workspace/gradle
COPY src /workspace/src

RUN chmod +x ./gradlew \
  && ./gradlew bootJar -x test \
  && cp "$(ls build/libs/*.jar | grep -v plain | head -n 1)" /workspace/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/app.jar /app/app.jar
COPY dummy.md /app/dummy.md

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
