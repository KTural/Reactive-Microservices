FROM openjdk:8-jre-alpine
ADD target/reactive-account.jar reactive-account.jar
EXPOSE 8085
ENTRYPOINT [ "java", "-jar", "reactive-account.jar"]