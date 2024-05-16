FROM openjdk:17-jdk-slim-buster
COPY /target/medicare.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
#ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/app.jar"]