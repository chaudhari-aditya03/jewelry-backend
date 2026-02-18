# ---------- Build Stage ----------
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

RUN apk add --no-cache maven

COPY . .

RUN mvn clean package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
