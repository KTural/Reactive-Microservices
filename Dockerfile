FROM openjdk:8
COPY target/reactive-account.jar /reactive-account.jar
CMD [ "java", "-jar", "/reactive-account.jar"]
