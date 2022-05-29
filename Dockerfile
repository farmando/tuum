FROM alpine:latest
RUN  apk update \
  && apk upgrade \
  && apk add ca-certificates \
  && update-ca-certificates \
  && apk add --update coreutils \
  && apk add --update openjdk11 \
  && apk add --no-cache nss \
  && rm -rf /var/cache/apk/*
WORKDIR /usr/app
EXPOSE 8080
COPY ./build/libs/tuum-interview-0.0.1-SNAPSHOT.jar /usr/app/
ENTRYPOINT ["java", "-jar", "tuum-interview-0.0.1-SNAPSHOT.jar"]


