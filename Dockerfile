FROM java:openjdk-8-jre-alpine
LABEL maintainer="diogoalbuquerque"
ENV REPO_PATH /kotlin/src/generator
RUN mkdir -p $REPO_PATH
COPY build/libs/generator-1.0.jar $REPO_PATH
WORKDIR $REPO_PATH
CMD java -jar generator-1.0.jar
EXPOSE 8080