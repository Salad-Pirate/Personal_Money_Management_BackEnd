# ---------- Build stage ----------
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy gradle wrapper and build config ก่อน
COPY gradlew .
COPY gradlew.bat .
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./

# Grant run gradlew (For Linux)
RUN chmod +x gradlew

# Download dependency (cache)
RUN ./gradlew --no-daemon dependencies || return 0

# Copy src
COPY src ./src

# Build jar (skip test เพื่อให้ build เร็วขึ้น)
RUN ./gradlew clean bootJar --no-daemon -x test

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# copy jar build
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Create user
RUN useradd -ms /bin/bash appuser
USER appuser

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
