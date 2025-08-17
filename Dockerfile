# ---- Build stage (uses Maven with JDK 21) ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom first to leverage Docker layer caching
COPY pom.xml .
RUN mvn -q -e -U -B dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ---- Runtime stage (small JRE 21 image) ----
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copy the built jar (wildcard handles your version automatically)
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","/app/app.jar"]
