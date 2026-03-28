FROM docker.1ms.run/maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY backend/pom.xml backend/pom.xml
RUN mvn -f backend/pom.xml -q -DskipTests dependency:go-offline
COPY backend/src backend/src
RUN mvn -f backend/pom.xml -DskipTests clean package

FROM docker.1ms.run/eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app
ENV TZ=Asia/Shanghai
RUN apt-get update \
    && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /app/uploads
COPY --from=builder /build/backend/target/assistant-backend-0.0.1-SNAPSHOT.jar /app/app.jar
COPY docker/demo-uploads/ /app/uploads/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
