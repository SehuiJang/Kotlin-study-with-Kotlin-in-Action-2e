# What chapter in 'Kotlin in Action'?
- Chapter 4.4.3 Companion objects as regular objects (2/e version)

# What did I look at?
- A *companion object* is essentially a regular object declared inside a class.
- You do not have to give a companion object a name, but you can name it just like a regular object if you want to.
  - Unlike regular nested objects, you can access members of a companion object using only the class name.
  - If you want, you can also use the companion object's own name when accessing its members, just as you would with a regular object.
  - If you do not explicitly name a companion object, its default name is `Companion`.
- A companion object can also implement interfaces.
  - A class itself can, of course, implement an interface.
  - However, the companion object inside that class can implement an interface instead.
  - In that case, the companion object itself can be used as a value (a companion-object instance). For example, it can be passed as an argument to a function.
- You can also define an extension function that can be called using the class name itself rather than an instance of the class.
  - First, the class must have a companion object.
  - Then, define the extension function on `<ClassName>.Companion`.
  - After that, the extension function can be called as `<ClassName>.<FunctionName>`.

# Codes
## Named companion object: You can give a name to a companion object!
A companion object is still an object declared inside a class.  
- A companion object can also be given a name.
- However, unlike a regular nested object, a companion object does not have to be explicitly named. If no name is specified, its default name is `Companion`.

When calling a member of a companion object, you can use both the containing class name and the companion object's name.  
However, you can also omit the companion object's name and use only the class name.  

Using the example from the second edition of `Kotlin in Action`, you can write code like the following.
```kotlin
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

fun main() {
    val person =
        Person.Loader.fromJSON("""{"name": "Dmitry"}""")
    println(person.name)
    // Dmitry

    val person2 =
        Person.fromJSON("""{"name": "Brent"}""")
    println(person2.name)
    // Brent
}
```

### A few examples of companion objects in the Kotlin standard library
`Random` from the Kotlin standard library is one example.
```kotlin
import kotlin.random.Random

fun main() {
    val chance = Random.nextInt(from = 0, until = 100)
    val coin = Random.Default.nextBoolean()
    val coin2 = Random.nextBoolean()
}
```

## Implementing interfaces in a companion object
Consider the following example.

```kotlin
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

fun main() {
    loadFromJSON(Person2.Companion)
    loadFromJSON(Person2)

    val factory1: JSONFactory<Person2> = Person2
    val factory2: JSONFactory<Person2> = Person2.Companion

    println(factory1 === factory2)
    // true
    // `Person2` and `Person2.Companion` refer to the same companion-object instance.
}
```

In this example, the structure of the `Person2` class can be represented as follows.
```text
Person2 class
│
├── name
│
└── Companion object
        │
        └── implements JSONFactory<Person2>
```

As you can see from the source code, the companion object implements the interface.  
The `Person2` class itself could implement the interface instead. In that case, every instance of `Person2` would also be a `JSONFactory<Person2>`.  
In the current code, however, it is the companion object of `Person2` that implements `JSONFactory<Person2>`. Therefore, instances of `Person2` are not themselves `JSONFactory<Person2>` values.

### Using a companion-object instance as a value
Take a look at the `main` function in the example above.  
You can use the companion object of the `Person2` class itself as a value (a companion-object instance).  
This is the same as with regular objects.

The first argument of `loadFromJSON` must therefore be an instance of `JSONFactory<Person2>`, and the companion object can be passed as that value.  
In other words, you can pass the companion object as an argument to a function as follows.
- The explicit form is `Person2.Companion`.
- However, Kotlin also allows you to omit the companion object's name and write only the containing class name.
```kotlin
loadFromJSON(Person2.Companion)  // explicit form
loadFromJSON(Person2)            // allowed
```

In fact, `Person2.Companion` and `Person2`, when used as values in this context, both refer to the same companion object.
```kotlin
val factory1: JSONFactory<Person2> = Person2
val factory2: JSONFactory<Person2> = Person2.Companion

println(factory1 === factory2)
// true
```

### Then, does `Person2` mean a class or a companion-object instance?
In this example, how `Person2` is interpreted depends on the context in which it appears in the source code.  

First, in cases like the following, `Person2` refers to the class/type.
```kotlin
val p: Person2
Person2("Alice")
```

By contrast, when `Person2` is used as a value, as in `loadFromJSON(Person2)`, it refers to the companion object (i.e., `Person2.Companion`). Member access such as `Person2.fromJSON(...)` similarly allows the companion-object member to be accessed through the containing class name.
- You can also write the companion name explicitly as `Person2.Companion` when appropriate, although Kotlin normally allows the shorter class-name syntax.
```kotlin
Person2.fromJSON(...)
loadFromJSON(Person2)
```

## Companion-object extensions
Normally, when you define an extension function for a class, the function can be called on an instance of that class.  

Then, can you also define an extension function that can be called using only the class name itself? Can you define an extension function whose call syntax looks like that of a companion-object member?  
&rarr; Yes!

You can define a companion-object extension function as follows.
- Suppose there is a class named `C` and that the class has a companion object, which may be empty.  
- Also suppose you want to define an extension function named `func`.
- In that case, define `func` as an extension function on `C.Companion`. In other words, define `C.Companion.func`.
  - The extension receiver in this case is `C.Companion`.
- You can then call it as `C.func(...)`.
  - You can also use the explicit form `C.Companion.func(...)`.

Using an example based on the second edition of `Kotlin in Action`, you can write the following.
```kotlin
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
    val p = Person3.Companion.fromJSON("""{"name": "Ben"}""")
    println("${p.firstName} ${p.lastName}")
    // Ben Last_temp

    val pp = Person3.fromJSON("""{"name": "Bob"}""")
    println("${pp.firstName} ${pp.lastName}")
    // Bob Last_temp
}
```

The important point is the following.
```text
To define companion-object extension functions for a class, that class must have a companion object.
At minimum, an empty companion object is required.
```

### Caution
Like other extension functions, a companion-object extension function cannot access the receiver's `private` members merely because it is an extension.  
- A companion-object extension may look like a member of the companion object, but it is actually a function declared outside the companion object.
- Therefore, being an extension of the companion object does not give it access to the companion object's `private` members.

In addition, inside a companion-object extension, `this` (i.e., the extension receiver) refers to the companion-object instance, not to an instance of the containing class.  
- A companion object has no implicit reference to an instance of the containing class. Therefore, a companion-object extension function cannot directly access instance members of the containing class. It can, however, call an accessible constructor of the containing class, such as a `public` constructor.
- The extension function can also be written to explicitly receive an instance of the containing class as an argument. In that case, it can access the members of that instance that are visible from the extension function's declaration site.

```kotlin
class Person(val name: String) {
    fun greet() {
        println("Hello, I'm $name")
    }

    companion object
}

fun Person.Companion.create(): Person {
    return Person("Alice") // Allowed
}

fun Person.Companion.test() {
    println(name)   // Not allowed
    greet()         // Not allowed
}
```

Note that an actual member function of a companion object, rather than an extension function, is slightly different.
- An actual member function of the companion object can explicitly receive an instance of the containing class as an argument.
- Because the companion object is declared within the containing class, such a member function can access members of that instance according to the containing class's visibility rules, including its `private` members.

# Side note: Kotlin companion objects and static members
## How to access the companion object for a class from Java code?
To use a Kotlin companion object from Java, you generally access the companion-object instance through the static field generated for it, and then call its member.  
For example, suppose you have the following class.
```kotlin
class Person(val name: String) {
    companion object Factory {
        fun create(name: String): Person =
            Person(name)
    }
}
```

To call `create`, which belongs to the companion object of `Person`, you can write the following.
- In Kotlin:
    ```kotlin
    val p = Person.create("Alice")
    ```
- In Java:
    ```java
    Person p = Person.Factory.create("Alice");
    ```

From Java's point of view, the relevant part of `Person` can be thought of approximately as follows. Because this companion object is explicitly named `Factory`, the generated static field is also named `Factory`. An unnamed companion object would instead be accessed through `Companion`.  
```text
Person
 └─ static field Factory
          │
          └─ create(...)   // companion-object instance method
```

## Utilizing `@JvmStatic`
This annotation can be applied to member functions and properties of Kotlin objects and companion objects.  
It is intended to make Kotlin/JVM declarations interoperate more naturally with Java by adjusting the Java-facing JVM API generated from Kotlin code.  

In particular, Java code may need to call member functions declared in a Kotlin object or companion object.  
`@JvmStatic` can make the Java-facing API more convenient.
- For a function in a companion object, it allows Java code to call the function as if it were a static method of the containing class.

The role of `@JvmStatic` becomes intuitive with an example.  
Suppose the following class is defined.
```kotlin
class Person(val name: String) {
    companion object {
        @JvmStatic
        fun create(name: String): Person =
            Person(name)
    }
}
```

To call `create`:
- In Kotlin, the call remains the same:
    ```kotlin
    val p = Person.create("Alice")
    ```
- In Java, the following syntax becomes available:
    ```java
    Person p = Person.create("Alice");
    ```

When `@JvmStatic` is applied to a member function of a companion object, the compiler generates an additional static method in the containing class for Java interoperability. The original instance method in the companion object also remains available.  
Conceptually, the relevant part of `Person` can therefore be viewed from Java as follows.
```text
Without annotation:

Person
 └─ Companion
      └─ create()

--------------------------------

With @JvmStatic:

Person
 ├─ static create()      ← additional static method for Java
 │
 └─ Companion
      └─ create()        ← original companion-object member still exists
```

In this way, `@JvmStatic` does not turn the Kotlin companion-object function itself into a fundamentally different kind of Kotlin function.  
- Here, the function is still a member function of the companion object from Kotlin's point of view.

Instead, the compiler generates an additional JVM representation that allows Java code to use it like a static method.  
This improves Java interoperability.

### `@JvmStatic`: a regular object vs. a companion object
When `@JvmStatic` is applied to a function in a regular named `object`, the function is generated as a static method in the object's JVM class, without a separate instance method for that function.

A companion object behaves differently. When `@JvmStatic` is applied to one of its functions, the compiler generates an additional static method in the containing class while keeping the original instance method in the companion object.

```text
Regular object:

Obj
└── static create()


Companion object:

ContainingClass
├── static create()
│
└── Companion
    └── create()       // instance method remains
```

### Applying `@JvmStatic` to properties of objects or companion objects
You can also apply `@JvmStatic` to a property of an object or companion object.  
This does not expose the property itself as a Java field. Instead, its getter and, for a `var`, setter are generated as static methods in the relevant Java-facing class.

For example, suppose you have the following Kotlin code.
```kotlin
class Example {
    companion object {
        @JvmStatic
        var count = 0
    }
}
```

In Java, you cannot access `count` as `Example.count`. Instead, you can use the following syntax.
- You can use only the containing class name, without going through `Companion`.
```java
Example.getCount();
Example.setCount(10);
```

In other words, when `@JvmStatic` is applied to a property in a companion object, static accessor methods are generated for the property.  
If you want to expose a property directly as a field to Java code, you can use `@JvmField` instead.  
This is explained in the next section.

## Utilizing `@JvmField`
`@JvmField` can be applied to eligible Kotlin properties that have backing fields.  
It can be used for properties of regular classes as well as properties declared in objects and companion objects.

### When applying `@JvmField` to properties of regular classes
Suppose `Person` is defined in Kotlin as follows:
```kotlin
class Person {
    val name: String = "Alice"
}
```

To access `name` from Java, you normally use its getter:
```java
person.getName();
```

By contrast, if you use the `@JvmField` annotation, the `Person` class becomes:
```kotlin
class Person {
    @JvmField
    val name: String = "Alice"
}
```

In that case, Java can access `name` directly as follows:
```java
person.name;
```
In other words, when a property is annotated with `@JvmField`, it is exposed directly as a field in the Java API instead of being accessed through generated getter/setter methods.

### When applying `@JvmField` to properties of an object or companion object
You can also use this annotation on eligible properties declared in an object or companion object.

For example, suppose you have the following `Person` class:
```kotlin
class Person {
    companion object {
        @JvmField
        val DEFAULT_NAME = "Unknown"
    }
}
```

In Kotlin, the syntax for accessing `DEFAULT_NAME` is the same whether or not the annotation is present.  
In Java, however, `@JvmField` allows you to access the field directly instead of calling a getter:
```java
String name = Person.DEFAULT_NAME;
```

From Java's point of view, the generated API is conceptually similar to the following.
```java
public static final String DEFAULT_NAME = "Unknown";
```

## Summary with an example
The following example summarizes the differences.
```kotlin
// Kotlin
class Person(val name: String) {
    companion object {
        // Ordinary companion-object member
        fun createNormally(name: String) =
            Person(name)

        // Exposed as a static method to Java
        @JvmStatic
        fun create(name: String) =
            Person(name)

        // Exposed as a static field to Java
        @JvmField
        val DEFAULT_NAME = "Unknown"
    }
}
```

The corresponding Kotlin and Java syntax is as follows.
```kotlin
// Kotlin
val p1 = Person.createNormally("Alice")
val p2 = Person.create("Bob")
val name = Person.DEFAULT_NAME
```

```java
// Java
// Ordinary companion member
Person p1 = Person.Companion.createNormally("Alice");

// @JvmStatic
Person p2 = Person.create("Bob");

// @JvmField
String name = Person.DEFAULT_NAME;
```
