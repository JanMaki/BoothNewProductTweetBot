#コンパイル
FROM gradle:jdk17 AS compile

WORKDIR /app
COPY . .

RUN gradle wrapper && chmod +x ./gradlew && ./gradlew shadowJar -i



#Botを起動
FROM amazoncorretto:17 AS bot

WORKDIR /app

COPY --from=compile /app/build/libs .

CMD ["java", "-jar", "BoothNewProductTweetBot-1.0.0-all.jar"]
