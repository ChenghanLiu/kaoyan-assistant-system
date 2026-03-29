FROM docker.1ms.run/eclipse-temurin:17-jre-jammy

WORKDIR /app
ENV TZ=Asia/Shanghai

RUN apt-get update \
    && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /app/uploads

COPY backend/target/*.jar /app/app.jar
COPY docker/demo-uploads/ /app/uploads/

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]