FROM gradle:jdk21 AS build

WORKDIR /app
COPY . .
RUN gradle wrapper && ./gradlew shadowJar -i --stacktrace

FROM amazoncorretto:21-alpine3.22 AS app

WORKDIR /app
COPY --from=build /app/build/libs .

CMD ["java", "-jar", "BoothNewProductTweetBot.jar"]