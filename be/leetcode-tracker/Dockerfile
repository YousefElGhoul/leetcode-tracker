FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml mvnw ./
COPY .mvn ./.mvn
RUN ./mvnw --batch-mode dependency:go-offline

COPY src ./src
RUN ./mvnw --batch-mode package -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache dumb-init \
    && addgroup -g 1000 spring \
    && adduser -u 1000 -G spring -s /bin/sh -D spring

WORKDIR /app
COPY --chown=spring:spring --from=build /app/target/leetcode-tracker-0.0.1-SNAPSHOT.jar app.jar

USER spring:spring

ENV PORT=8080
EXPOSE $PORT

ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "exec java -XX:+UseZGC -Xmx256m -XX:MaxMetaspaceSize=128m -Dserver.port=${PORT:-8080} -jar app.jar"]
