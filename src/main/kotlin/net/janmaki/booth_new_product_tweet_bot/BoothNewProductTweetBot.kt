package net.janmaki.booth_new_product_tweet_bot

import io.github.redouane59.twitter.TwitterClient
import io.github.redouane59.twitter.signature.TwitterCredentials
import org.jsoup.Jsoup
import java.util.concurrent.TimeoutException


fun main() {
    BoothNewProductTweetBot()
}

class BoothNewProductTweetBot {
    //Twitterのクライアントを作成
    private val client = TwitterClient(
        TwitterCredentials.builder()
            .accessToken(System.getenv("TwitterAccessToken"))
            .accessTokenSecret(System.getenv("TwitterAccessSecret"))
            .apiKey(System.getenv("TwitterAPIKey"))
            .apiSecretKey(System.getenv("TwitterAPISecretKey")).build()
    )

    //検索情報
    private val keyword: String? = System.getenv("SearchKeyword")?.replace(" ", "%20")
    private val exceptWords: List<String> =
        System.getenv("SearchExceptWord")?.split(",")?.map { it.trim() } ?: mutableListOf()
    private val tags: List<String> = System.getenv("SearchTag")?.split(",")?.map { it.trim() } ?: mutableListOf()

    //クールタイム
    private val coolTime = System.getenv("BoothSearchCoolTime")?.toLongOrNull() ?: 30000L

    //URL
    private val url = createUrl()

    //新着じゃないものをキャッシュ
    private val catch = getElementIds()

    init {
        //何回も繰り返す
        while (true) {
            //時間を置く
            Thread.sleep(coolTime)

            //タスクを実行
            task()
        }
    }

    /**
     * 実行するタスク
     *
     */
    private fun task() {
        try {
            val elementIds = getElementIds()
            //要素を確認する
            elementIds.filter { !catch.contains(it) } //キャッシュに含まれているのを除去
                .forEach { id ->
                    //キャシュに追加
                    catch.add(id)

                    //URLを作成
                    val url = "https://booth.pm/ja/items/${id}"

                    //商品のページに接続
                    Jsoup.connect(url).get().let { document ->
                        //ツイート
                        client.postTweet("${document.title().removeSuffix("- BOOTH")}\n${url}")

                        //ログを出す
                        println("Tweet！ $url")

                        //時間を置く
                        Thread.sleep(10000)
                    }
                }

            //キャッシュのサイズを確認
            if (catch.size >= 100) {
                //削除
                catch.clear()
                //キャッシュに追加
                catch.addAll(elementIds)
            }

        } catch (e: Exception) {
            if (e !is TimeoutException) e.printStackTrace()
        }
    }

    /**
     * Boothのアイテムを取得する
     *
     * @return BoothのアイテムのID
     */
    private fun getElementIds(): MutableList<String> {
        return Jsoup.connect(url)
            .get()
            .select(".item-card")
            .map { it.attr("data-product-id") }
            .toMutableList()
    }

    /**
     * URLを作成
     *
     * @return
     */
    private fun createUrl(): String {
        val baseUrlBuilder = StringBuilder("https://booth.pm/ja/")
        if (keyword != null) {
            baseUrlBuilder.append("search/items?")
        } else {
            baseUrlBuilder.append("items?")
        }
        if (exceptWords.isNotEmpty()) {
            baseUrlBuilder.append("except_words%5B%5D=${exceptWords.joinToString("+")}")
        }
        if (tags.isNotEmpty()) {
            baseUrlBuilder.append("tags%5B%5D=${tags.joinToString("+")}")
        }

        //並び順を新着に
        baseUrlBuilder.append("&sort=new")

        println(baseUrlBuilder)

        return baseUrlBuilder.toString()
    }

}