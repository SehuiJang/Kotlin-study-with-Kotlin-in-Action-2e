import kotlin.random.Random

class Person(val name: String) {
    companion object Loader {
        fun fromJSON(jsonText: String): Person {
            val name = jsonText
                .substringAfter("\"name\"")
                .substringAfter(":")
                .substringAfter("\"")
                .substringBefore("\"")

            return Person(name)
        }
    }
}

// ============================

interface JSONFactory<T> {
    fun fromJSON(jsonText: String): T
}

class Person2(val name: String) {
    companion object : JSONFactory<Person2> {
        override fun fromJSON(jsonText: String): Person2 {
            val name = jsonText
                .substringAfter("\"name\"")
                .substringAfter(":")
                .substringAfter("\"")
                .substringBefore("\"")

            return Person2(name)
        }
    }
}

fun <T> loadFromJSON(factory: JSONFactory<T>): T {
    return factory.fromJSON("Alice")
}

// ============================

class Person3(val firstName: String, val lastName: String) {
    // an empty companion object
    companion object {
    }
}

fun Person3.Companion.fromJSON(json: String): Person3 {
    val name_ = json
        .substringAfter("\"name\"")
        .substringAfter(":")
        .substringAfter("\"")
        .substringBefore("\"")

    return Person3(name_, "Last_temp")
}


fun main() {
// ----------------------------
    val person =
        Person.Loader.fromJSON("""{"name": "Dmitry"}""")

    println(person.name)
    // Dmitry

    val person2 =
        Person.fromJSON("""{"name": "Brent"}""")

    println(person2.name)
    // Brent

    val chance = Random.nextInt(from = 0, until = 100)
    val coin = Random.Default.nextBoolean()
    val coin2 = Random.nextBoolean()
// ----------------------------

    loadFromJSON(Person2.Companion)
    loadFromJSON(Person2)

    val factory1: JSONFactory<Person2> = Person2
    val factory2: JSONFactory<Person2> = Person2.Companion

    println(factory1 === factory2)
    // true
// ----------------------------

    val p = Person3.Companion.fromJSON("""{"name": "Ben"}""")
    println("${p.firstName} ${p.lastName}")
    // Ben Last_temp

    val pp = Person3.fromJSON("""{"name": "Bob"}""")
    println("${pp.firstName} ${pp.lastName}")
    // Bob Last_temp
}