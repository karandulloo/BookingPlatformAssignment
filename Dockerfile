# Use Java 17 and Maven to build and run the app
FROM maven:3.9-eclipse-temurin-17
WORKDIR /app

# Copy project files and build the JAR
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests -q

# Expose the port the app runs on
EXPOSE 8080

# Run the JAR
CMD ["sh", "-c", "java -jar target/*.jar"]
