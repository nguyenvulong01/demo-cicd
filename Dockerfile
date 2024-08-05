FROM eclipse-temurin:17-jdk-alpine
EXPOSE 8080
ADD target/springboot-new.jar springboot-new.jar
ENTRYPOINT ["java","-jar","/springboot-new.jar"]
