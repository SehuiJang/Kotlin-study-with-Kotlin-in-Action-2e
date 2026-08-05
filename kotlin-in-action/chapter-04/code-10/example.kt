class LengthCounter {
    var counter: Int = 0
        private set
        // private get  // Error!
        // example.kt:4:9: error: getter visibility must be the same as property visibility.
        // private get
        // ^^^^^^^

    
    fun addWord(word: String) {
        counter += word.length
    }
}

fun main() {
    val lengthCounter = LengthCounter()
    lengthCounter.addWord("Hi!")
    println(lengthCounter.counter)
    // 3

    // lengthCounter.counter = 0
    // Error: Cannot assign to 'counter': the setter is private in 'LengthCounter'
}