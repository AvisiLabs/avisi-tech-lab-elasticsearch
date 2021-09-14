FROM openjdk:11.0.6-slim

ENV JVM_MEMORY_OPTIONS ""

RUN ln -fs /usr/share/zoneinfo/Europe/Amsterdam /etc/localtime \
    && dpkg-reconfigure --frontend noninteractive tzdata

COPY wait-for-it.sh .
RUN chmod +x ./wait-for-it.sh
COPY build/libs/techlab-elasticsearch-0.0.1-SNAPSHOT.jar app.jar

CMD java $JVM_MEMORY_OPTIONS -jar app.jar
