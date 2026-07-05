interface Clickable {
    fun click()
    // equivalent to `fun click() : Unit`
}

// The class `Button` implements an interface `Clickable`.
class Button : Clickable {
    override fun click() = println("I was clicked")
}

fun main() {
    Button().click()
    // I was clicked
}
