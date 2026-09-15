FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/ms-pedidos360-notify-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]