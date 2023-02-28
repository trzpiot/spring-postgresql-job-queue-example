FROM eclipse-temurin:17-jre-alpine
COPY build/libs/postgresql-job-queue-example-0.0.1-SNAPSHOT.jar app.jar

RUN apk add --no-cache py3-img2pdf

ENTRYPOINT ["java", "-jar", "app.jar"]