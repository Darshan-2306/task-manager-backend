FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy JAR from Maven target folder
COPY target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
