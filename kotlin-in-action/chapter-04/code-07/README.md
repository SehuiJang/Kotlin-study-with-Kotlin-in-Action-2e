# What chapter in 'Kotlin in Action'?
- Chapter 4.2.2 Secondary constructors: Initializing the superclass in different ways (2/e version)

# What did I look?
- *Secondary* constructors of a class
- The way to invoke a superclass constructor through secondary constructors.

# Secondary constructors
## TIP: When secondary constructors are unnecessary?
In Kotlin, classes with multiple constructors are more uncommon than in Java.  
Please recall that Kotlin supports default parameter values and named argument syntax.  
> You don't need to declare secondary constructors for overloading or setting default parameter values.  
> Instead, the syntax of named argument and default parameter value will help you.

## When secondary constructors are useful?
During programming, you can face situations when your class certainly needs multiple constructors.  
For example, it is sometimes possible that you want for your class to have constructors to initialize the class in various manners.  
- Especially, all constructors could have different types of parameters each other.

At times like that, if you want a class with two or more constructors, you can consider secondary constructors.
- They are declared in the class body and have their own body.

Let's see some examples introduced in 'Kotlin in Action 2/e'.

# Codes
## Java example
```java
import java.net.URI;

public class Downloader {
    public Downloader(String url) {
        // some code
    }
    public Downloader(URI uri) {
        // some code
    }
}
```
- You can see two constructors.
  - The upper one has a parameter being a `String` type.
  - The lower one takes a `URI` instance.

## Kotlin example
Rewriting the above source code in Kotlin gives us the following:
```kotlin
import java.net.URI

open class Downloader {
    // secondary constructor 1
    constructor(url: String?) {
        // some code
    }

    // secondary constructor 2
    constructor(uri: URI?) {
        // some code
    }
}
```
- The `Downloader` class has no primary constructor.
  - You cannot see parentheses after the class name.
- Instead, it has two secondary constructors.
  - Please look at the `constructor` keywords inside the class body.
  - As you can see, secondary constructors are declared in the class body and have their own body.
  - You can add secondary constructors whenever you want.

## Extending the `Downloader` class: constructor delegation
Let's suppose that there is a `Downloader` class, and it inherits from `Downloader`.  
Just as with a primary constructor, secondary constructors must initialize the constructor of the superclass.  
- Each secondary constructor can call the corresponding constructor of your superclass.
- You can freely determine what superclass constructor the secondary constructor invokes.
```kotlin
class MyDownloader : Downloader {
    constructor(url: String?) : super(url) {
        // ...
    }
    constructor(uri: URI?) : super(uri) {
        // ...
    }
}
```
- Please focus on the `super` keyword.
  - You can use `super` for invoking the superclass constructor.
- Note that there are a few relationship of delegation.
  - (1) `constructor(url: String?)` of `MyDownloader` is delegated to `super(url: String?)` of `Downloader`.
  - (2) `constructor(url: URI?)` of `MyDownloader` is delegated to `super(url: URI?)` of `Downloader`.
- It is worthy of remembering that `super` is executed before `constructor` of `MyDownloader`.


## Another option for constructor delegation
There is another option.
- You can delegate a constructor of your own class to another constructor of the same class.
- If you want for a constructor to be delegated to another constructor of the same class, just use `this` instead of `super`.
- **Each secondary constructor must finally reach `super` by any means.**
```kotlin
class MyDownloader : Downloader {
    constructor(url: String?) : this(URI(url))
    constructor(uri: URI?) : super(uri)
}
```
- We can express the delegation flow between the constructors:
  - `this` and `super` mean the constructor of `MyDownloader` and `Downloader`, respectively.
  - `this(String?)` &rarr; `this(URI?)` &rarr; `super(URI?)`
  - `super(String?)` (no delegation)
- If you instantiate `MyDownloader` with a `String` instance, three constructors will be executed.
  - The execution order:
    > `super(URI?)` &rarr; `this(URI?)` &rarr; `this(String?)`
  - The execution order is the reverse of the one you saw on the delegation map.

# Note
## When a class without a primary constructor inherits from a superclass
As in above examples, it is possible that a class has no primary constructor but secondary constructors.  
In such class, it is mandatory that each secondary constructor initializes the base class (i.e., call the constructor of the superclass) or delegates to another constructor that does so.  

## When a class with a primary constructor inherits from a superclass
When your class has both a primary constructor and secondary constructors, remember that:
- The primary constructor is in charge of calling the superclass constructor (i.e., `super`).
- Secondary constructors just invoke the primary constructor `this(...)` of the same class.
- The delegation flow of constructors might be as follows:
```text
Secondary constructor 1 ↘
Secondary constructor 2  → A primary constructor → A superclass constructor
Secondary constructor 3 ↗
```

### Caution
In such type of case, it is impossible that secondary constructors directly choose and invoke `super`.  
That is, the following way is not allowed:
```kotlin
class MyDownloader(uri: URI?) : Downloader(uri) {
    constructor(url: String?) : this(URI(url))

    constructor(rawUri: CharSequence?) : super(URI(rawUri.toString())) {
        // ERROR
    }
}
```

Note that the primary constructor is in charge of calling a superclass constructor as long as the primary constructor exists.  
Even so, you can make secondary constructors invoke other secondary constructor, using `this`.
- Importantly, regardless of intermediate path, secondary constructors need to reach the primary constructor in the end.
- In other words, the delegation chain (i.e., flow) must eventually reach the primary constructor.
