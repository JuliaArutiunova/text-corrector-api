FROM eclipse-temurin:21.0.5_11-jre-noble

COPY ./build/libs/text-corrector-api.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/text-corrector-api.jar"]

EXPOSE 8080