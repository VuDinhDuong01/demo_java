FROM amazoncorretto:21.0.4 as builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

#BUILD
RUN mvn  package -DskipTests

FROM amazoncorretto:21.0.4 as runner

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 5000

CMD [ "java" ,"-jar","app.jar" ]

