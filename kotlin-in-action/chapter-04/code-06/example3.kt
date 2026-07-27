class User(val nickname: String) {
    val normalizedNickname = nickname.trim().lowercase()    // alice

    init {
        println("First init: $normalizedNickname")
    }

    val displayName = normalizedNickname.replaceFirstChar { it.uppercase() }    // Alice

    init {
        println("Second init: $displayName")
    }
}

fun main() {
    User("  ALICE  ")
	// First init: alice
	// Second init: Alice
}