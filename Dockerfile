# ─────────────────────────────────────────────
# Stage 1: Build
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build

# Copy Maven wrapper and POM first (layer cache for dependencies)
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Fix permissions on Maven wrapper
RUN chmod +x mvnw

# Download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -q

# Copy source and build the fat JAR
COPY src src
RUN ./mvnw package -DskipTests -q

# ─────────────────────────────────────────────
# Stage 2: Runtime
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Create a non-root user for security
RUN addgroup -S ferret && adduser -S ferret -G ferret

# Copy the built JAR from the builder stage
COPY --from=builder /build/target/*.jar app.jar

# Give ownership to the non-root user
RUN chown ferret:ferret app.jar

USER ferret

# Expose the application port
EXPOSE 8080

# JVM tuning for t3.small (2 vCPU / 2 GB RAM)
# -XX:+UseContainerSupport  → respects Docker memory limits
# -Xms256m / -Xmx512m      → leaves ~1.2 GB for OS + Postgres
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-Xms256m", \
  "-Xmx512m", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
