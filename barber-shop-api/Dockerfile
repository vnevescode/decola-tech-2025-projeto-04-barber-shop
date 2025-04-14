FROM gradle:8.11.1-jdk-21-and-23 AS build

RUN apt-get update && apt-get install -qq -y --no-install-recommends

ENV INSTALL_PATH=/barber-shop-api
ENV TZ=America/Sao_Paulo

RUN mkdir $INSTALL_PATH

WORKDIR $INSTALL_PATH

COPY . .

# (opcional) buildar logo no container
# RUN ./gradlew clean build -x test

EXPOSE 8084