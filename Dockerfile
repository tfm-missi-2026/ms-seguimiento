FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY pom.xml ./
RUN mkdir -p src && \
    apt-get update > /dev/null && \
    apt-get install -y --no-install-recommends maven > /dev/null && \
    rm -rf /var/lib/apt/lists/* && \
    mvn -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
RUN apt-get update > /dev/null && \
    apt-get install -y --no-install-recommends wget > /dev/null && \
    rm -rf /var/lib/apt/lists/*
COPY --from=build /app/target/ms-seguimiento.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","/app/app.jar"]