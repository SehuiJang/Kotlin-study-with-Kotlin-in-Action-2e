open class User(val nickname: String) { /* ... */ }

class SocialUser(nickname: String) : User(nickname) { /* ... */ }


open class Button

class RadioButton: Button() // calling default constructor of `Button`

// private constructor
class Secret private constructor(private val agentName: String) {}

fun main() {
    val inst1 = SocialUser("ABC")

    val inst2 = RadioButton()   // calling default constructor of `RadioButton`

    // val inst3 = Secret("Agent1") // invoking a private constructor from the outside of the class is not allowed. 
}
