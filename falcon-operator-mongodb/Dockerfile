FROM openjdk:8-jdk-alpine
COPY target/falcon-operator-mongodb-2.0.0-SNAPSHOT.jar .
EXPOSE 8080
ENTRYPOINT ["java","-jar","/falcon-operator-mongodb-2.0.0-SNAPSHOT.jar"]