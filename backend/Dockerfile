FROM gradle:8.7.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon
# Gradleビルド後に生成されたJARファイルの名前を確認し、app.jarとしてコピー
RUN find /app/build/libs -name "*.jar" -not -name "*-plain.jar" -exec cp {} /app/app.jar \;

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 