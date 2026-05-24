# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache dumb-init
RUN addgroup -g 1000 spring && adduser -u 1000 -G spring -s /bin/sh -D spring

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN chown -R spring:spring /app

USER spring:spring

ENV PORT=8080
ENV SERVER_PORT=8080
EXPOSE $PORT

ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "exec java -XX:+UseZGC -Xmx256m -XX:MaxMetaspaceSize=128m -Dserver.port=${PORT:-8080} -jar app.jar"]
