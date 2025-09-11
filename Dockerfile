# Build stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace/app

# Copy the entire project
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /workspace/app/target/demo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]