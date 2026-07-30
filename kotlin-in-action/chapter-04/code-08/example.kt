interface User {
    val nickname: String
}

class PrivateUser(override val nickname: String) : User

class SubscribingUser(val email: String) : User {
    override val nickname: String
        get() = email.substringBefore('@')
}

class SocialUser(val accountId: Int) : User {
    override val nickname = getNameFromSocialNetwork(accountId)
}

fun getNameFromSocialNetwork(accountId: Int) =
    "kodee$accountId"

fun main() {
    println(PrivateUser("kodee").nickname)
    // kodee
    println(SubscribingUser("test@kotlinlang.org").nickname)
    // test
    println(SocialUser(123).nickname)
    // kodee123
}