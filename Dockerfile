FROM java:8

ENV FACEBOOK_TOKEN=DUMMY
ADD target/tombot-1.0-SNAPSHOT-fat.jar tombot.jar

CMD java -jar tombot.jar

EXPOSE 9999