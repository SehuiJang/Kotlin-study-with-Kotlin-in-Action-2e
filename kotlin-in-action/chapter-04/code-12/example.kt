// Declaring `data class`
data class Customer(val name: String, val postalCode: Int){
    val myVar = 0

    // If you manually declare the `copy` method, it would look like the method below.
    /*
    fun copy(name: String = this.name, postalCode: Int = this.postalCode) = Customer(name, postalCode)
    */
}

data class CustomerVar(val name: String, val postalCode: Int){
    var variableProp = 0
}

fun main() {
    val c1 = Customer("Sam", 11521)
    val c2 = Customer("Mart", 15500)
    val c3 = Customer("Sam", 11521)
    println(c1)
    // Customer(name=Sam, postalCode=11521)
    println(c1 == c2)
    // false
    println(c1 == c3)
    // true
    println(c1.hashCode())
    // 2580770
    println(c3.hashCode())
    // 2580770

    val bob = Customer("Bob", 973293)
    println(bob)    // Customer(name=Bob, postalCode=973293)
    println(bob.copy(postalCode = 382555))  // Customer(name=Bob, postalCode=382555)
}