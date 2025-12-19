# BUILD
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /build
COPY pom.xml ./
# cache dependencies
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
