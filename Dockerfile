FROM openjdk:17-jdk-alpine
COPY target/MSTxFleet-DBS-Postgis-0.0.1-SNAPSHOT.jar MSTxFleet-DBS-Postgis.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/MSTxFleet-DBS-Postgis.jar"]