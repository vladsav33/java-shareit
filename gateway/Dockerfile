FROM amazoncorretto:11-alpine-jdk
RUN apk add -U tzdata
ENV TZ=Europe/Moscow
RUN cp /usr/share/zoneinfo/Europe/Moscow /etc/localtime
COPY target/shareit-gateway-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]