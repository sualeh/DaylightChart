# Build stage
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -pl daylightchart-web -am package -DskipTests --no-transfer-progress

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/daylightchart-web/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
