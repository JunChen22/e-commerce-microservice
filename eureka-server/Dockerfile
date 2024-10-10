FROM eclipse-temurin:17-jre-alpine
EXPOSE 8761
ADD ./target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
