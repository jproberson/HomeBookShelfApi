# Use an official OpenJDK image as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file from the host to the container
COPY build/libs/*.jar app.jar

# Expose port 8080 for the Spring Boot app
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
