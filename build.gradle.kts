plugins {
    kotlin("jvm") version "2.0.0"

    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.simpletimer.booth_new_product_tweet_bot"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {

    //JSoup
    implementation("org.jsoup:jsoup:1.17.2")

    //Twitter連携
    implementation("io.github.redouane59.twitter:twittered:2.23")

    //Log
    implementation("ch.qos.logback", "logback-classic", "1.2.8")

}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "net.janmaki.booth_new_product_tweet_bot.BoothNewProductTweetBotKt"
    }

    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })

    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
}