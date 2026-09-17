FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven && mvn clean package -DskipTests -Dspring-boot.build-image.skip=true && ls -la target/

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/sistema-inventario-*.jar app.jar
RUN ls -la app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
