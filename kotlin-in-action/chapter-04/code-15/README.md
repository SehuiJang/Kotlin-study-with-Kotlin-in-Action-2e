# What chapter in 'Kotlin in Action'?
- Chapter 4.4.2 Companion objects: A place for factory methods and static members (2/e version)

# What did I look at?
- The `companion object` keyword.
  - Like a nested object, it can access `private` members of the containing class.
  - A `companion object` does not necessarily need to have its own object name.
  - Its properties and member functions can be accessed from outside using only the class name.
  - It is especially useful for providing a natural API for factory methods.
  - Members of a `companion object` cannot be accessed through an instance of the containing class.

# Introduction to `companion object`: Why is `companion object` necessary?
## There is no `static` keyword in Kotlin
Unlike Java/C++, Kotlin does not have a `static` keyword.  
Instead, Kotlin relies on package-level functions and `object` declarations. For simple utility functions, top-level functions are generally preferred.  

### < When using a top-level function >
For example, in Java, if you need simple `static` methods that do not need to store any state, you might create a utility class like the following.
```java
// Java
class StringUtils {
    static String normalize(String s) {
        return s.trim().toLowerCase();
    }
}
```
In Kotlin, by contrast, you can use a top-level function in situations where Java would typically use a `static` method.
```kotlin
// Kotlin
fun normalize(s: String): String =
    s.trim().lowercase()

fun main() {
    println(normalize("  Kotlin  "))
    // kotlin
}
```
---
### < When using an `object` declaration >
If you want to group shared values and behavior in one place, Java often uses `static` fields and `static` methods.  
Since Kotlin does not have a `static` keyword, you can use an `object` declaration instead.  
- In practice, Java classes with `static` fields/methods and Kotlin `object` declarations can serve similar purposes.
- However, they are not the same concept. A Java class with `static` fields/methods can still have instances, whereas a Kotlin `object` is itself a true singleton object (instance).
```kotlin
object AppConfig {
    val maxRetries = 3

    fun printConfig() {
        println("maxRetries = $maxRetries")
    }
}

fun main() {
    println(AppConfig.maxRetries)
    AppConfig.printConfig()
}
```

## Limitation of top-level functions
However, top-level functions declared outside a class cannot access the class's `private` members.  
- When programming, you may want to make a class constructor `private` so that callers cannot invoke the constructor directly. In such a case, you may instead want to provide separate *factory methods* responsible for creating objects.
- In that situation, the *factory methods* need to be able to access the class's `private` members, including its `private` constructors.
- However, as shown in the example below, a top-level function cannot access a class's `private` members.

```kotlin
class User private constructor(
    val name: String
)

// top-level function used as factory function
fun createUser(name: String): User {
    return User(name)  // (X) Cannot access private constructor
}
```

**This is one situation where a `companion object` is useful in Kotlin.**

## How to implement a function accessing `private` members of a class? How to implement factory methods?
If you want to implement factory methods, you can do so as follows.

### The case of Java
In Java, you can place a `static` method inside the class as shown below.  
This factory method can then be called through the class name.

```java
// Java
class User {
    private final String name;

    private User(String name) {
        this.name = name;
    }

    // factory method
    public static User create(String name) {
        return new User(name);
    }
}

class Main {
    // The main function
    public static void main(String[] args) {
        // Using the factory method
        User user = User.create("Alice");
    }
}
```
---
### The case of Kotlin: nested `object`
In Kotlin, you can create a factory method using a nested `object`.
- A nested object can also access `private` members of the containing class.

```kotlin
class User private constructor(
    val name: String
) {
    object Factory {
        // A factory method
        fun create(name: String): User {
            return User(name)   // call a private constructor
        }
    }
}

fun main() {
    // Utilizing the factory method
    val user = User.Factory.create("Alice")
    println(user.name)
}
```

### The case of Kotlin: using a `companion object`
Although a nested object can be used to implement a factory method, you can also use a `companion object`.  
- The difference is mainly in the syntax used to call the factory method.
- You can call the factory method using only the class name, without having to specify a separate object name.
```kotlin
class User private constructor(
    val name: String
) {
    companion object Factory {
        fun create(name: String): User {
            return User(name)
        }
    }
}

fun main() {
    // Utilizing the factory method
    val user = User.create("Alice")
    println(user.name)
}
```

**In short, you can consider using a companion object when you want the API to look like an object-creation method provided directly by the class (type) itself.**


# Codes
According to `Kotlin in Action`, the following is an example related to `companion object`.  
It is similar to the example in the previous *Introduction* section.

## Declaring `companion object` inside a class
```kotlin
class MyClass {
    companion object {
        fun callMe() {
            println("Companion object called")
        }
    }
}

fun main() {
    // It can call the member function of the `companion object`, using just the class name.
    MyClass.callMe()
    MyClass.Companion.callMe()
    // Companion object called

    // Caution: It is not allowed to access the companion object's members through instances of the class.
    val myObject = MyClass()
    // myObject.callMe()   // NOT allowed!
    // Error: Unresolved reference: callMe
}
```
- You can call members of the `companion object` using only the name of the containing class.
- You can also access them in the form `<ClassName>.Companion.<MemberName>`.

### Caution
Suppose you create an instance of the containing class.  
You cannot use that instance to access members of the `companion object`.  
In this respect, Kotlin companion-object members differ from Java static members.

---
## Multiple secondary constructors vs. Factory methods
Suppose you declare a class and want to allow its instances to be created using either an `Int` or a `String`.  
Also suppose you want the behavior to differ slightly depending on whether the constructor receives an `Int` or a `String`.

### Using multiple secondary constructors
One possible approach is to define two secondary constructors as follows.

```kotlin
fun getSocialNetworkName(accountId: Int): String =
    "kodee$accountId"

class User1 {
    val nickname: String

    // secondary constructor 1
    constructor(email: String) {
        nickname = email.substringBefore('@')
    }

    // secondary constructor 2
    constructor(socialAccountId: Int) {
        nickname = getSocialNetworkName(socialAccountId)
    }
}

fun main() {
    val subscribingUser1 = User1("bob@gmail.com")
    val socialUser1 = User1(4)
    println(subscribingUser1.nickname)
    // bob
    println(socialUser1.nickname)
    // kodee4
}
```

---

### Using factory methods
Another approach is to define only a `private` primary constructor and provide two factory methods.  

```kotlin
fun getNameFromSocialNetwork(accountId: Int): String =
    "kodee$accountId"

class User2 private constructor(val nickname: String) {

    companion object {
        fun newSubscribingUser(email: String) =
            User2(email.substringBefore('@'))

        fun newSocialUser(accountId: Int) =
            User2(getNameFromSocialNetwork(accountId))
    }
}

fun main() {
    // Calling the methods of the `companion object` via the class name
    val subscribingUser2 = User2.newSubscribingUser("bob@gmail.com")
    val socialUser2 = User2.newSocialUser(4)
    println(subscribingUser2.nickname)
    // bob
    println(socialUser2.nickname)
    // kodee4
}
```

# Side Note - Constructors vs. Factory methods: Which is more useful?
As discussed earlier, object (instance) creation can be provided in the following two ways.
- Using secondary constructors.
- Defining factory methods as member functions of a `companion object`.

The following sections describe situations where factory methods are more useful, as well as situations where constructors may be preferable.

## When factory methods are more useful (1)
Factory methods are useful when you want to give different creation methods names that reflect their purposes.
- Unlike constructors, factory methods are ordinary member functions of a `companion object`.
- Therefore, you are free to give them names that describe their purposes.
- This can also be seen in the examples above.
    - [Example of constructors](#using-multiple-secondary-constructors) &rarr; Constructors cannot have custom names.
    - [Example of factory methods](#using-factory-methods) &rarr; Factory methods can be given explicit, descriptive names.

## When factory methods are more useful (2)
A constructor always creates an instance of the class to which it belongs.  
A factory method, however, is simply a function (member function) inside a `companion object`, so its return type can be chosen more flexibly.
- In particular, as shown in the toy example below, the return type of a member function in a `companion object` (e.g., a factory method) does not have to be the containing class itself.
- If desired, a factory method can return a subclass.
    ```kotlin
    open class User
    class SubscribingUser : User()
    class SocialUser : User()

    class UserFactory {
        companion object {
            fun create(type: String): User {
                return if (type == "subscribing") {
                    SubscribingUser()
                } else {
                    SocialUser()
                }
            }
        }
    }
    ```

## When factory methods are more useful (3)
Another advantage of factory methods is that they do not necessarily have to create a new object every time.
- Calling a constructor creates a new instance.
- With factory methods, if you manage a collection such as a `MutableMap` inside a `companion object`, you can check whether a corresponding object has already been created before creating a new one. The toy example below demonstrates this.
    - Suppose each email corresponds to exactly one instance. In other words, assume there is a one-to-one correspondence between an email and a `User` instance.
    - In this case, when the factory method receives an email, it can check whether a `User` instance corresponding to that email already exists in the `users` collection.
    - If a `User` instance corresponding to the email is already stored in the collection, unnecessary object creation can be avoided.
    ```kotlin
    class User private constructor(val email: String) {
        companion object {
            private val users = mutableMapOf<String, User>()

            fun getUser(email: String): User {
                return users.getOrPut(email) {
                    User(email)
                }
            }
        }
    }
    ```

## When factory methods are less useful than constructors?
Sometimes constructors are the better choice.  
If you are designing a class hierarchy and want each subclass to define its own construction behavior naturally, constructors—and, when polymorphic behavior is needed, regular member functions—are generally more appropriate than relying on member functions of a `companion object`.  

### Unallowed way
You cannot override a member function of a superclass's `companion object` from the `companion object` of a subclass as shown below.  
If you want to make active use of inheritance and `override`, regular member functions are the appropriate mechanism, while constructors can be defined separately by each subclass and chained to superclass constructors.  
- The important point is that the `companion object` of `SpecialUser` does not inherit from the `companion object` of `User`.
  - These two `companion object` declarations simply define two separate singleton objects.
```kotlin
open class User {
    companion object {
        fun create(): User = User()
    }
}

class SpecialUser : User() {
    companion object {
        override fun create(): SpecialUser = SpecialUser()
        // NOT permitted
    }
}
```

### Allowed way
Unlike companion-object members, constructors participate in class construction through constructor delegation, while regular member functions participate in inheritance and overriding.  
Therefore, as shown below, a subclass can define its own constructor and invoke a superclass constructor.  

```kotlin
open class User(val name: String)

class SpecialUser(name: String) : User(name)
```

Regular member functions can also be inherited and overridden as shown below.
```kotlin
open class Parent {
    open fun foo() {
        println("Parent")
    }
}

class Child : Parent() {
    override fun foo() {
        println("Child")
    }
}
```
