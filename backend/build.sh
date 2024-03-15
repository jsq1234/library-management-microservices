#!/bin/bash

echo "Compiling the project"
./mvnw clean package -DskipTests -T 12

dockerfiles=("./gateway" "./book-service" "./user-service")
imagename=("gateway-image" "book-service-image" "user-service-image")
pids=()

for ((i=0; i<${#dockerfiles[@]}; i++)); do

    dockerfile="${dockerfiles[i]}"
    name="${imagename[i]}"

    docker build -t "$name" "$dockerfile" &
    
    pids+=($!)

done

for pid in "${pids[@]}"; do
    wait $pid
done


echo "All docker images are built."
