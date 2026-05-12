# ─────────────────────────────────────────────
# Stage 1: Build
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build

COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -q

COPY src src
RUN ./mvnw package -DskipTests -q

# ─────────────────────────────────────────────
# Stage 2: Runtime
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S ferret && adduser -S ferret -G ferret

COPY --from=builder /build/target/*.jar app.jar
RUN chown ferret:ferret app.jar

USER ferret
EXPOSE 8080

# JVM tuning para t3.small (2 vCPU / 2 GB RAM)
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-Xms256m", \
  "-Xmx512m", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]