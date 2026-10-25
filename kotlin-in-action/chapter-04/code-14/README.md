# What chapter in 'Kotlin in Action'?
- Chapter 4.4 The object keyword: Declaring a class and creating an instance, combined (2/e version)
- Chapter 4.4.1 Object declarations: Singletons made easy (2/e version)

# What did I look at?
- The `object` keyword. You can make singletons!
  - It is helpful to think of it as combining a class declaration with the creation of an instance.

# Introduction to the uses of the `object` keyword
1. *Object declaration* &rarr; a way to declare a singleton.
2. *Companion objects* &rarr; they can contain factory methods as well as other methods related to the containing class itself.
    - In other words, their members can be called through the name of the containing class rather than through an instance of that class.
    - In terms of usage, they are similar to static member functions and variables in Java/C++. However, properties and functions of a Kotlin companion object cannot be accessed through an instance of the containing class.
3. *Object expressions* &rarr; they can be used instead of Java anonymous inner classes.

For reference, this document focuses on *object declarations*.

# Introduction to object declarations
## The *singleton* pattern in other languages
When designing object-oriented systems, you may sometimes need a class for which only one instance should exist.  
In Java, you can create a class with a `private` constructor and then define a static field that holds the single instance of that class.  

## *Object declarations* in Kotlin
Kotlin provides first-class language support for creating singletons in such situations: the *object declaration* feature.  
In other words, it is a feature intended for cases where you want to define a class that has only one instance.  
You can think of it as combining a *class declaration* with the declaration of a *single instance*.  

The following points are worth remembering.
- An object cannot have constructors.
- Unlike regular classes, you cannot create additional instances by calling a constructor.
- An object declaration provides the single instance as part of the declaration itself. The object is initialized lazily when it is first accessed.

# Codes
## Object declaration
An object declaration is created using the `object` keyword.  
Like a class declaration, an object can contain properties, member functions, initializer blocks, and so on.  
**However, an object cannot have constructors.**  
- The singleton instance is provided directly by the object declaration, so you do not create it separately by calling a constructor.
- With a regular class, you can declare the class in one place and create instances elsewhere by calling its constructor. In this respect, an object differs from a regular class.  

```kotlin
class Person

object Payroll {
    val allEmployees = mutableListOf<Person>()

    fun calculateSalary() {
        for (person in allEmployees) {
            /* ... */
        }
    }
}

fun main() {
    Payroll.allEmployees.add(Person())
    Payroll.calculateSalary()
}
```

As shown in the code above, you can use the object name to access its properties or call its member functions.  

## Object and inheritance
When declaring an object, you can inherit from a class or implement interfaces.  
When using a framework, you may need to implement a particular interface. However, the resulting implementation may not need to store any separate state.  
- For example, an interface may define a `compare` function that you need to implement.
- Comparing two objects does not necessarily require storing any additional state (i.e., data).
- In such a situation, you can consider using an object instead of a regular class.

```kotlin
import java.io.File

object CaseInsensitiveFileComparator : Comparator<File> {
    override fun compare(file1: File, file2: File): Int {
        return file1.path.compareTo(file2.path, ignoreCase = true)
    }
}

fun main() {
    println(
        CaseInsensitiveFileComparator.compare(
            File("/User"), File("/user")
        )
    )
    // 0

    val files = listOf(File("/Z"), File("/a"))
    println(files.sortedWith(CaseInsensitiveFileComparator))
    // [/a, /Z]
    // `sortedWith` takes a `Comparator<...>` as its argument.
}
```

An important point is that a singleton object can be used anywhere an ordinary object instance of the appropriate type can be used.  
- For example, in the code above, `sortedWith` expects a `Comparator` object (instance) as its argument.
  - As an aside, `files.sortedWith(Comparator)` sorts the `files` list according to the given `Comparator` and returns the resulting sorted list.
- In this example, `CaseInsensitiveFileComparator` implements the `Comparator` interface. The object declaration also provides a single singleton instance of `CaseInsensitiveFileComparator`.
- Therefore, when `CaseInsensitiveFileComparator` is used as an expression, its name refers to that singleton instance, which implements `Comparator<File>`. This means the object itself can be passed directly as an argument to `sortedWith`.

### Caution
If an object implements an interface or inherits from a class, it must provide implementations for all inherited abstract properties and abstract member functions.  
An object declaration represents a concrete singleton instance, so it cannot leave abstract members unimplemented.  

## Nested objects
In short, you can also declare objects inside a class.  
- Each nested object still has exactly one instance.
- Each instance of the containing class does not get its own separate instance of the nested object.

An important point is that you cannot access the nested object through an instance of the containing class.  
Instead, you access it through the name of the containing class.  
The following example illustrates this.

```kotlin
class Container {
    object Shared {
        var count = 0
    }
}

fun main() {
    val instance1 = Container()
    val instance2 = Container()

    instance1.Shared   // NOT allowed
    instance2.Shared   // NOT allowed
    Container.Shared   // Correct usage
}
```

The structure can be illustrated as follows.
- In particular, the instances do not own separate nested objects.

```text
// Good representation
Container class
│
├── instance1 : Container's instance
│
├── instance2 : Container's instance
│
└── Shared : singleton object
```

```text
// Bad representation
instance1
└── Shared #1

instance2
└── Shared #2
```

### Nested objects do not refer to outer instances
A nested object does not refer to an outer instance of the containing class.
- This behavior is similar to that of a nested class and is the opposite of an `inner class`.

For example, the following is not possible.

```kotlin
class Container(val name: String) {
    object Shared {
        fun printName() {
            // println(name) // CANNOT use outer instance's property.
        }
    }
}
```

Suppose three instances of the `Container` class are created as follows.  
The `Shared` object does not belong separately to any of these instances. Instead, only one `Shared` object exists for the entire `Container` class. Therefore, from the perspective of `Container.Shared`, there is no particular `Container` instance whose `name` property it could refer to.

```kotlin
Container("Alice")
Container("Bob")
Container("Charlie")
```

### Example in `Kotlin in Action`
Following the example in `Kotlin in Action`, a nested object can be used as shown below.  
If you have a class and want to define a `Comparator<...>` that compares instances of that class, you can declare the comparator as a nested object.
- Notice that this nested object does not access properties of an outer instance. Instead, it receives two separate instances and compares them.
- There is no separate `NameComparator` belonging to `Person("Bob")` and another one belonging to `Person("Alice")`. There is simply one comparator named `Person.NameComparator`.

```kotlin
// Nested object
data class Person2(val name: String) {
    object NameComparator : Comparator<Person2> {
        override fun compare(p1: Person2, p2: Person2): Int =
            p1.name.compareTo(p2.name)
    }
}

fun main() {
    val persons = listOf(Person2("Bob"), Person2("Alice"))
    println(persons.sortedWith(Person2.NameComparator))
    // [Person2(name=Alice), Person2(name=Bob)]
}
```

# Side Note: How to use Kotlin `object` from Java?
If you declare an `object` in Kotlin and compile it, it become a class which have a static field `INSTANCE`.  
- `INSTANCE` holds a singleton instance of the class.

Thus, you can access the field `INSTANCE` in Java when you are about to use a Kotlin `object` from Java.  
Here are a few examples.
```Java
// Java
CaseInsensitiveFileComparator.INSTANCE.compare(file1, file2);
Person.NameComparator.INSTANCE.compare(person1, person2)
```