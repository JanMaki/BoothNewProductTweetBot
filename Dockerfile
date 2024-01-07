#コンパイル
FROM gradle AS compile

WORKDIR /app
COPY . .
RUN ./gradlew shadowJar -i

#Botを起動
FROM amazoncorretto:17 AS bot

WORKDIR /app

COPY --from=compile /app/build/libs .

CMD ["java", "-jar", "BoothNewProductTweetBot-1.0.0-all.jar"]