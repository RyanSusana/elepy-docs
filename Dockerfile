FROM openjdk:8
VOLUME /tmp
ADD /target/elepy-docs-1.0-SNAPSHOT.jar app.jar
EXPOSE 4242
ENTRYPOINT ["java","-jar","/app.jar"]