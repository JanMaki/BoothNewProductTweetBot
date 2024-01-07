plugins {
    kotlin("jvm") version "1.6.21"

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.simpletimer.murder_mystery_notice"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {

    //JSoup
    implementation("org.jsoup:jsoup:1.15.1")

    //Twitter連携
    implementation("io.github.redouane59.twitter:twittered:2.16")

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