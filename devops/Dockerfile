FROM maven:3.6.3-openjdk-11 as builder
COPY . /src
WORKDIR /src
RUN mvn clean install -DskipTests

FROM adoptopenjdk/openjdk11:latest
COPY --from=builder /src/target/template-manager-0.0.1-SNAPSHOT.jar template-manager.jar
EXPOSE 8080
EXPOSE 8000
ENTRYPOINT ["java", "agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000", "-jar", "template-manager.jar"]