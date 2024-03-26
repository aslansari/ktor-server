FROM amazoncorretto:17.0.7-alpine
EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/libs/*-all.jar /app/ktor-server-all.jar
ENTRYPOINT ["java","-jar","/app/ktor-server-all.jar"]