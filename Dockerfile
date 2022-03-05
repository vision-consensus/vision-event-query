FROM ubuntu:18.04
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

RUN apt-get update \
    && apt-get install -y openjdk-8-jdk wget \
    && apt-get install -y maven \
    && rm -r /var/cache/

RUN wget https://github.com/Yelp/dumb-init/releases/download/v1.2.0/dumb-init_1.2.0_amd64.deb \
    && dpkg -i dumb-init_1.2.0_amd64.deb

COPY ./ $APP_HOME

RUN mvn package

RUN chmod +x /usr/app/deploy.sh

EXPOSE 8080

ENTRYPOINT ["/usr/bin/dumb-init", "-c", "/usr/app/deploy.sh"]
