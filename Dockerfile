FROM amazoncorretto:17-alpine

COPY ./target/ordika-0.0.1-SNAPSHOT.jar ordika.jar

ENTRYPOINT ["java", "-jar", "ordika.jar"]