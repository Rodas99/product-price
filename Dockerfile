FROM openjdk:17
EXPOSE 8080
ADD target/price.jar price.jar
ENTRYPOINT ["java","-jar","/price.jar"]