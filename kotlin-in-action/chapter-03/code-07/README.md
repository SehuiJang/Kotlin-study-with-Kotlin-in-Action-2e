# What chapter in 'Kotlin in Action'?
- Chapter 3.3.3 Utility functions as extensions (2/e version)

# What did I look?
- Declaring an extension function with parameters and default parameter values
- A few examples: the receiver type is an interface, not a single class.

# Codes
## `joinToString()` as an extension function
```kotlin
// Receiver type: Collection<T>
fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
): String {
    val result = StringBuilder(prefix)
    
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }
    
    result.append(postfix)
    return result.toString()
}

fun main() {
    val list = listOf(1, 2, 3)
    println(
        list.joinToString(
            separator = "; ",
            prefix = "(",
            postfix = ")"
        )
    )
    // (1; 2; 3)
    
    val list2 = listOf(1, 2, 3)
    println(list2.joinToString(" "))
    // 1 2 3
}
```
- Note that `Collection<T>` is not a class but an interface.
- Making an extension function to a collection of elements (`Collection<T>`) means:
    ```
    it can works on lists, sets, and so on.
    ```
- Of course, you can specify default values for all the parameters of the extension function.

## Designating a more specific type as a receiver type
```kotlin
fun Collection<String>.join(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
) = joinToString(separator, prefix, postfix)    // reuse joinToString
    // the same as this.joinToString(...)

fun main() {
    println(listOf("one", "two", "eight").join(" "))
	// one two eight
    
//     listOf(1, 2, 8).join()   // the receiver type is Collection<Int>
    // error! None of the following candidates is applicable because of a receiver type mismatch
}
```
- `Collection<T>` indicates a collection (e.g., set, list, etc.) of elements of which the type is `T`.
- `Collection<String>` points to multiple classes (set, list, etc.) but is a more specific type: a collection of `String` elements!

