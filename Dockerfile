FROM 8u191-jdk-alpine3.9

COPY BeLoyal-1.0-jar-with-dependencies.jar /app.jar

CMD ["/usr/bin/java", "-XX:-UseContainerSupport", "-jar", "/app.jar"]