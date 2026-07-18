internal open class TalkativeButton {
    private fun yell() = println("Hey!")
    protected fun whisper() = println("Let's talk!")
}

// Error: public member (= extension function `giveSpeech()`) exposes its `internal` receiver type `TalkativeButton`
fun TalkativeButton.giveSpeech() {
    yell()  // Error: `yell` is private in TalkativeButton
    whisper()   // Error: `whisper` is protected in TalkativeButton
}

fun main() {
    
}