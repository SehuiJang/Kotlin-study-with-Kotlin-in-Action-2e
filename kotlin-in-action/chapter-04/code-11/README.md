# What chapter in 'Kotlin in Action'?
- Chapter 4.3 Compiler-generated methods: Data classes and class delegation (2/e version)
- Chapter 4.3.1 Universal object methods (2/e version)

# What did I look?
- Universal object methods: `equals`, `toString`, and `hashCode`
- How to change (i.e., override) the default behavior of them.
- `==` & `===` in Kotlin.
- If not specifying the superclass, the class implicitly inherits from the `Any` class.

# Introduction
- There are several methods defined by the Java platform. They need to belong to many classes and are often implemented in a routine and repititive way.
  - `equals`: compares two objects for equality.
  - `hashCode`: provides a hash code used by hash-based data structures.
  - `toString`: returns a textual representation of an object.

In Java platform, IDEs can automatically generate these methods.  
Even so, the IDE-generated methods remains in the form of source code.
- You have to maintain the boilerplate code.

Then, the Kotlin compiler can generate common methods such as `equals` behind the scenes.
- The generated code does not appear in your source code.
- You can avoid seeing the cluttered source code!

Even so, if you don't like the default behavior, you can consider overriding them.

# Codes
## Universal object method 1: `toString`
When you pass a variable referring an object into `println`, `toString` of the object is invoked.
- `toString` returns the class name and the memory address of the object.
- If you want a different behavior, you can override it.
  - Technically, you can override `Any.toString()`

```kotlin
// Implicitly inherits from the `Any` class
class CustomerV1(val name: String, val postalCode: Int)

class CustomerV2(val name: String, val postalCode: Int) {
    // overriding Any.toString() (full signature -> public open fun toString(): String)
    override fun toString() = "Customer(name=$name, postalCode=$postalCode)"
}

fun main() {
    val customer0 = CustomerV1("Alice", 342562)
    println(customer0)
    // CustomerV1@4aa298b7

    val customer1 = CustomerV2("Alice", 342562)
    println(customer1)
    // Customer(name=Alice, postalCode=342562)
}
```

## Universal object method 2: `equals`
`equals` is utilized when you want to check for object equality.  
You can directly call `equals` like `c1.equals(c2)`.  
But you can also use more intuitive syntax: `c1 == c2`.  

Practically, unless overriding `equals`, it returns `true` when the memory address of two objects are the same.  
So, you can override it if you like other behavior.  
- Technically, the target to be overridden is `Any.equals(other: Any?)`.

```kotlin
// Implicitly inherits from the `Any` class
class CustomerV1(val name: String, val postalCode: Int)

class CustomerV3(val name: String, val postalCode: Int) {
    // overriding Any.toString() (full signature -> public open fun toString(): String)
    override fun toString() = "Customer(name=$name, postalCode=$postalCode)"

    // overriding Any.equals(other: Any?) (full signature -> public open operator fun equals(other: Any?): Boolean)
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is CustomerV3)  // `is` operator smart-casts `other` to the `CustomerV3` type, and `is!` is the negation of `is`.
            return false
        return name == other.name && postalCode == other.postalCode
    }
}

fun main() {
    val customer0 = CustomerV1("Alice", 342562)
    val customer0_ = CustomerV1("Alice", 342562)

    val customer2 = CustomerV3("Alice", 342562)
    val customer2_ = CustomerV3("Alice", 342562)
    println(customer0 == customer0_)    // false
    println(customer2 == customer2_)    // true
}
```
- Importantly, the type of `other` is `Any?`, not both `CustomerV3` and `CustomerV3?`.
  - It follows the signature of `Any.equals`.

## Universal object method 3: `hashCode`
`hashCode` returns a hash code of an object.  
Importantly, when two objects are equal according to `equals`, the return values of `hashCode` are also identical between the two ones.  
- Default `hashCode` is consistent with the default `equals`. 
- Therefore, the default `hashCode` returns distinct hash codes for different objects according to the default `equals` (i.e., objects located at different addresses).

Accordingly, if you have overridden `equals`, you also need to override `hashCode`.  
- Strictly, `Any.hashCode()` is overridden.

If `hashCode` is not consistent with `equals`, you can encounter some problems.
- Imagine that you create an object, namely `objA`, and insert it into a hash-based collection.
- After that, you create an object, namely `objB` outside the collection and try to find the object in the collection.
- Even though `objA` and `objB` have the same property values, they have different hash code, according to the default behavior of `hashCode`.
  - Finding `objA` in the collection by using `objB` would lead to a result different from your intention.


```kotlin
// Implicitly inherits from the `Any` class
class CustomerV1(val name: String, val postalCode: Int)

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
    // The two `CustomerV1("Alice", 342562)` have distinct hash codes.
    val processed1 = hashSetOf(CustomerV1("Alice", 342562))
    println(processed1.contains(CustomerV1("Alice", 342562)))  // false

    // The two `CustomerV4("Alice", 342562)` have the same hash codes.
    val processed2 = hashSetOf(CustomerV4("Alice", 342562))
    println(processed2.contains(CustomerV4("Alice", 342562)))  // true
}
```
- Note that the first `CustomerV1("Alice", 342562)` and the second one would have different hash code each other even though their properties have the same values.
  - The `contains` method may check the hash code of the second `CustomerV1(...)` and the corresponding hash bucket.
  - However, the hash code of the first `CustomerV1(...)` differ from the second `CustomerV1`. Thus, the hash bucket which the `contains` method explores doesn't have the first `CustomerV1`.
  - As a result, `contains` fails to find the first `CustomerV1` and returns `false`.
- On the other hand, the two `CustomerV4("Alice", 342562)` objects have separate memory addresses but the identical hash code.
  - The `contains` method can explore the hash bucket containing the first `CustomerV4`, returning `true`.

---

# Note
## `Any` class
Note that if you don't specify the superclass, the custom class implicitly inherits from the `Any` class.  
It is the analogue of `java.lang.Object`.  
`Any` has the `equals`, `hashCode`, and `toString`.  

Their full signatures are as follows.
- `public open fun toString(): String`
- `public open operator fun equals(other: Any?): Boolean`
- `public open fun hashCode(): Int`

## Default behavior of `toString`, `equals`, and `hashCode`
If you don't override them, they show us their default behavior.

### `toString`
It returns a class name and the memory address of the object.
- `getClass().getName() + '@' + Integer.toHexString(hashCode())`
- For example, `Customer@5e9f23b4`

### `equals`
Its default behavior is a referential equality.  
That is, `equals` returns `true` when two variables (i.e., references) refer to the identical object.  
- Made by different constructor calls, the objects are not judged as the distinct ones in terms of the default `equals` behavior.

For example, look at the code below:
```kotlin
class Customer(val name: String, val postalCode: Int)

fun main() {
    val c1 = Customer("Alice", 342562)  // c1 ──> Customer("Alice", 342562)@address1
    val c2 = Customer("Alice", 342562)  // c2 ──> Customer("Alice", 342562)@address2
    val c3 = c1 // c3 ──> Customer("Alice", 342562)@address1

    println(c1 == c2)  // false
    println(c1 == c3)  // true
}
```
- The objects referred by `c1` and `c2` are created by the different constructor call, each other.
- Thus, `c1` and `c2` refer different objects and have distinct memory address.
- Contrarily, `c1` refers an object, and `c3` refers the same one.
- Therefore, `c1 == c3` returns `true`.

### `hashCode`
There is an essential contract:  
> Two objects for which the return value of `equals` is `true` must return the same `hashCode`.  

`hashCode` must be consistent with `equals`. If two objects are equal according to `equals`, they must return the same hash code.  
Default `equals` usually returns `false` for two objects constructed by separate constructor call. Thus, default `hashCode` throws different hash codes for the different objects (in terms of the return value of the default `equals`).  

Practically, the default behavior of `hashCode` is that:
```text
If two objects have different memory address, `hashCode` returns distinct hash codes.
```

Thus, if you have overridden `equals` in your class, it is recommended to override `hashCode` as well.  
This is important for hash-based collections such as `HashSet` and `HashMap`.

## `==` vs. `===` in Kotlin
Simply speaking, `==` is a structural equality, while `===` means a referential equality.
- `==`: It calls `equals`. If you overrode it, `==` follows its logic.
- `===`: It checks whether two references point to the same object. In other words, if the objects to which the two references point have a same address, `===` returns `true`.  

## `==` in Java
In Java, `==` can be used as either structural equality or referential equality depending the type of operands.  
- When using `==` for the primitive-type objects, it acts as a structural equality (i.e., value comparison).
- When using `==` for the reference-type variables, it implies a referential equality.

Some examples of primitive and reference types are shown in the table below:
| Kind of type             | Example                                      | The meaning of `==`         |
| -------------- | --------------------------------------- | --------------- |
| primitive type | `int`, `boolean`, `double`, `char`      | Comparing values            |
| reference type | `String`, `Customer`, `List`, arrays, general objects | Comparing whether two references point to the same object |
