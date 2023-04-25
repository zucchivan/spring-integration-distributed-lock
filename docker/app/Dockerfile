FROM openjdk:17

ENV APP_HOME=/app
WORKDIR $APP_HOME

COPY ../build/libs/*.jar $APP_HOME/distributedlockpoc.jar

CMD ["java","-jar","tyww-vw-vs.jar"]