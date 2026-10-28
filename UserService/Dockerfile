# Etapa 1: compilar la aplicación con Maven y Java 25
FROM maven:3.10.0-eclipse-temurin-25 AS build

WORKDIR /app

# Copiar primero la configuración para aprovechar la caché de Docker
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

RUN chmod +x mvnw

# Descargar dependencias antes de copiar el código fuente
RUN ./mvnw -B dependency:go-offline

# Copiar el código y generar el JAR
COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

# Etapa 2: ejecutar con una imagen Java más ligera
FROM eclipse-temurin:25-jre

WORKDIR /app

#Instalar curl para la verificación de salud
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Verificación de salud para asegurarse de que el servicio esté activo
HEALTHCHECK --interval=10s --timeout=3s --start-period=60s --retries=5 \
  CMD curl -fsS http://localhost:8081/actuator/health || exit 1

# Crear un usuario sin privilegios de administrador
RUN useradd --system --uid 10001 appuser

# Copiar únicamente el JAR generado
COPY --from=build /app/target/UserService-0.0.1-SNAPSHOT.jar app.jar

USER 10001

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
