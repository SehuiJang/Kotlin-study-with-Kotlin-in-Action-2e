# What chapter in 'Kotlin in Action'?
- Chapter 4.5 Extra type safety without overhead: Inline classes (2/e version)

# What did I look at?
- How to declare a class as an *inline class*: the `@JvmInline` annotation and the `value` keyword
- Requirements for a class to qualify as an *inline class*
  - It must have exactly one property initialized in its primary constructor.
  - It must be `final` and cannot be a subclass of another class.
- What *inline classes* are allowed to do
  - They can implement interfaces.
  - They can define methods.
  - In addition to the single property initialized through the primary constructor, they can declare properties in the class body. However, those properties must be computed on the fly and cannot have backing fields.

# Introduction: When are *inline classes* helpful?
Using an example from the second edition of `Kotlin in Action`, consider the following simple situation.  
Imagine that you are an American and you define a function like this: `fun addExpense(expense: Int)`  

The function is intended to process expenses from your everyday life in the United States.  
It receives an `Int` value representing an expense and treats that value as an amount in US cents. If you live in the United States, using this unit may feel natural enough that you do not think much about it.  

One day, you travel to Japan.  
You visit a restaurant and spend ¥200.  
Then, you call `addExpense(200)` to record the expense.  
However, in this situation, `200` represents JPY, not US cents.  
Because `addExpense` simply accepts a single `Int`, the call still succeeds without any error, and the function treats the value as 200¢.  

## How to prevent this situation?
As in this example, an `Int` value with a meaning other than US cents can accidentally be passed to `addExpense`.  
One way to prevent this is to wrap the `Int` value in a class called `UsdCent`.  
```kotlin
class UsdCent(val amount: Int)
```

Then, change the signature of `addExpense`.  
In other words, change the parameter type from `Int` to `UsdCent`:
```kotlin
fun addExpense(expense: UsdCent) {}
```

This prevents values of unrelated types from being passed to `addExpense` and silently interpreted as US cents.  
In this example, note that the wrapper type is `UsdCent`, while its underlying type is `Int`.  

## But what about performance?
With a regular wrapper class, each expense value requires a `UsdCent` instance to be created. In this example, that would normally mean creating a wrapper object whenever `addExpense` is called.  
If this function is called frequently, a large number of short-lived `UsdCent` objects may be allocated and later garbage-collected.  
Creating and collecting many short-lived wrapper objects can introduce unnecessary run-time overhead.  

This is where *inline classes* are useful.  
They can provide additional type safety while allowing the compiler to avoid wrapper-object allocation in many cases.
- You can expect reduced allocation overhead and memory usage when the value can be represented using its underlying type.
- In such unboxed cases, the run-time representation can be close to using the underlying type directly.

# Codes
## How to convert a class into an *inline class*?
To declare an *inline class* for the JVM, you need to do both of the following.
- Annotate the class with `@JvmInline`.
- Mark the class with the `value` keyword.

Following the example in `Kotlin in Action`, you can declare an *inline class* as follows.  
```kotlin
@JvmInline
value class UsdCent(val amount: Int)

fun addExpense(expense: UsdCent) {
    // save the expense as USD cent
    println("${expense.amount}¢ is saved!")
}

fun main() {
    addExpense(UsdCent(147))
    // 147¢ is saved!
}
```

At run time, instances of `UsdCent` can be represented using their underlying property wherever possible. In this example, they can be represented as `Int` values at their usage sites.  
In other words, the data of a `UsdCent` instance can be described as being *inlined* at the places where it is used.  

However, there are also cases where an *inline class* instance must retain its wrapper representation instead of being represented directly as the underlying type. These cases are discussed in the [Side note](#side-note) below.  

## Rules when it comes to *inline classes*
### Qualifications of *inline classes*
If you want to make a class an *inline class*, keep the following requirements in mind.  
- The class must have **exactly one property initialized in its primary constructor**.
- The class cannot participate in a class hierarchy as a subclass. It is always `final` and cannot extend another class.

### Things permitted for *inline classes*
The following are still allowed for *inline classes*.
- *Inline classes* can implement interfaces.
- *Inline classes* can define methods.
- In addition to the single property initialized through the primary constructor, they can declare properties in the class body. **However, such properties must be computed properties and cannot have backing fields.**

Based on `Kotlin in Action`, the following code is also valid.  
- Even when using *inline classes*, you can still write code in an object-oriented style.
```kotlin
interface PrettyPrintable {
    fun prettyPrint()
}

@JvmInline
value class UsdCent(val amount: Int) : PrettyPrintable {
    val salesTax
        get() = amount * 0.06
    override fun prettyPrint() = println("${amount}¢")
}

fun main() {
    val expense = UsdCent(1_99)
    println(expense.salesTax)
    // 11.94
    expense.prettyPrint()
    // 199¢
}
```

# Side note
The Kotlin compiler prefers to represent an inline class using its underlying type wherever possible.  
- This helps produce optimized code with less wrapper-object allocation.
- In the previous example, the underlying type of `UsdCent` is `Int`.

However, there are cases where the wrapper representation needs to be kept instead of using the underlying type directly.
- You can find examples in the Kotlin documentation at: <https://kotlinlang.org/docs/inline-classes.html#representation>
- For convenience, the same example code is also included below.

In the following source code, there are several situations where the Kotlin compiler needs to use the wrapper representation of the inline class. In other words, the inline class is *boxed*.  
As a rule of thumb, this happens when an inline-class value is used as another type.  
The examples below include the following cases.
1. The inline class is used as a **type parameter** (that is, as a generic type).
2. The inline class is used as an **interface type**.
3. The inline class is used as its **nullable type**.
   - An inline class type and its nullable form may require different run-time representations.
   - For example, `Foo` and `Foo?` are treated differently in this context.


```kotlin
interface I

@JvmInline
value class Foo(val i: Int) : I

fun asInline(f: Foo) {}
fun <T> asGeneric(x: T) {}
fun asInterface(i: I) {}
fun asNullable(i: Foo?) {}

fun <T> id(x: T): T = x

fun main() {
    val f = Foo(42)

    asInline(f)    // unboxed: used as Foo itself
    asGeneric(f)   // boxed: used as generic type T
    asInterface(f) // boxed: used as type I
    asNullable(f)  // boxed: used as Foo?, which is different from Foo

    // Below, 'f' is first boxed when passed to 'id' and then unboxed when returned from 'id'.
    // In the end, 'c' contains the unboxed representation (just '42'), like 'f'.
    val c = id(f)
}
```

# Side note2: Project Valhalla
As of 2026, Kotlin/JVM inline value classes are primarily implemented through special handling by the Kotlin compiler rather than through a stable, generally available JVM feature that directly corresponds to Kotlin's inline value classes.  
Project Valhalla is developing JVM and Java support for value classes and value objects, with the goal of allowing the JVM to understand value-oriented types more directly.  

## Kotlin feature vs. JVM feature
First, it is useful to distinguish Kotlin's compile-time type semantics from the JVM representation produced for execution.  
Using the previous `UsdCent` example, the distinction can be summarized as follows.  
```text
Kotlin source / compile-time semantics
──────────────────────────────────────
Kotlin treats UsdCent as a distinct type from Int.

Example:
fun addExpense(expense: UsdCent)

addExpense(UsdCent(200))  // Good!
addExpense(200)           // Bad: compile error

Even though the wrapped property's type is Int,
UsdCent and Int represent different Kotlin types with different meanings.

-> The compiler prevents values with the wrong semantic meaning
   from being passed accidentally.
-> Type safety!


Runtime representation
──────────────────────────────────────
For Kotlin/JVM, the compiler determines how UsdCent is represented
in the generated JVM code: whether a wrapper representation is required
or whether the underlying Int can be used directly.

(1) If UsdCent can be unboxed and represented as Int:

UsdCent(200)
    ↓
represented effectively as Int 200 at run time

This can avoid allocating a separate UsdCent wrapper object.
-> Object allocation and the associated overhead are reduced.
-> Performance!

(2) If boxing is required:

UsdCent is represented using its wrapper form.

In this case, wrapper-object allocation may be necessary,
so some allocation overhead can remain.
```

In an ordinary JVM program, the compiler determines how source-language constructs are translated into JVM-compatible code, and the JVM then executes the generated representation.  
For Kotlin inline value classes, the Kotlin compiler currently performs special translation to decide where values can use an unboxed underlying representation and where a boxed wrapper representation is required.  

## How are inline classes currently processed?
As of 2026, Project Valhalla's value-class support is still evolving rather than being a finalized, universally available JVM mechanism that Kotlin can simply rely on by default.  
Therefore, Kotlin/JVM currently implements inline value classes through compiler-level transformations.
- The Kotlin compiler enforces the compile-time semantics of the value class as a distinct Kotlin type.
- It also chooses an appropriate JVM representation when generating code.
  - Where possible, it uses the underlying type directly.
  - Where necessary, it uses the generated wrapper representation.
- The `@JvmInline` annotation explicitly marks a Kotlin value class as using Kotlin's current JVM-specific inline representation.

The current situation can be pictured informally as follows.
```text
JVM:
  "I do not have a stable JVM feature that directly represents
   Kotlin's inline value class exactly as Kotlin defines it."
  "I don't know and cannot understand it."

Kotlin compiler:
  "That's fine. I'll translate it into JVM-compatible forms,
   using the underlying value where possible and a wrapper where necessary."
```

Conceptually, the current compilation flow looks like this.
```text
[Kotlin source]
    │
    │ @JvmInline
    │ value class UsdCent(val amount: Int)
    ▼
[Kotlin compiler]
    │
    │ "This is a JVM inline value class, which is different from a regular class."
    │ "I'm about to treat it specially."
    │ "Choose an appropriate JVM representation."
    │ "Use Int directly where possible; otherwise use the wrapper."
    ▼
[JVM-compatible generated code]
    │
    ▼
[JVM]
```

## What may change with Project Valhalla?
Project Valhalla is working toward native JVM support for value classes and value objects.  
If Kotlin adopts this JVM support in a future backend, some of the compiler-specific machinery currently required for Kotlin/JVM inline classes may be reduced or changed.  

A simplified conceptual picture would look like this:
```text
Kotlin compiler:
  "This is a value class."
  "This Kotlin type should use the JVM's value-class capabilities."

JVM:
  "I know that."
  "I understand value-oriented classes (i.e., inline classes) and can provide native support for them."
```

A possible future compilation model can therefore be illustrated as follows:
```text
[Kotlin source]
    │
    │ value class UsdCent(val amount: Int)
    ▼
[Kotlin compiler]
    │
    │ translates the Kotlin value class
    │ to the JVM's native value-class facilities
    ▼
[JVM]
    │
    │ native value-class support
    ▼
runtime representation managed with JVM value-class capabilities
```

The second edition of `Kotlin in Action` notes that a future Valhalla-based implementation could allow Kotlin/JVM inline classes to be declared without `@JvmInline`.  
However, the exact integration between future Valhalla features and Kotlin remains an implementation and language-design matter, so this should be understood as a future direction rather than a guarantee about the final syntax.
