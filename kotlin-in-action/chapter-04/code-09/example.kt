class User(val name: String) {
    var address: String = "unspecified"
        set(value: String) {
            println(
                """
                Address was changed for $name:
                "$field" -> "$value".
                """.trimIndent()
            )
            field = value   // The special identifier `field`: the reference to the backing field.
        }
        // If you don't redefine the getter `get()`, the trivial getter is used.

}

class Person(var birthYear: Int) {
    var ageIn2050   // It doesn't have the backing field.
        get() = 2050 - birthYear
        set(value) {
            println("Change in birthYear: ${this.birthYear} -> ${2050 - value}")
            birthYear = 2050 - value
        }
}

fun main() {
    val user = User("Alice")
    user.address = "Christoph-Rapparini-Bogen 23"   // The setter is invoked.
    // Address was changed for Alice:
    // "unspecified" -> "Christoph-Rapparini-Bogen 23".

    println("")
    val person = Person(2000)
    person.ageIn2050 = 30
    // Change in birthYear: 2000 -> 2020
}