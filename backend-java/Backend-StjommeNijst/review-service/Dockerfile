# Gebruik een OpenJDK image als basis
FROM openjdk:17-jdk-slim

# Zet de werkdirectory in de container
WORKDIR /app

# Kopieer het JAR-bestand van je project naar de container
COPY target/review-service-0.0.1-SNAPSHOT.jar /app/reviewservice.jar

# Voer de microservice uit
CMD ["java", "-jar", "reviewservice.jar"]

# Exposeer de poort die de microservice gebruikt
EXPOSE 8087
