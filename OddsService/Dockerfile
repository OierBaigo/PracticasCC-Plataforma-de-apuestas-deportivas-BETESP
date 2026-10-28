FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

RUN chmod +x mvnw
RUN ./mvnw -B dependency:go-offline

COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:25-jre

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

HEALTHCHECK --interval=10s --timeout=3s --start-period=60s --retries=5 \
  CMD curl -fsS http://localhost:8083/actuator/health || exit 1

RUN useradd --system --uid 10001 appuser

COPY --from=build /app/target/OddsService-0.0.1-SNAPSHOT.jar app.jar

USER 10001

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
