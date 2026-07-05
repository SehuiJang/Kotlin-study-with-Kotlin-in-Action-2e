# What chapter in 'Kotlin in Action'?
- Chapter 4.1 Defining class hierarchies (2/e version)
- Chapter 4.1.1 Interfaces in Kotlin (2/e version)

# What did I look?
- Declaring an interface which has one or more methods
  - Abstract method: a method without its body and implementation
  - Interface (non-abstract) method: a method with a default implementation
- A class implementing an interface
- The `override` modifier is *mandatory* in Kotlin for safety.
- The `super` keyword with a base type name in `<>`: you can invoke the inherited implementation.
- When a class implements interfaces or other classes:
  - Interfaces &rarr; it can implement one or more interfaces.
  - Classes &rarr; it can implement only one class. That is, a class can have **only one superclass**!

# Codes
## Declaring and implementing an interface
The example code shows that:
- (1) a `Clickable` interface is declared,
- (2) and the interface is implemented by a `Button` class.
```kotlin
interface Clickable {
    fun click()
    // equivalent to `fun click() : Unit`
}

// The class `Button` implements an interface `Clickable`.
class Button : Clickable {
    override fun click() = println("I was clicked")
}

fun main() {
    Button().click()
    // I was clicked
}
```
- The `Clickable` interface has a single abstract method (`click`).
  - The methods in interfaces is `public open` by default, while methods in classes is `public final`.
- The `Button` class is a non-abstract class, and implement the `Clickable` interface.
  - This class must implement the `click` method.
- Just add a colon `:` after the class name, when you want for a class (e.g., `Button`) to:
  - (1) implement an interface (e.g., `Clickable`)
  - (2) or inherit from a superclass.
- Note that **the `override` modifier is mandatory in Kotlin.**
  - It prevents you from an accidental and unintended overriding.
  - If you have no intention to override a method, you must rename it.

### How many interfaces or classes can a class implement/subclass?
When it comes to interfaces:
> A class can implement one or more interfaces at the same time.

However, when a class do subclassing (i.e., inherit from a superclass),
> A class is able to extend only one class.  
> In other words, a class has only one parent class.

Accordingly, the following example is possible:
```kotlin
interface Clickable {
    fun click()
}

interface Focusable {
    fun setFocus(focused: Boolean)
}

open class View

class Button : View(), Clickable, Focusable {
    override fun click() {
        println("Clicked")
    }

    override fun setFocus(focused: Boolean) {
        println("Focus: $focused")
    }
}

class Button2 : View() {
    fun hello() {
        println("Hello, Button2!")
    }
}
```
- The `Button` class inherits from a `View` class and two interfaces: `Clickable` and `Focusable`.
- The `Button2` class falls under a case of single inheritance.

However, it is not allowed:
```kotlin
open class View
open class Widget

class Button : View(), Widget()
```
- The `Button` class has two superclasses. &rarr; NOT permitted!


### Does the `click` method really return *nothing*?
Technically, it returns a value: `kotlin.Unit`.
- Its type is called `Unit`.

Considering that `void` in Java means nothing is returned, `Unit` in Kotlin seems to be similar to that.  
However, unlike `void` in Java, `Unit` is a real type.
- There is a single value of the `Unit` type: `kotlin.Unit`.
- Unlike `void` in Java, `Unit` can be used as a type argument!
- Therefore, `Unit` is a **full-fledged type**.
- `Unit` represents the absence of a meaningful return value, but it is not the same as `null`.
  - The `null` is closed to "no value", "no object/instace", and "no reference".
  - The `Unit` has a value, but it is just meaningless.

With `Unit`, it is possible:
```kotlin
// equivalent to fun click(): {...}
fun click(): Unit {
    println("I was clicked")
}

fun main() {
    val result: Unit = click()  // I was clicked
    println(result) // kotlin.Unit
}
```

---

## Interface method with a default implementation
### Let's consider two interfaces
```kotlin
interface Clickable {
    // regular method declaration
    fun click()     // abstract method

    // This method has a default implementation.
    fun showOff() = println("I'm clickable!")   // interface (non-abstract) method
}

// This interface has a method `showOff` of which name is the same as the one in `Clickable`.
interface Focusable {
    fun setFocus(b: Boolean) =
        println("I ${if (b) "got" else "lost"} focus.")

    fun showOff() = println("I'm focusable!")
}
```
- As for the `Clickable` interface, there are two methods:
  - `click`: it is an abstract method in that it has no function body.
  - `showOff`: it is a non-abstract method, and it has a default implementation.
- If a class subtypes the `Clickable` interface,
  - it is necessary for the class to provide an implementation for `click`.
  - On the other hand, it is not mandatory to implement `showOff` because of its default implementation. If you like that, you can skip writing down the implementation.
- **Note that the `Focusable` interface also contains a non-abstract method `showOff`.**
  - If you implement both `Clickable` and `Focusable`, it is required to explicitly implement the behavior of `showOff`.
  - Unless you do that, you would encounter a compiler error.

### Subtyping two interfaces and Resolving conflicting method implementations
As you can see in the code below, the class `Button` implements two interfaces.  
Its supertypes are `Clickable` and `Focusable`.
```kotlin
// The class `Button` implements two interfaces.
// supertype: Clickable and Focusable
class Button : Clickable, Focusable {
    override fun click() = println("I was clicked")

    // Both two supertype (interface) has the member `showOff`
    // Thus, you have to implement `showOff` in this class.
    override fun showOff() {
        // super<SuperType> -> it specifies the parent 
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }

    // Or you can choose one of the inherited implementations like this:
    // override fun showOff() = super<Clickable>.showOff()
}

fun main() {
    val button = Button()
    button.showOff()
    // I'm clickable!
    // I'm focusable!
    button.setFocus(true)
    // I got focus.
    button.click()
    // I was clicked.
}
```
- Remember that both `Clickable` and `Focusable` include a conflicting method `showOff()`.
- That is why the `Button` class must determine its own algorithm of `showOff()`.
  - Otherwise, you face a compiler error:
    ```text
    example2.kt:19:1: error: class 'Button' must override 'showOff' because it inherits multiple interface methods for it.
    class Button : Clickable, Focusable {
    ^^^^^^^^^^^^
    ```
- In above code, `showOff()` of `Button` calls the `showOff()` methods inherited from both `Clickable` and `Focusable`.
  - You can also consider the expression-body syntax if you want to choose one of them (`super<Clickable>.showOff()` and `super<Focusable>.showOff()`).
- When it comes to `setFocus(b: Boolean)`, the default implementation from `Focusable` is automatically used unless you provide the explicit implementation.

---
---

# When you declare an interface in Kotlin but implement that on Java...
In this section, we consider the three cases:
- Declaring interface in Kotlin + implementing it on Kotlin
- Declaring interface in Java + implementing it on Java
- Declaring interface in Kotlin + implementing it on Java

## Declaring interface in Kotlin + implementing it on Kotlin
It is already discussed above.  
Assume that there is an interface with an abstract and a non-abstract method.  
If a class is to implement the interface,
- it is an obligation to provide an implementation of the abstract method.
- it is optional to newly implement the interface method.
  - If not providing an implementation, the default implementation would be used.

For example, it is allowed:
```kotlin
interface Clickable {
    fun click() // abstract method

    fun showOff() = println("I'm clickable!")   // interface method
}

class Button : Clickable {
    override fun click() = println("I was clicked")
}
```

## Declaring interface in Java + implementing it on Java (after Java 8)
This case is similar, too.  
In Java, it can be expressed roughly like this:
```java
interface Clickable {
    void click();

    default void showOff() {
        System.out.println("I'm clickable!");
    }
}

class JavaButton implements Clickable {
    @Override
    public void click() {
        System.out.println("I was clicked");
    }

    // You can omit implementing `showOff`
}
```
- The `default` method in Java has its default implementation.
  - Java compiler treats it as a default method which subclasses can inherit.

## Declaring interface in Kotlin + implementing it on Java
> **You have to pay attention to this case!**

Long story short, you have to make an implementation for all the methods from the interface.
- Both abstract method and interface method (i.e., non-abstract method)
- Even though interface methods have the default implementations, Java compiler cannot recognize that they are the default implementation of the corresponding interface methods.

Let's see an example.  
In Kotlin, you declared an interface containing an abstract and non-abstract method:
```kotlin
interface Clickable {
    fun click()
    fun showOff() = println("I'm clickable!")
}
```

From the perspective of Java, the interface from Kotlin would be seen roughly like this:
```java
public interface Clickable {
    void click();

    void showOff();

    public static final class DefaultImpls {
        public static void showOff(Clickable $this) {
            System.out.println("I'm clickable!");
        }
    }
}
```
- It has a form of a combination of a regular interface and a helper class.
  - Here, the methods in this Java interface have no default implementation.
  - The class has static methods whose bodies are corresponding to the default implementation of non-abstract methods (interface methods) in Kotlin.
    - In above example, the `DefaultImpls` has a static method `showOff`.
    - The body of this static method is corresponding to the default implementation of `showOff` in Kotlin.
    - However, unfortunately, Java compiler do not know that the static method is the default implementation of `showOff` from Kotlin `Clickable`.
- Therefore, Java compiler just thinks:
  - "there are two methods (i.e., `click` and `showOff`) from the Kotlin-compiled interface `Clickable`",
  - "but they do not have the default implementation, thus they are all abstract methods."
  - "I have to ask the programmer for the implementation for both methods."

- Well, it would be nice if:
  ```text
  Java compiler treats the static method as the default implementation of `void showOff();`
  OR
  Java compiler can accept the `showOff` method in Kotlin-compiled interface as the `default` method of Java `Clickable` interface
  ```
  - However, that is not the case in reality.

Thus, when you implement the `Clickable` interface in Java, you must implement both `click` and `showOff`:
```java
class JavaButton implements Clickable {
    @Override
    public void click() {
        System.out.println("I was clicked");
    }

    @Override
    public void showOff() {
        System.out.println("I'm showing off");
    }
}
```

In conclusion,
> **If you want to implement a Kotlin interface in Java, you must write down the implementation of all the methods from the Kotlin interface.**
> The default implementations of methods in Kotlin-compiled interfaces are not automatically passed down to classes in Java.