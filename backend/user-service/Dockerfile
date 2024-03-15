# Use the official OpenJDK 17 base image
FROM openjdk:17-slim 

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY target/user-service-0.0.1-SNAPSHOT.jar /app/

# Expose the application port (adjust if needed)
EXPOSE 8081

# Specify the default command to run your application
CMD ["java", "-jar", "user-service-0.0.1-SNAPSHOT.jar"]
