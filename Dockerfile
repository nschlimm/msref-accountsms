FROM openjdk:11-slim as build

COPY target/msref-accountsms-0.0.1-SNAPSHOT.jar msref-accountsms-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","/msref-accountsms-0.0.1-SNAPSHOT.jar"]
