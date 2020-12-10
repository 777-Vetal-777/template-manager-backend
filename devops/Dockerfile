FROM maven:3.6.3-openjdk-11 as builder
COPY . /src
WORKDIR /src
RUN mvn clean install

FROM adoptopenjdk/openjdk11:latest
COPY --from=builder /src/target/template-manager.jar template-manager.jar
EXPOSE 8080
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "template-manager.jar"]