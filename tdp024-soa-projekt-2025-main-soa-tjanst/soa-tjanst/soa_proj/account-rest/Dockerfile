FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY ../.. .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-focal
 
WORKDIR /app
 
COPY --from=builder /app/account-rest/target/rest-1.0.jar app.jar

COPY --from=builder /app/account-rest/src/main/resources/application.properties /app/resources/application.properties

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
