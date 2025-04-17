FROM gradle:8.5-jdk17 AS builder
WORKDIR /build
COPY . .
RUN gradle build --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /build/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]