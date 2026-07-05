interface Clickable {
    // regular method declaration
    fun click()

    // This method has a default implementation.
    fun showOff() = println("I'm clickable!")
}

// This interface has a method `showOff` of which name is the same as the one in `Clickable`.
interface Focusable {
    fun setFocus(b: Boolean) =
        println("I ${if (b) "got" else "lost"} focus.")

    fun showOff() = println("I'm focusable!")
}

// The class `Button` implements two interfaces.
// supertype: Clickable and Focusable
class Button : Clickable, Focusable {
    override fun click() = println("I was clicked")

    // Both two supertype (interface) has the member `showOff`
    // Thus, you have to implement `showOff` in this class.
    override fun showOff() {
        // super<SuperType> -> it specifies the parent 
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }

    // Or you can choose one of the inherited implementations like this:
    // override fun showOff() = super<Clickable>.showOff()
}

fun main() {
    val button = Button()
    button.showOff()
    // I'm clickable!
    // I'm focusable!
    button.setFocus(true)
    // I got focus.
    button.click()
    // I was clicked.
}
