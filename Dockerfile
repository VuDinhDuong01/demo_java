FROM openjdk:17-jdk-alpine AS builder

RUN apk add --no-cache maven

WORKDIR /app

COPY pom.xml .

COPY src ./src

# build
RUN mvn package -DskipTests 

FROM openjdk:17-jdk-slim AS runner

WORKDIR /app

COPY --from=builder /app/target/*.jar ./app.jar

EXPOSE  8080

CMD ["java","-jar","/app/app.jar"]
