# syntax=docker/dockerfile:1

FROM openjdk:19-jdk-alpine3.16

ADD /target/SpringTelegramGHActivityBot-0.0.1-SNAPSHOT.jar backend.jar

ENTRYPOINT ["java", "-jar", "backend.jar"]