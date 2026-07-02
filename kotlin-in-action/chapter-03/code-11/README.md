# What chapter in 'Kotlin in Action'?
- Chapter 3.4.2 Varargs: Functions that accept an arbitrary number of arguments (2/e version)

# What did I look?
- The `vararg` modifier: you can declare variable arguments of the function.
  - You can pass an arbitrary number of values to the function.
- The *spread operator* `*`: you can explicitly unpack the array.
  - It transforms elements of the array into the separate elements.

# Variable arguments and spread operator
## A modifier `vararg`
You can make a list:
```kotlin
val list1 = listOf(2, 3, 5, 7, 11)
val list2 = listOf(2, 3)
```
Here, you can pass any number of arguments to `listOf`.  
How is it possible?  
You can find the answer in the declaration of `listOf`.
```kotlin
fun listOf<T>(vararg values: T): List<T> { /* implementation */ }
```
- Just focus on the `vararg` modifier!
  - It lets a function parameter accept a variable number of arguments.
  - This means that, when calling the function, you can provide zero or more values for that parameter.

## Spread operator `*`
Meanwhile, you can sometimes want to pass the elements of an array to the variable parameter of a function.  
However, passing an array directly means passing the array as one argument.  
- If you want to pass the array's arguments as separate arguments to the `vararg` parameter, you can use the spread parameter `*`.

```kotlin
fun main(args: Array<String>) {
    val list = listOf("args: ", *args)
    println(list)
}
```
- If `args` accepts `arrayOf("apple", "banana", "strawberry")`, the arguments of `listOf` is equivalent to:
    ```text
    ("args: ", "apple", "banana", "strawberry")
    ```

# Codes
## A function `sumAll` with a `vararg` parameter
```kotlin
fun sumAll(vararg numbers: Int): Int {
    var total = 0
    for (num in numbers) {
        total += num
    }
    return total
}
```
- The parameter `numbers` can accept a variable number of values.

## Passing an arbitrary number of values to the `vararg` parameter
### Directly listing zero or more numbers as arguments
```kotlin
fun main() {
    println(sumAll(1, 2))
    // 3
    println(sumAll(10, 20, 30, 40))
    // 100
    println(sumAll())
    // 0
}
```
- However many numbers you pass, it works well!
  - The absence of arguments (i.e., passing no argument) is also OK.

### Applying a spread operator `*` to an array
```kotlin
fun main() {
    val numbersArray = intArrayOf(10, 20, 30)

    val result = sumAll(*numbersArray) 
    println(result)
    // 60
}
```
- As you can see, you can pass `numbersArray` with a spread operator (*) before it.
  - It is equivalent to:
    ```kotlin
    val result = sumAll(10, 20, 30) 
    ```
