# Use the official OpenJDK 17 base image
FROM openjdk:17-slim 

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY target/book-service-0.0.1-SNAPSHOT.jar /app/

# Expose the application port (adjust if needed)
EXPOSE 8082
# Install Dockerize
RUN apt-get update && apt-get install -y wget && \
    wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz && \
    tar -C /usr/local/bin -xzvf dockerize-linux-amd64-v0.6.1.tar.gz && \
    rm dockerize-linux-amd64-v0.6.1.tar.gz

CMD ["dockerize", "-wait", "tcp://book-database:3306", "-timeout", "30s", "java", "-jar",  "book-service-0.0.1-SNAPSHOT.jar"]
