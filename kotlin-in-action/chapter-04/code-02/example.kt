// The interface and its members are `open` by default.
interface Clickable {
    fun click() // regular method
    fun showOff() = println("I'm clickable!") // a method with its default implementation
}

// open class -> subclasses can inherit the class
open class RichButton : Clickable {
    fun disable() { println("`disable` of a class `RichButton`") } // it is `final`. subclasses can invoke it but cannot override it.
    open fun animate() { println("`animate` of a class `RichButton`") }    // subclasses can override it.
    override fun click() { println("`click` overridden in a class `RichButton`") }  // an overriden method is `open` by default.
}

class ThemedButton : RichButton() {
    // Note that `disable` of class `RichButton` cannot be overridden in this subclass.
    override fun animate() { println("`animate` overridden in a class `ThemedButton`") }    // overriding a method `animate` of class `RichButton`
    override fun click() { println("`click` overridden in a class `ThemedButton`") }  // overriding a method `click` of class `RichButton`
    override fun showOff() { println("`showOff` overridden in a class `ThemedButton`") }  // overriding a method `showOff` of interface `Clickable`
}

open class RichButton2 : Clickable {
    // If you don't want for the overridden method `click` to be overridden in lower-level subclasses, the `final` modifier can be marked explicitly.
    final override fun click() { println("`click` overridden in a class `RichButton2` / it is `final`") }
}

// ---------------------------------------------------------------

// modifier `abstract`
// `abstract class` -> you cannot instantiate this class.
abstract class Animated {
    // `abstract` property -> no initial value and no custom accessor
    // subclasses MUST override `abstract` properties.
    abstract val animationSpeed: Double
    val keyframes: Int = 20
    open val frames: Int = 60

    // `abstract fun` (member function) -> no implementation body
    // subclasses MUST override `abstract fun`.
    abstract fun animate()
    open fun stopAnimating() { println("`open fun stopAnimating()` in `abstract class Animated`") }
    fun animateTwice() { println("`fun animateTwice()` in `abstract class Animated`") }
}

fun main() {

}