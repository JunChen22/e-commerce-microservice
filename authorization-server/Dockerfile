FROM adoptopenjdk:16_36-jre-hotspot
EXPOSE 9999
ADD ./target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
