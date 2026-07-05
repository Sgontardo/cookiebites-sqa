FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /workspace

COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY src src
COPY Data Data

RUN chmod +x mvnw && ./mvnw -B -DskipTests package

FROM eclipse-temurin:17-jre-jammy AS runtime

WORKDIR /app

ENV JAVA_OPTS=""

COPY --from=build /workspace/target/cookiebites-0.0.1-SNAPSHOT.jar /app/app.jar
COPY --from=build /workspace/Data /app/Data

RUN mkdir -p /app/src/main/java/com/example/cookiebites/Front/View/img/galletas

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]