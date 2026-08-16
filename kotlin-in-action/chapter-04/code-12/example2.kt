// Programming in this way is not recommended.
data class Customer(
    var name: String,
    var postalCode: Int
)

fun main() {
    // A hash code of the instance is calculated, a corresponding bucket is created, and the instance would be stored in the bucket.
    val anInstance = Customer("Alice", 342562)
    val processed = hashSetOf(anInstance)

    println(anInstance in processed)
    // true

    anInstance.postalCode = 999999
    println(anInstance in processed)
    // false
}