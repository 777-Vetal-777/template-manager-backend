FROM maven:3.6.3-openjdk-11 as builder
COPY ./settings.xml /root/.m2/
COPY . /src
WORKDIR /src
ARG SKIP_TESTS=false
RUN mvn clean install -DskipTests=${SKIP_TESTS}

FROM adoptopenjdk/openjdk11:jdk-11.0.10_9-alpine
COPY --from=builder /src/target/template-manager*.jar template-manager.jar
EXPOSE 8080 8000
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000", "-jar", "template-manager.jar"]