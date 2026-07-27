// most simplest syntax
class User1(val nickname: String)

// most explicit, unpacked syntax
class User2 constructor(_nickname: String) { // primary constructor
    val nickname: String  // class property

    init {  // initializer block
        nickname = _nickname
    }
}

// using the identical name (`nickname`) for both a constructor parametr and a class property.
// However, using `this` is recommended to avoid the ambiguity.
class User2_ constructor(nickname: String) {
    val nickname: String

    init {
        this.nickname = nickname
    }
}

// The `constructor` keyword is omitted.
// In this case, `init` block does not necessarily need to be utilized.
class User3(_nickname: String) {
    val nickname = _nickname   // directly initializing the class property with a constructor parameter
}

class User3_ @JvmOverloads constructor(_nickname: String = "ABC") {
    val nickname = _nickname
}

class User4(
	val nickname: String,
	val isSubscribed: Boolean = true
)

fun main() {
    run {
        val u1 = User2_("Alice")
    }

    run{
        val alice = User4("Alice")
        println(alice.isSubscribed)
        // true
        val bob = User4("Bob", false)
        println(bob.isSubscribed)
        // false
        val carol = User4("Carol", isSubscribed = false)
        println(carol.isSubscribed)
        // false
        val dave = User4(nickname = "Dave", isSubscribed = true)
        println(dave.isSubscribed)
        // true
    }
}