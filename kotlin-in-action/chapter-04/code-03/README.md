# What chapter in 'Kotlin in Action'?
- Chapter 4.1.3 Visibility modifiers: Public by default (2/e version)

# What did I look?
- Visibility modifiers: `public` (default), `internal`, `protected`, and `private`
- The visibility of all types appearing in a public API (i.e., function or class) must be at least as visible as the API itself.
  - All types in parameter list of a function must be as visible as the function itself.
  - Everything in the list of base types of a class is supposed to be as visible as the class itself.
  - The receiver type of an extension function have to be as visible as the function itself.
  - Generic type arguments can also be used as a part of the function signature. Thus, if a generic type argument is appeared in the function signature, the type in the argument also has not to be less-visible type than the function itself.
- The degree of visibility of a top-level function/class:
  - `public` > `internal` > `private`

# Visibility modifiers
The visibility modifiers in Kotlin are analogous to them in Java.  
If not specifying any visibility modifiers, `public` applies.  

When it comes to visibility inside a module, you can make use of `internal`.  
- *module*: A set of Kotlin files compiled together. A unit of compilation.
  - A Gradle source set, a Maven project, an IntelliJ IDEA module, etc.

| Modifier | Class members with that modifier | Top-level declarations with that modifier |
| --- | --- | --- |
| `public` (default) | Visible everywhere | Visible everywhere |
| `internal` | Visible in a module | Visible in a module |
| `protected` | Visible only in the class and subclasses | X |
| `private` | Visible only in a class (not subclasses) | Visible only in a file |

## Visibility from extension functions
```text
If you declare an extension function of a class,
the `protected` and `private` members of the class are not visible from the extension function.
```

Please recall that the extension function is actually a function with the first argument being an instance of the receiver type.
- The extension function is not a real member function of the class.
- The extension function is outside the class that is a receiver type.
- That is why the `protected` and `private` members of the class cannot be accessed by the extension function.

## In Kotlin, module-private visibility (`internal`) instead of package-private visibility
There is no concept of package-private visibility in Kotlin.
- It is the default visibility in Java.
- In Java, a package is not a strict boundary.
  - That is, external codes can declare packages having the identical package name, even though the external ones belongs to other projects, other modules, or other jar.
  - So, the external codes can also access to package-private member.
  - In other words, **bypassing is easy from the perspective of external code.**
- In short, package encapsulation in Java can be easily broken, even though there is package-private visibility in Java.
- Therefore, Kotlin does not accept the concept of package-private.
  - Note that the package is just a way to organize code in namespaces.  
  - In Kotlin, a package is NOT a unit for controlling visibility.
  - Instead, Kotlin introduces module-private visibility (`internal`).

\
Kotlin's `internal` means that classes or top-level functions with the modifier are visible inside the same module.
- Consider the following module structure in Kotlin.
    ```text
    my-library module
    └─ com.example.library.Helper  // internal

    my-app module
    └─ com.example.library.ExternalCode
    ```
    - Even if the side of `my-app` module uses the identical package name on purpose, accessing to `Helper` is not allowed since `Helper` belongs to other module `my-library`.
        ```kotlin
        // a source code (.kt) in my-app module
        package com.example.library

        fun test() {
            val helper = Helper() // not allowed (not visible)
        }
        ```
    - It is important whether they are the **same module**, not package.

### Java's package-private vs. Kotlin's `internal`
| Content                       | Java package-private | Kotlin `internal`        |
| ------------------------ | -------------------- | ------------------------ |
| Criteria                       | Identical package           | Identical module                |
| Can access only with the same package name? | Yes                   | No                      |
| Easy for external code to bypass?         | Relatively easy               | Difficult from the viewpoint of Kotlin compiler |
| Intended use                     | Hiding the internal implementations of packages     | Encapsulating the internal implementations of modules/libraries  |
| Is that Supported in Kotlin?           | NO                   | **YES**                       |

Notably,
```text
Java package-private:
"Accessable from the code with the same package name"

Kotlin internal:
"Accessable from the code compiled in the same module"
```

> **Kotlin's `internal` cannot be bypassed by package naming gimmick (i.e., by placing external code in the same package name).**


# Codes
## Using different visibility modifiers
```kotlin
internal open class TalkativeButton {
    private fun yell() = println("Hey!")
    protected fun whisper() = println("Let's talk!")
}

// Error: public member (= extension function `giveSpeech()`) exposes its `internal` receiver type `TalkativeButton`
fun TalkativeButton.giveSpeech() {  // Basically, no visible modifier == using `final` modifier
    yell()  // Error: `yell` is private in TalkativeButton
    whisper()   // Error: `whisper` is protected in TalkativeButton
}
```
- In above code, the extension function `giveSpeech` is a `public` top-level declaration.
  - Here, **public declaration** == **public member** means `giveSpeech()`.
- Note that:
    > Fundamentally, the `public` function (i.e., extension function `giveSpeech`) is exposing the `internal` type `TalkativeButton` as its receiver type. &rarr; **ERROR!**
- Well, it is worth recalling that the extension function is conceptually the function with its first argument being the receiver type.
  - In above code, `TalkativeButton.giveSpeech()` is conceptually close to `giveSpeech(receiver_obj: TalkativeButton)`.
  - Thus, `TalkativeButton` can be thought as the part of the function signature of `giveSpeech()`.
- Here, `giveSpeech` is `public` while `TalkativeButton` is `internal` (i.e., is less-visible type).
  - From the perspective of external code, `fun TalkativeButton.giveSpeech()` is viewed as a strange API `fun ???.giveSpeech()`.
  - Thus, Kotlin forbids the `public` member `giveSpeech` to use the **less-visible** type `TalkativeButton` in its 'function parameter, return type, receiver type, and so on'.
- Meanwhile, extension functions of a class cannot access to its `private` or `protected` members.
  - Calling the `protected whisper` & `private yell` from the extension function `giveSpeech` leads to an error.


### Thus, it is important:
```text
the base types (i.e., a superclass and interfaces) of a class, type parameters of a generic class or a generic function, and all types (including a receiver type) used in method signature
have to be as visible as the class/method itself.
```
- Easily speaking, you don't have to use `internal`/`private` types as a `public` function's parameters, return type, receiver type, and so on.
- Likewise, you don't have to use `internal`/`private` types, including classes, as a `public` class's base types, entire types in method signature, and so on.
- Also, there is a list of type parameters in class/function generics. You cannot use `internal`/`private` types for the `public` class/function generic's type parameters.  

### If you want to solve the error that occured in the above code,
There are two choices:
- making the `giveSpeech` extension function `internal`
- making the `TalkativeButton` class `public`

## Some other simple examples
### Example 1: parameter type
```kotlin
internal class InternalConfig

fun setup(config: InternalConfig) {
    // ...
}
```
- `setup()`: `public`
- `InternalConfig`: `internal`

When the external module calls `setup()`, an instance of `InternalConfig` is needed.
- However, the external module do not have access to the type `InternalConfig`.
- Thus, it is not allowed for the `public` function `setup(...)` to use `InternalConfig` as its parameter.

The solutions are one of following ways:
- modifying `fun setup(...)` to `internal fun setup(...)`
- modifying `internal class InternalConfig` to `public class InternalConfig`

### Example 2: return type
It is not different.  
Note that the function signature includes the **return type**.  

```kotlin
internal class InternalResult

fun calculate(): InternalResult {
    return InternalResult()
}
```

Assume that the external module invokes the `public` function `calculate`:
```kotlin
val result = calculate()
```
- To normally run this code, the external module must know `InternalResult`.
- However, `InternalResult` is `internal`, and thus it is not visible from the external module.

Therefore, you don't have to use `internal`/`private` class as the return type of the `public` function.

The solution is one of the following methods:
- using `public` class as the return type
- adding `internal` to `fun calculate()`

### Example 3: generic type argument (parameter)
In Kotlin, a generic type argument is a part of the function signature.

```kotlin
internal class InternalItem

fun loadItems(): List<InternalItem> {
    return listOf(InternalItem())
}
```
- The precise return type of `loadItems` is `List<InternalItem>`.
  - `InternalItem` is included in the function signature.
  - Thus, the `internal` type `InternalItem` is exposed to public API (i.e., the signature of `loadItems`).
- This example is similar to the above **Example 2**.

To solve this situation:
```kotlin
// make the function `loadItems` into `internal fun`
internal fun loadItems(): List<InternalItem> {
    return listOf(InternalItem())
}
```
OR
```kotlin
// use the `public class` rather than `internal class` for the generic type argument of the return type
public class PublicItem

fun loadItems(): List<PublicItem> {
    return listOf(PublicItem())
}
```

### Example 4: base type of a class
The list of base types can include:
- a superclass which a class inherits from
- interfaces which a class implements

```kotlin
internal interface InternalBehavior

class PublicButton : InternalBehavior {
    // ...
}
```
- `PublicButton`: `public`
- The base type `InternalBehavior`: `internal`

In this case, when external users (e.g., external modules) try to understand or extend the class hierarchy of `PublicButton`, they are not able to view `InternalBehavior`.  
Therefore, the base type must be as visible as the class itself (i.e., `PublicButton`).

To address this problem,
```kotlin
// make the class an `internal` one
internal class PublicButton : InternalBehavior {
    // ...
}
```
OR
```kotlin
// make the class implement a `public` supertype rather than an `internal` type
public interface PublicBehavior

class PublicButton : PublicBehavior {
    // ...
}
```

# Summary
```text
The visibility of all types appearing in a public API (e.g., function signatures and a list of base types)
must be at least as visible as the API (i.e. function or class) itself.
```
