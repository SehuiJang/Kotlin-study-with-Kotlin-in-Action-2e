class MyClass {
    companion object {
        fun callMe() {
            println("Companion object called")
        }
    }
}

// -----------------
fun getSocialNetworkName(accountId: Int): String =
    "kodee$accountId"

class User1 {
    val nickname: String

    // secondary constructor 1
    constructor(email: String) {
        nickname = email.substringBefore('@')
    }

    // secondary constructor 2
    constructor(socialAccountId: Int) {
        nickname = getSocialNetworkName(socialAccountId)
    }
}

fun getNameFromSocialNetwork(accountId: Int): String =
    "kodee$accountId"

class User2 private constructor(val nickname: String) {

    companion object {
        fun newSubscribingUser(email: String) =
            User2(email.substringBefore('@'))

        fun newSocialUser(accountId: Int) =
            User2(getNameFromSocialNetwork(accountId))
    }
}

fun main() {
    // It can call the member function of the `companion object`, using just the class name.
    MyClass.callMe()
    MyClass.Companion.callMe()
    // Companion object called

    // Caution: It is not allowed to access the companion object's members through instances of the class.
    val myObject = MyClass()
    // myObject.callMe()   // NOT allowed!
    // Error: Unresolved reference: callMe

// -------------------------
    val subscribingUser1 = User1("bob@gmail.com")
    val socialUser1 = User1(4)
    println(subscribingUser1.nickname)
    // bob
    println(socialUser1.nickname)
    // kodee4

    val subscribingUser2 = User2.newSubscribingUser("bob@gmail.com")
    val socialUser2 = User2.newSocialUser(4)
    println(subscribingUser2.nickname)
    // bob
    println(socialUser2.nickname)
    // kodee4

}