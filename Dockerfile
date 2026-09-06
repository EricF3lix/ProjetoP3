
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app


COPY pom.xml .
RUN mvn dependency:go-offline -B


COPY src ./src
RUN mvn package -DskipTests -B


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup


RUN mkdir -p /app/dados /app/relatorios \
    && chown -R appuser:appgroup /app

USER appuser


COPY --from=build /app/target/loja-aluga-de-um-tudo-1.0-SNAPSHOT.jar app.jar


ENTRYPOINT ["java", "-jar", "app.jar"]