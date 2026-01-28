# stage 1 create build file by gradle
FROM gradle:jdk21-jammy AS builder
WORKDIR /app
COPY . .

RUN chmod +x ./gradlew
# build source code
RUN ./gradlew clean bootJar -x test

# stage 2
FROM openjdk:21-jdk-slim
WORKDIR /app
# copy build file from stage 1
COPY --from=builder /app/build/libs/*.jar app.jar

# open 8080
EXPOSE 8080

# Runnnnnnnnnnnnnnnnn
ENTRYPOINT ["java", "-jar", "app.jar"]