# What chapter in 'Kotlin in Action'?
- Chapter 4.1.4 Inner and nested classes: Nested by default (2/e version)

# What did I look?
- Inner class & Nested class
- If you declare a class inside another class, it would be a nested class by default.
- If you want to declare an inner class, you need to explicitly mark an `inner` modifier.
- Difference between a nested and inner class
  - A nested class: a reference to an outer class is unavailable.
  - An inner class: it stores a reference to an outer class (`this@Outer_class_name`).

# Nested class & Inner class
## When they are needed?
- Encapsulating a helper class inside another class
- Keeping code close to where it is utilized

## Kotlin's nested class does not have `this@Outer_class_name`
Note that nested classes in Kotlin have no access to the outer class instance.
- You cannot make use of `this@Outer_class_name`.
- **Unlike in Java.**

If you wanna use `this@Outer_class_name`, you can declare an inner class, using an `inner` modifier.

## Syntax for a nested class & an inner class: In Java vs In Kotlin
Imagine that you would like to declare a nested class or an inner class inside other class.  
The name of the nested or inner class is `A`.  

In Java, if you declare a class with the syntax `class A` inside another class, it means an inner class. If you want a nested class, using the syntax `static  class A` is a solution.  

On the other hand, the syntax `class A` inside another class means a nested class. If you require an inner class, you can add an `inner` modifier before `class A`. That is, `inner class A` make you declare an inner class.  


| Nested / Inner | In Java | In Kotlin |
| --- | --- | --- |
| Nested class | `static class A` | `class A` |
| Inner class | `class A` | `inner class A` |

# Codes
## Scenario setting: Declaring an interface `View` with serializable state

```kotlin
interface State : Serializable

interface View {
    fun getCurrentState(): State
    fun restoreState(state: State) { /* ... */ }
}
```
- `State` is an interface that implements an other interface `Serializable`.
- `View` is an interface, and it has two methods.
  - `getCurrentState()`: it should return its state (type: `State`).
  - `restureState(...)`: it restores an already stored state.

Since now, you declare a class `Button` implementing the interface `View`.

## Implementing `View` in Java: introducing an inner class
Well, if you declare the class `Button` in Java, the code of `Button` would be as follows:
```java
/* Java */
public class Button implements View {
    @Override
    public State getCurrentState() {
        return new ButtonState();
    }

    @Override
    public void restoreState(State state) { /*...*/ }

    public class ButtonState implements State { /*...*/ }
}
```
What you have to notice, a class `ButtonState` is defined inside `Button`.  
- In Java, it is an inner class.
- **The inner class `ButtonState` implicitly stores a reference to `Button` class (`ButtonState`'s outer class).**

Considering the inheritance hierarchy, `ButtonState` class itself is **serializable**.  
However, importantly, `ButtonState` is implicitly holding a reference to `Button`, and `Button` is not serializable.
Therefore, if you run the following code in Java, you can encounter `java.io.NotSerializableException` because of `Button`.  
```java
Button button = new Button();
State state = button.getCurrentState();	// `state` is actually holding an instance of`ButtonState`.

serialize(state);
```
- Let us consider a function `serialize` and assume that it cannot get a non-serializable object.
- Well, you can expect:
  - "The instance of `ButtonState` is serializable."
  - "So, passing `state` into the function `serialize` would not cause a problem."
- Contrary to expectations, it can throw an exception (`java.io.NotSerializableException:Button`).
  - `ButtonState` is serializable, **yet the reference to the outer class `Button` is not.**
  - `ButtonState` is a non-static inner class, and thus it implicitly includes the reference to the enclosing class.
  - When trying to serialize a `ButtonState` instance, the serializer attempts to serialize the fields of `ButtonState`, including the hidden reference to `Button`.
  - As a result, `serialize(state)` leads to an error.

Even though the actual code do not show the reference to `Button` inside the inner class `ButtonState`, the source code of `ButtonState` is effectively the same as the code below:
```java
public class ButtonState implements State {
    // You cannot see the below line in the real source code but it effectively exists.
    private final Button outerButton;
}
```

Reflecting the internal implementation style of JVM, it is closed to the structure below:
```java
public class Button$ButtonState implements State {
    final Button this$0;

    public Button$ButtonState(Button outer) {
        this.this$0 = outer;
    }
}
```

### How to fix the problem?
It would be a good solution to get rid of the implicit reference from `ButtonState`.  
In Java, you can consider declaring a nested class, not inner class, by adding `static`.
- `public class ButtonState` &rarr; `public static class ButtonState`
- The `static` inner class (nested class) does not have the reference to the enclosing class.
  - It means that `ButtonState` is no longer tied to a certain `Button` instance.
  - Thus, you can serialize `ButtonState` instances independently.

```text
Before:
ButtonState -> hidden reference -> Button

After:
ButtonState
```

## Implementing `View` in **Kotlin**: introducing an nested class
When you implement `View` in Kotlin, the following code works:
```kotlin
class Button : View {
	override fun getCurrentState(): State = ButtonState()

	override fun restoreState(state: State) { /*...*/ }
	
	class ButtonState : State { /*...*/ }	// corresponds to a static nested class in Java
}
```

- Note that declaring `class ButtonState` inside another class without explicit modifiers means **a nested class**.
  - It corresponds to a static nested class in Java.
  - It has no implicit references to the outer class.
- If you want an inner class which holds an implement reference, just add a modifier `inner`.
  - `class ButtonState` &rarr; `inner class ButtonState`

### Syntax of a reference to the outer class of an inner class
If you really need a reference to the outer class instance, an explicit `inner` modifier is required.
- It declares **an inner class**, not a nested class.
- The reference to the outer class instance is `this@Outer_class_name`.

```kotlin
class Button : View {
	override fun getCurrentState(): State = ButtonState()

	override fun restoreState(state: State) { /*...*/ }
	
	inner class ButtonState : State {
		fun getOuterButton(): Button = this@Button
	}
}
```

