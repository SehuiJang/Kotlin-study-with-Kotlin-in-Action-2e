interface MouseListener {
    fun onEnter()
    fun onClick()
}

class Button(private val listener: MouseListener) {
    fun run() {
        listener.onClick()
    }
}

fun main() {
    // (1) passing an object expression (anonymous object) to the constructor of `Button`
    Button(object : MouseListener {
        override fun onEnter() { /* ... */ }
        override fun onClick() { /* ... */ }
    })

    // (2) You can also assign an anonymous object to a variable.
    val listener = object : MouseListener {
        override fun onEnter() { /* ... */ }
        override fun onClick() { /* ... */ }
    }

    // (3) Accessing and modifying a local variable from an anonymous object
    var clickCount = 0
    var button = Button(object : MouseListener {
        override fun onEnter() { /* ... */ }
        override fun onClick() {
            clickCount++
            println(clickCount)
        }
    })
    button.run()
    // 1
    button.run()
    // 2
    button.run()
    // 3

}