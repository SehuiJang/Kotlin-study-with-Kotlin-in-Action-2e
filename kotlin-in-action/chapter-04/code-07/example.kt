import java.net.URI

open class Downloader {
    constructor(url: String?) {
        println("constructor(url: String?) in Downloader")
    }
    constructor(uri: URI?) {
        println("constructor(url: URI?) in Downloader")
    }
}

class MyDownloader : Downloader {
    constructor(url: String?) : super(url) {
        println("constructor(url: String?) in MyDownloader")
    }
    constructor(uri: URI?) : super(uri) {
        println("constructor(url: URI?) in MyDownloader")
    }
}

class MyDownloader2 : Downloader {
    constructor(url: String?) : this(URI(url)) {
        println("constructor(url: String?) in MyDownloader2")
    }
    constructor(uri: URI?) : super(uri) {
        println("constructor(url: URI?) in MyDownloader2")
    }
}

fun main() {
    run {
        println("============================")
        val obj1 = Downloader("https://abc.com")
        println("=========")
        val obj2 = Downloader(URI("https://abc.com"))
    }

    run {
        println("============================")
        val obj1 = MyDownloader("https://abc.com")
        println("=========")
        val obj2 = MyDownloader(URI("https://abc.com"))
    }

    run {
        println("============================")
        val obj1 = MyDownloader2("https://abc.com")
        println("=========")
        val obj2 = MyDownloader2(URI("https://abc.com"))
    }

}