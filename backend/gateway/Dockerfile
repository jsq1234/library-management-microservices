# Use the official OpenJDK 17 base image
FROM openjdk:17-slim 

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY target/gateway-0.0.1-SNAPSHOT.jar /app/

# Expose the application port (adjust if needed)
EXPOSE 8080

# Specify the default command to run your application
CMD ["java", "-jar", "gateway-0.0.1-SNAPSHOT.jar"]
