#!/bin/bash

# Read the .env.cognito file line by line
while IFS='=' read -r key value; do
    # Append each key-value pair as environment variable assignment
    args+=" $key=$value "
done < ../.env.cognito

# Run the Maven test command with the specified environment variables
 eval "$args ./mvnw test"

