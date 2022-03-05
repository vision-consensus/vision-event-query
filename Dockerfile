FROM ubuntu:16.04
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

RUN apt-get update \
    && apt-get install -y default-jdk wget \
    && rm -r /var/cache/

ENV JAVA_HOME=/usr/lib/jvm/default-java
ENV M2_HOME=/opt/maven
ENV MAVEN_HOME=/opt/maven
ENV PATH=${M2_HOME}/bin:${PATH}

RUN wget https://www-us.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz -P /tmp \
    && tar xf /tmp/apache-maven-*.tar.gz -C /opt \
    && ln -s /opt/apache-maven-3.6.3 /opt/maven

RUN wget https://github.com/Yelp/dumb-init/releases/download/v1.2.0/dumb-init_1.2.0_amd64.deb \
    && dpkg -i dumb-init_1.2.0_amd64.deb

COPY ./ $APP_HOME

RUN mvn package

RUN chmod +x /usr/app/deploy.sh

EXPOSE 8080

ENTRYPOINT ["/usr/bin/dumb-init", "-c", "/usr/app/deploy.sh"]
