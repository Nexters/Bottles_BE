FROM amazoncorretto:17-alpine

ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY build/libs/*.jar batch.jar
ENTRYPOINT ["java", "-jar", "batch.jar"]
