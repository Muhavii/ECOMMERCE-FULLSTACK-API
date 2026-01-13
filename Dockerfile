FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/ecommerce-api-1.0.0.jar app.jar
EXPOSE 10000
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
