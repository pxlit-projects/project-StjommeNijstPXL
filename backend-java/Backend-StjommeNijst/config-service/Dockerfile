# Gebruik een OpenJDK image als basis
FROM openjdk:17-jdk-slim

# Zet de werkdirectory in de container
WORKDIR /app

# Kopieer het JAR-bestand van je project naar de container
COPY ./target/config-service-0.0.1-SNAPSHOT.jar /app/configservice.jar

# Voer de microservice uit
CMD ["java", "-jar", "configservice.jar", "--spring.profiles.active=native"]

# Exposeer de poort die de microservice gebruikt
EXPOSE 8088
