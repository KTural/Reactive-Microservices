FROM openjdk:8-jdk-alpine
RUN apt-get update && apt-get -y install mvn
COPY target/reactive-account.jar /reactive-account.jar
CMD [ "java", "-jar", "/reactive-account.jar"]
