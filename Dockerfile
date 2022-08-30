FROM openjdk:11-slim as build

COPY target/accountsms-0.0.1-SNAPSHOT.jar accountsms-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","/accountsms-0.0.1-SNAPSHOT.jar"]
