// Implicitly inherits from the `Any` class
class CustomerV1(val name: String, val postalCode: Int)

class CustomerV2(val name: String, val postalCode: Int) {
    // overriding Any.toString() (full signature -> public open fun toString(): String)
    override fun toString() = "Customer(name=$name, postalCode=$postalCode)"
}

class CustomerV3(val name: String, val postalCode: Int) {
    // overriding Any.toString() (full signature -> public open fun toString(): String)
    override fun toString() = "Customer(name=$name, postalCode=$postalCode)"

    // overriding Any.equals(other: Any?) (full signature -> public open operator fun equals(other: Any?): Boolean)
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is CustomerV3)
            return false
        return name == other.name && postalCode == other.postalCode
    }
}

class CustomerV4(val name: String, val postalCode: Int) {
    // overriding Any.toString() (full signature -> public open fun toString(): String)
    override fun toString() = "Customer(name=$name, postalCode=$postalCode)"

    // overriding Any.equals(other: Any?) (full signature -> public open operator fun equals(other: Any?): Boolean)
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is CustomerV4)
            return false
        return name == other.name && postalCode == other.postalCode
    }

    // overriding Any.hashCode() (full signature -> public open fun hashCode(): Int)
    override fun hashCode(): Int = name.hashCode() * 31 + postalCode
}


fun main() {
    val customer0 = CustomerV1("Alice", 342562)
    val customer0_ = CustomerV1("Alice", 342562)
    println(customer0)
    // CustomerV1@4aa298b7

    val customer1 = CustomerV2("Alice", 342562)
    println(customer1)
    // Customer(name=Alice, postalCode=342562)

    val customer2 = CustomerV3("Alice", 342562)
    val customer2_ = CustomerV3("Alice", 342562)
    println(customer0 == customer0_)    // false
    println(customer2 == customer2_)    // true

    // The two `CustomerV1("Alice", 342562)` have distinct hash codes.
    val processed1 = hashSetOf(CustomerV1("Alice", 342562))
    println(processed1.contains(CustomerV1("Alice", 342562)))  // false

    // The two `CustomerV4("Alice", 342562)` have the same hash codes.
    val processed2 = hashSetOf(CustomerV4("Alice", 342562))
    println(processed2.contains(CustomerV4("Alice", 342562)))  // true
}