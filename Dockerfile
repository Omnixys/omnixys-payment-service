# syntax=docker/dockerfile:1.14.0

ARG JAVA_VERSION=23
ARG APP_NAME=payment
ARG APP_VERSION=2025.05.16

# ---------------------------------------------------------------------------------------
# Stage 1: builder (Gradle Build)
# ---------------------------------------------------------------------------------------
FROM azul/zulu-openjdk:${JAVA_VERSION} AS builder

ARG APP_NAME
ARG APP_VERSION
WORKDIR /source

# Kopiere Gradle-Dateien
COPY build.gradle.kts gradle.properties gradlew settings.gradle.kts ./
COPY gradle ./gradle
COPY src ./src

# Gradle Build – JAR erstellen und Schichten extrahieren
RUN ./gradlew --no-configuration-cache --no-daemon --no-watch-fs bootJar
RUN JAR_FILE=$(ls ./build/libs/*.jar | grep -v plain | head -n 1) && \
    echo "Using JAR: $JAR_FILE" && \
    java -Djarmode=layertools -jar "$JAR_FILE" extract


# ---------------------------------------------------------------------------------------
# Stage 2: final (Produktions-Image mit JRE)
# ---------------------------------------------------------------------------------------
FROM azul/zulu-openjdk:${JAVA_VERSION}-jre AS final

ARG APP_NAME
ARG APP_VERSION
LABEL org.opencontainers.image.title="${APP_NAME}" \
      org.opencontainers.image.description="Microservice ${APP_NAME} v1 mit Basis-Image Azul Zulu und Ubuntu Jammy" \
      org.opencontainers.image.version="${APP_VERSION}" \
      org.opencontainers.image.licenses="GPL-3.0-or-later" \
      org.opencontainers.image.vendor="Omnixys" \
      org.opencontainers.image.authors="caleb.gyamfi@omnixys.com" \
      org.opencontainers.image.base.name="azul/zulu-openjdk:LATEST_VERSION-jre"

WORKDIR /workspace

# Installiere systemabhängige Pakete, erstelle User, Berechtigungen
RUN apt-get update && \
    apt-get upgrade --yes && \
    apt-get install --no-install-recommends --yes dumb-init=1.2.5-2 wget && \
    apt-get autoremove -y && \
    apt-get clean -y && \
    rm -rf /var/lib/apt/lists/* /tmp/* && \
    groupadd --gid 1000 app && \
    useradd --uid 1000 --gid app --no-create-home app && \
    chown -R app:app /workspace

USER app

# Kopiere die extrahierten Schichten, Konfiguration und Anwendung
COPY --from=builder --chown=app:app /source/dependencies/ /source/spring-boot-loader/ /source/application/ ./

EXPOSE 8080

# Healthcheck für das Mikroservice – prüft, ob der Service bereit ist
HEALTHCHECK --interval=30s --timeout=3s --retries=1 \
    CMD wget -qO- --no-check-certificate https://localhost:8080/actuator/health/ | grep UP || exit 1

# Starte die Spring Boot Anwendung mit dem JAR-Launcher
ENTRYPOINT ["dumb-init", "java", "--enable-preview", "org.springframework.boot.loader.launch.JarLauncher"]

