# What chapter in 'Kotlin in Action'?
- Chapter 4.2 Declaring a class with nontrivial constructors or properties (2/e version)
- Chapter 4.2.1 Initializing classes: Primary constructor and initializer blocks (2/e version)

# What did I look?
- A *primary* constructor of a class, which cannot have its body
  - including its explicit syntaxes
- Using an annotation or a visibility modifier on a class
- `init` block in the class body that is executed when the class is initialized by the *primary* constructor
  - `init` block itself is not the body of the *primary* constructor.
- Initializing the arguments of a constuctor of superclass

# The unique property of class constructors in Kotlin
Classes in object-oriented languages usually have one or more constructors.  
Then, Kotlin is unique in that it differentiates between a *primary* constructor and a *secondary* constructor.
- A *primary* constructor (What we study in the chapter 4.2.1):
  - It is the main and concise manner to initialize a class.
  - It is declared outside of the class body.
- A *secodary* constructor:
  - It is declared inside of the class body.

# Codes
In this part, let's cover a variety of syntaxes of a *primary* constructor.

## The simplest syntax
```kotlin
class User(val nickname: String)
```

The primary constructor has a parameter: `nickname`.  
Note that `val` means that a property with the identical name (i.e., `nickname`) is declared and initialized with the corresponding constructor parameter.  

Thus, if you invoke the constructor and pass a `String` value (e.g., `"Alice"`) to `nickname`, the following events occur:
1. The class property of which name is `nickname` is declared (i.e., generated).
2. The property `nickname` is initialized with `"Alice"`.

## The most explicit, unpacked syntax
Now, let's unpack the above simplest syntax!  
Please pay attention to the keywords: `constructor` and `init`.

```kotlin
class User constructor(_nickname: String) { // primary constructor
	val nickname: String  // class property

    init {  // initializer block
		nickname = _nickname
    }
}
```
- Note that the part `constructor(_nickname: String)` located in the class header is a *primary* constructor.
  - A class is able to have only one primary constructor.
  - As you can see, the `constructor` keyword begins the declaration of a primary/secondary constructor.
- There is an `init` block (i.e., initializer block) in the class body.
  - The `init` block runs right after the execution of the primary constructor.
  - You can utilize it for various purposes:
    - initializing one or more class properties
    - adding a few lines of code for validation or logging
  - If needed, you can use multiple `init` blocks. The multiple `init` blocks would be executed in the order they were written in your code.
  - The `init` block is unnecessary.

### Using a same name `nickname` for both a constructor parameter and a class property
You can also consider the following code.  
Remember that `this` is recommended to avoid the ambiguity.
```kotlin
class User constructor(nickname: String) {
    val nickname: String

    init {
        this.nickname = nickname
    }
}
```

### Class having multiple `init` blocks & multiple property initializers
Note that:
- You can utilize multiple `init` blocks and multiple property initializers.
- The order of execution is equal to the order they are written in your code.

There is an example:
```kotlin
class User(val nickname: String) {
    val normalizedNickname = nickname.trim().lowercase()

    init {
        println("First init: $normalizedNickname")
    }

    val displayName = normalizedNickname.replaceFirstChar { it.uppercase() }

    init {
        println("Second init: $displayName")
    }
}

fun main() {
    User("  ALICE  ")
	// First init: alice
	// Second init: Alice
}
```
- In first, the primary constructor `(val nickname: String)` is executed first of all.
  - There is a `val` keyword in the constructor.
  - Accordingly, a property `val nickname` is created with its initial value `"  ALICE  "`.
- Subsequently, the things written inside the class body are executed in order.
  - (1) The property initializer of `val normalizedNickname`
  - (2) The upper `init` block containing `println("First init: $normalizedNickname")`
  - (3) The second property initializer of `val displayName`
  - (4) The lower `init` block containing `println("Second init: $displayName")`


## A slightly simpler syntax: property initializer
```kotlin
// 1. No `init` block is used because there is a one-to-one correspondence between a class property and a constructor parameter.
// 2. A `constructor` keyword can be omitted.
class User(_nickname: String) {
    val nickname = _nickname	// property initializer
}
```
Note that:
- In this example, a constructor parameter simply corresponds to a class property.
  - You can directly initialize the class property `nickname` with the constructor parameter `_nickname`, without `init` blocks.
- If you do not use annotations or visibility modifiers on the primary constructor, you can omit the `constructor` keyword.

## Default values for constructor parameters
Similar to function parameters, class constructor parameters can have its default value.  

```kotlin
class User(
	val nickname: String,
	val isSubscribed: Boolean = true
)
```

## Creating an instance of a class
Directly calling the class constructor is the way to create an instance.  
```kotlin
class User(
	val nickname: String,
	val isSubscribed: Boolean = true
)

fun main() {
	val alice = User("Alice")
	println(alice.isSubscribed)
	// true
	val bob = User("Bob", false)
	println(bob.isSubscribed)
	// false
	val carol = User("Carol", isSubscribed = false)	// explicitly writing the argument name.
	println(carol.isSubscribed)
	// false
	val dave = User(nickname = "Dave", isSubscribed = true)
	println(dave.isSubscribed)
	// true
}
```

## Initializing the constructor arguments of a superclass
Consider a case where your class inherits from a superclass, and the superclass has its constructors with one or more arguments.  
- The primary constructor of your class have to initialize them.
- You can apply the values passed to the primary constructor parameters to the superclass constructor parameters.

```kotlin
open class User(val nickname: String) { /* ... */ }

class SocialUser(nickname: String) : User(nickname) { /* ... */ }
```
- Note that the primary constructor of `SocialUser` doesn't has the `val` keyword.
  - `SocialUser` itself does not declare its `nickname` property.
- If you instantiate the `SocialUser` class and pass `"Alice"` to `nickname`:
  - Firstly, the primary constructor's parameter `nickname` gets `"Alice"`.
  - Secondly, the constructor of the superclass `User` gets `"Alice"` as a value of its argument.
  - Thirdly, the constructor of `User` is executed. A class property `val nickname` of `User` is created and initialized with `"Alice"`.
  - Fourthly, `SocialUser` inherits the property `val nickname = "Alice"` from `User`.
  - As a result, you can access to `val nickname` through the instance of `SocialUser`.

## Default constructor
When you declare a class, it is possible not to declare any constructors for it.  
In this case, a default constructors would be generated.
- It does nothing.
- It has no parameters.

```kotlin
// the class without constructors -> a default constructor is created.
open class Button

// When other classes inherit from `Button`, you must invoke the (default) constructor of `Button`.
// The parentheses mean calling the constructor.
class RadioButton: Button()
```

### How about `interface`?
It is worthy remembering that interfaces don't have constructors.  
Therefore, **if a class is declared and implements one or more interfaces, parentheses are not needed.**
- When a class inherits from a superclass, you have to provide parentheses after the superclass reference in the base class list, even though the constructor of the superclass has no parameter.
  - The parentheses mean 'the constructor'.
- **When a class implements an interface, you never write parentheses after the name of the interface.**
  - **That indicates that interfaces do not have constructor, so that parentheses are not required.**

## `private constructor`
Sometimes you can want for your class not to be instantiated by code outside the class.  
If so, `private constructor` can be considered.

```kotlin
class Secret private constructor(private val agentName: String) {}
```
- In this code, the primary constructor is `private`.
- Companion objects, which is covered in later chapters, are able to invoke such constructor.


# Note
## When you assign default values to all the constructor parameters
In this situation, the compiler will generate an additional constructor.  
The additional one has no parameters but uses all the default values.  
That makes you instantiate classes through parameterless constructors.  

## Using an annotation `@JvmOverloads` on your class constructor
Imagine that you have Java code, and the code has to invoke a Kotlin constructor with some default parameters.  
In this case, you have to add an annotation `@JvmOverloads` to the constructor.  
- It have the compiler create the appropriate overloads intended for Java users.
- Note that **`@JvmOverloads` cannot be used for constructors without default arguments.**

```kotlin
class User @JvmOverloads constructor(_nickname: String = "ABC") {
    val nickname = _nickname
}
```

## Alternatives to `private constructor`s
You can utilize `private constructors` to prevent your class from being instantiated by external code.  
This way is effective also in Java.  

Using `private constructors` in Java is considered when:
1. The class is is just a container of static utility members.
	- In Kotlin, you can declare top-level functions rather than a class with `private constructor`s.
2. The class is a singleton, that is, it allows creating only one instance.
	- In Kotlin, `object` declarations is a good alternative.
	- We are going to study `object` in later chapters.
