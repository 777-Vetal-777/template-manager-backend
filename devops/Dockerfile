FROM maven:3.6.3-openjdk-11 as builder
COPY . /src
WORKDIR /src
RUN mvn clean install

FROM adoptopenjdk/openjdk11:jdk-11.0.9.1_1-alpine
COPY --from=builder /src/target/template-manager*.jar template-manager.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "template-manager.jar"]
