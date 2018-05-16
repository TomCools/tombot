FROM openjdk:8-jre-alpine

ENV FACEBOOK_TOKEN=DUMMY
ENV WIT_TOKEN=DUMMY
ADD tombot-entrypoint/target/tombot-fat.jar tombot.jar

CMD java -jar tombot.jar

EXPOSE 80