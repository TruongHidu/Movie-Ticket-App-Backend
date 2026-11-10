# ===== Stage 1: build file .jar =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Tải dependency trước để cache, chỉ tải lại khi pom.xml thay đổi
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B package -DskipTests

# ===== Stage 2: chạy app =====
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV TZ=Asia/Ho_Chi_Minh

# Chạy bằng user thường (uid cố định 10001) thay vì root
RUN groupadd -r -g 10001 app && useradd -r -u 10001 -g app app

COPY --from=build /build/target/*.jar /app/app.jar

USER app
EXPOSE 8081
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
