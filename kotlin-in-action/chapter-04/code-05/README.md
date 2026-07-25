# What chapter in 'Kotlin in Action'?
- Chapter 4.1.5 Sealed classes: Defining restricted class hierarchies (2/e version)

# What did I look?
- Sealed class & a modifier `sealed`
  - The `sealed` modifier connote that the class is `abstract`.
  - Thus, you can declare `abstract` members in the `sealed class`.
- All *direct* subclasses of a `sealed` class:
  - **are supposed to be known at compile time.** That is, it means:
    > The compiler has to know all the possible kinds of the direct subclass of the `sealed` class  
    > and be able to nail down the list of the direct subclasses **in compile time.**
  - **must be declared in the same package** as the `sealed` class.
  - have to belong to **the identical module**.


# Codes
## The case of `interface Expr`
In some previous chapters, we handled an example to encode expressions such as `(1 + 2) + 4`.
```kotlin
interface Expr

class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr

fun eval(e: Expr): Int =
    when (e) {
        is Num -> e.value
        is Sum -> eval(e.left) + eval(e.right)
        else -> throw IllegalArgumentException("Unknown expression")
    }
```
- Note that `eval` function has an expression body, not block body.
  - The return value of the `when` expression is directly used as the return value of `eval` function.
  - The `when` expression must be exhaustive. In other words, it has to handle all the possible cases.
  - To make an exhaustive `when` expression, **the `else` branch is necessary** in preparation for the case in which none of the other branches match.

However, it is somewhat inconvenient that you must add a default branch (i.e., `else` branch).  
Moreover, imagine what happens if you declare a new class `Mul` implementing `Expr`.
- You have to append a new branch `is Mul` to the `when` expression in `eval` function.
- But it is possible for you to forget doing it, causing subtle bugs.
  - Even if you forget, the compiler will not alert you.
  - Instead of compile error, the source code will just run, and the default branch would be chosen if you pass a `Mul` instance to the `eval` function.
  - Well, you could think it would be convenient if the compiler can inform the fact you did not add a `is Mul` branch by mistake.


In this situation, using `sealed class` rather than `interface` would be a good approach.

### Why do you have to use an `else` branch for the `when` expression in `eval`?
According to the above example, you can think:
- It is clear that only two subclasses (i.e., `Sum` and  `Num`) implement the `interface Expr`.
- Thus, the `else` branch might be unnecessary if both `Sum` and `Num` are handled in the `when`.

However, the compiler does not think so.  
Let's assume the above source code belongs to a module `A`.  
When the compiler compiles a module `A`, it will think:
- Now, I am compiling a module `A`.
- In this compilation, the `interface Expr` has two direct subclasses: `Num` and `Sum`.
- Even so, other person developing another module `B` can declare new class `Mul` in `B`, import `interface Expr` from the module `A`, and make `Mul` implement the `interface Expr`.
  - The other direct subclass of `Expr` can be created in other module that is compiled later!
- Thus, some different classes other than `Sum` and `Num` can be passed to the `eval` function, in future.
- It would be good that I forces programmers to mandatorily add an `else` branch to `eval` function.
  - in order to handle the case in which the `eval` function gets `Expr`'s subclasses other than `Num` and `Sum`.

## The case of `sealed class`

```kotlin
sealed class Expr

class Num(val value: Int) : Expr()
class Sum(val left: Expr, val right: Expr) : Expr()

fun eval(e: Expr): Int =
    when (e) {
        is Num -> e.value
        is Sum -> eval(e.left) + eval(e.right)
    }
```

When it comes to the `sealed class`, note that:
- It represents a closed class hierarchy.
- Direct subclasses of the `sealed class` have to be declared in **the same package and module as the `sealed class`.**
- All direct subclasses of the `sealed class` **have to be known at compile time.**
  - When compiling a module, the compiler know all the possible direct subclasses of the `sealed class`.
  - The compiler **must be able to determine the complete set** of classes that directly inherit from the sealed class while compiling the module.

In above source code, the only types that `e` can accept are `Num` and `Sum`.
- Even if someone creates new module in future, new direct subclasses of `sealed class Expr` cannot be declared within the new module.

### The viewpoint of the compiler regarding `sealed class Expr`
Let's think about the viewpoint of the compiler.  
When you mark `Expr` as `sealed class`, the compiler might think that:
- In Kotlin, it is mandatory that programmers declare all the *direct* subclasses in the same package and module as `sealed class Expr`.
  - Thus, it is certain that the set consisting of `Num` and `Sum` is the set of all the possible subclasses of `Expr`.
  - Nobody can create additional subclasses of `sealed class Expr` in other modules & packages, even in future.
- Therefore, either `Sum` or `Num` will be passed to the subject `e` of the `when` expression in the `eval` function, in runtime.
- Moreover, the `when` expression has two branches: `is Num` and `is Sum`.
  - Thus, I am confident that this `when` expression is exhaustive, although it has no `else` branch!
- Accordingly, I do not throw any exception related to the `when` expression within the `eval` function.

### What happens if just defining a new direct subclass of `Expr` and recompiling?
Consider the following source code:
```kotlin
sealed class Expr

class Num(val value: Int) : Expr()
class Sum(val left: Expr, val right: Expr) : Expr()
class Mul(val left: Expr, val right: Expr): Expr()

fun eval(e: Expr): Int =
    when (e) {
        is Num -> e.value
        is Sum -> eval(e.left) + eval(e.right)
        // ERROR
    }
```

The compiler grasps that the direct subclasses are only `Num`, `Sum`, and `Mul`.  
However, the `when` expression has just two branches: `is Num` and `is Sum`.
- It means the `when` expression lost its exhaustiveness.
- The compiler would throw an exception that the `when` expression must be exhaustive.

There are two options for recovering the exhaustiveness of `when`:
1. just add an `else` branch.
    - It is not bad.
    - However, imagine that you declare a new subclass (e.g., `Div`) in the far future but forget adding the new branch `is Div` to the `when` expression.
    - In that case, the compiler will not alert you, resulting in subtle bugs in runtime.
2. add a new branch (`is Mul`).
    - Now that you introduce `sealed class` into the source code, **this option is recommended.**
    - Even if you don't add a default branch (i.e., `else` branch), the `when` expression becomes exhaustive after adding the `is Mul` branch.
    - In this case, if you make a new subclass (e.g., `Div`) of `Expr` and forget to construct the `is Div` branch to the `when` expression, the compiler will gladly inform you that the exhaustiveness of the `when` expression is required.


## You can also use the `sealed` modifier for interfaces!
There are not only `sealed` classes but also `sealed` interfaces.  
You can find the following example in 'Kotlin in Action 2/e':
```kotlin
sealed interface Toggleable {
	fun toggle()
}

// The following two classes implement the `sealed interface Toggleable`.
class LightSwitch: Toggleable {
	override fun toggle() = println("Lights!")
}

class Camera: Toggleable {
    override fun toggle() = println("Camera!")
}
```

Note that `sealed` interfaces are supposed to observe the same rules as `sealed` classes.
- The classes implementing a `sealed interface` must be within the same package and module as the `sealed interface`.
- After the module containing the `sealed interface` is compiled, new implementations cannot be provided anymore.
  - If you really wanna add new implementations of the interface, just revise the source code of the package including the `sealed interface` and recompile it.

