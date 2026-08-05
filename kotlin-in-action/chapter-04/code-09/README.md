# What chapter in 'Kotlin in Action'?
- Chapter 4.2.4 Accessing a backing field from a getter or setter (2/e version)

# What did I look?
- The special identifier `field`: You can access the backing field of a property from a getter or setter.
- When the backing field is generated
  - explicitly reference it from a getter or setter ,using `field`
  - or use the default, trivial accessor implementation
    - In other words, it means that you initialize the property with a value, using the property initializer, without redefining neither a setter nor a getter.
    - That is, using the property initializer leads to the backing field generation.
- How to avoid generating the backing field
  - Do not use the property initializer.
  - In addition, you must provide custom accessor implementations which don't utilize `field`.
    - `val` &rarr; the getter
    - `var` &rarr; both the getter and setter

# Codes
## Accessing the backing field `field` in a setter
```kotlin
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

fun main() {
    val user = User("Alice")
    user.address = "Christoph-Rapparini-Bogen 23"   // The setter is invoked.
    // Address was changed for Alice:
    // "unspecified" -> "Christoph-Rapparini-Bogen 23".
}
```
As you can see, the `field` identifier makes you be able to access the backing field.  
- In the getter `get()`, it is possible only to read the value.
- In the setter `set(...)`, both reading and modifying the value are possible.

Defining either the getter or setter of a `var` variable is also allowed.

## Declaring a property without the backing field
If you wanna declare a property without the backing field, you have to observe the following items.
- Avoid using the property initializer.
- You must define the custom accessors that don't use `field`.
  - `val` &rarr; the getter
  - `var` &rarr; both the getter and setter

```kotlin
class Person(var birthYear: Int) {
    var ageIn2050   // It doesn't have the backing field.
        get() = 2050 - birthYear
        set(value) {
            println("Change in birthYear: ${this.birthYear} -> ${2050 - value}")
            birthYear = 2050 - value
        }
}

fun main() {
    println("")
    val person = Person(2000)
    person.ageIn2050 = 30
    // Change in birthYear: 2000 -> 2020
}
```
