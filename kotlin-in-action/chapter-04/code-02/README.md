# What chapter in 'Kotlin in Action'?
- Chapter 4.1.2 `open`, `final`, and `abstract` modifiers: Final by default (2/e version)

# What did I look?
- In Kotlin, all classes (not interfaces) and their methods are *`final`*, by default.
- The `open` modifier
  - Marking a class with `open` &rarr; you can create subclasses of it.
  - Marking a method or a property with `open` &rarr; you can override them in subclasses.
- Overridden members (i.e., member functions & properties) are `open` by default.
  - If you want for them not to be overridden in lower-level subclasses, you can explicitly mark those members with the `final` modifier.
- The `abstract` modifier
  - Marking a class with `abstract` &rarr; the class cannot be instantiated.
  - Marking a method or a property with `abstract` &rarr; they have no implementations and have to be overridden in subclasses.
  - `abstract` members are always `open`.

# Codes
## The `open` modifier
You can use the `open` modifier for classes, member functions, and properties.
- `open class ...`: subclasses can inherit member functions and properties from the `open` class.
- `open fun ...`: subclasses can override the `open` function.
- `open val ...` or `open var ...`: subclasses can reassign a new value to the `open` property or redefine its accessor. That is, subclasses can override it.

Note that interfaces and their member functions & properties are `open` by default, unlike classes.  
```kotlin
// The interface and its members are `open` by default.
interface Clickable {
    fun click() // regular method
    fun showOff() = println("I'm clickable!") // a method with its default implementation
}

// open class -> subclasses can inherit the class
open class RichButton : Clickable {
    // Note that the method of a class is `final` by default.
    fun disable() { println("`disable` of a class `RichButton`") } // it is `final`. subclasses can invoke it but cannot override it.
    open fun animate() { println("`animate` of a class `RichButton`") }    // subclasses can override it.
    override fun click() { println("`click` overridden in a class `RichButton`") }  // an overriden method is `open` by default.
}

// Note that the class is `final` by default.
class ThemedButton : RichButton() {
    // Note that `disable` of class `RichButton` cannot be overridden in this subclass.
    override fun animate() { println("`animate` overridden in a class `ThemedButton`") }    // overriding a method `animate` of class `RichButton`
    override fun click() { println("`click` overridden in a class `ThemedButton`") }  // overriding a method `click` of class `RichButton`
    override fun showOff() { println("`showOff` overridden in a class `ThemedButton`") }  // overriding a method `showOff` of interface `Clickable`
}
```

Well, the inheritance hierarchy is as follows:
```text
interface `Clickable`
    ↓
class `RichButton`
    ↓
class `ThemedButton`
```

### The method `click()`
As you can see, this method is initially declared in the interface `Clickable`.  
The interface itself and all the belonging members (methods & properties) are `open` by default. Thus, the method `click()` is also `open`.  

Therefore, when a class `RichButton` inherits members from the interface `Clickable`, you can override `click()`!  
Subsequently, a lower-level subclass `ThemedButton` inherits the method `click()` from `open class RichButton`. Since `click()` is overridden in the class `RichButton`, and the overridden method is `open` by default, the subclass `ThemedButton` is also able to override `click()`.  

### The method `showOff()`
Like `click()`, the method `showOff()` is initially declared in the interface `Clickable`.  
And then, `open class RichButton` inherits `showOff()` from the interface. However, the class `RichButton` does not override it!  
The lower-level subclass `ThemedButton` can also inherits `showOff()` from the base class `RichButton` because `RichButton` is `open class`.  
Also, `showOff()` can be overridden in `ThemedButton` since the method is still `open` even in `RichButton`, even though it is not overridden in `RichButton`.  

Therefore, the class `ThemedButton` finally overrides `showOff()`.

### The method `disable()`
It initially defined in the class `RichButton`.  

It is worthy Noting that:
- `RichButton` is an `open` class, and thus `ThemedButton` can receive `disable()` from `RichButton` and invoke it.
- `disable()` cannot be overridden in `ThemedButton`. It is because `disable()` is not `oepn` but `final` in the base class `RichButton`.

### The method `animate()`
It is similar to `disable()` in that it is initially declared and defined in the class `RichButton`.  
Importantly, the member function `animate()` is `open` in the class `RichButton` while `disable()` is `final`.  

Accordingly, the lover-level class `ThemedButton` is able not only to invoke `animate()` but also override it!

## The `final` modifier + `override` modifier
What you have to remember is that the overridden member (i.e., member function or property) is **`open` by default**.  
That means the lower-level subclass can also override it.  

You can forbid the subclass from overriding it.
- Just add the `final` modifier.

```kotlin
interface Clickable {
    fun click()
    fun showOff() = println("I'm clickable!")
}

open class RichButton2 : Clickable {
    final override fun click() { println("`click` overridden in a class `RichButton2` / it is `final`") }
}

class ThemedButton : RichButton2() {
    // calling `click()` is permitted, but overriding it is not.
}
```
Now, even if other class such as `ThemedButton` inherits the overridden `click()` method from `RichButton2`, the subclass cannot override this method.
- The subclass can only call it.


## The `abstract` modifier
Besides `open`, there is another modifier: `abstract`.  
- `abstract class ...`
  - You cannot instantiate this class, and the class can include `abstract` methods and `abstract` properties.
- `abstract fun ...`
  - It cannot have its implementation body.
  - It is mandatory for subclasses to override it, i.e., implement its concrete behavior.
- `abstract val ...` or `abstract var ...`
  - It does not have an initial value and custom accessors.
  - Subclasses have to set its value or define the custom accessors.

Here is an example:
```kotlin
abstract class Animated {
    abstract val animationSpeed: Double
    val keyframes: Int = 20
    open val frames: Int = 60

    abstract fun animate()
    open fun stopAnimating() { println("`open fun stopAnimating()` in `abstract class Animated`") }
    fun animateTwice() { println("`fun animateTwice()` in `abstract class Animated`") }
}
```

# Table of **access modifiers** in a class
| Modifier | Corresponding class | Corresponding member function | Corresponding property | Comments |  
| --- | --- | --- | --- | --- |
| `final` | Cannot be subclassed | Cannot be overridden | Cannot be overridden | It's used by default, thus explicit marking is not required. |
| `open` | Can be classed | Can be overridden (i.e., can reimplement its behavior in subclasses) | Can reinitialize it or reimplement its getter/setter in subclasses | If you need it, you have to specify it. |
| `abstract` | Cannot be instantiated | Must be overridden (i.e., must implement its logic in subclasses) | Must initialize it or implement its getter/setter in subclasses | (1) you can use it only for `abstract` classes, (2) `abstract` members are supposed not to have their implementation & initial value. |
| `override` | - | Overrides (i.e., reimplements) the behavior of the function | Reinitializes its value or reimplements its setter/getter  | Overridden members are `open` by default, unless marked with `final` |

---
---

# Note: `open` classes & smart casts
## Smart casts from the viewpoint of classes
As you can see in previous chapter, smart casts mean:
```text
After type-checking a variable, you can utilize it in your logic without any explicit manual casting.  
It is allowed only when variables could not have changed after type-checking.
```

From the perspective of classes, smart casts can only be available for class properties that:
- are `val`
- have no custom accessor
- **are `final`**

When a base class is `open` and a class property is marked with `open`,
- a subclass can inherit it from the class
- the subclass can override the property, reimplementing its custom accessor
- if accessing that property in runtime, the property can return a different value each time it is accessed!

### Thus, note that:
If you wanna leverage smart casts for a class property, the property must satisfy some conditions.
> 1. it have to be `val`.  
> 2. it has no custom accessor.  
> 3. it is a `final` property.

## Example 1: Basic smart cast
In the following example, you can invoke `value.length` in the `if` branch, without a manual casting.
```kotlin
fun printLength(value: Any) {
    if (value is String) {
        println(value.length)   // manual casting is unneccessary.
    }
}
```

## Example 2: Smart cast for a class property
If the class property is just `val`, the smart cast is possible.  
```kotlin
class Holder(val value: Any)

fun printLength(holder: Holder) {
    // class property "holder.value"
    if (holder.value is String) {
        println(holder.value.length)    // smart cast is allowed
    }
}
```
Note that `holder.value` meets the three points:
- the class `Holer` and its property `value` are all `final` by default. &rarr; overriding is impossible.
- `holder.value` is `val`.
- there are no custom accessors of `holder.value`.

## Example 3: A class property with a custom accessor
An access to a class property can be seen as an access to a field, but in fact, it could be a getter invocation.

```kotlin
class UnstableHolder {
    private var firstAccess = true

    val value: Any
        get() =
            if (firstAccess) {
                firstAccess = false
                "Kotlin"
            } else {
                123
            }
}

fun printLength(holder: UnstableHolder) {
    if (holder.value is String) {
        println(holder.value.length) // CANNOT expect a smart cast
    }
}
```
In this example, the getter is called each time you access to `holder.value`.
- The first access to `holder.value` returns a string `"Kotlin"`.
- The second access returns an interger `123`.

Therefore, even though an `is` check for `holder.value` is done, the compiler does not permit a smart cast.  

```text
The compiler does not allow a smart cast for a class property with its custom accessor in the first place.
```

## Example 4: A class property with the `open` modifier
Consider a case of a class property that has no custom accessor but is `open`.  
The property is possible to be overridden in a subclass.
```kotlin
// the property `value` is `open` and does not have any custom accessor.
open class OpenHolder(open val value: Any)

// the property `value` is overridden in a subclass `FlakyHolder`
// the overridden `value` has a custom getter.
class FlakyHolder : OpenHolder("dummy") {
    private var firstAccess = true

    override val value: Any
        get() =
            if (firstAccess) {
                firstAccess = false
                "Kotlin"
            } else {
                123
            }
}

fun printLength(holder: OpenHolder) {
    if (holder.value is String) {
        println(holder.value.length) // the compiler would reject it.
    }
}

fun main() {
    val holder: OpenHolder = FlakyHolder()
    printLength(holder)
}
```
Here, the static type of `holder` is `OpenHolder`.  
However, the object which `holder` refers to in runtime is an instance of `FlakyHolder`.  
So, when you call `printLength(holder)`:
- `holder.value` in `if (holder.value is String) {` returns a string `"Kotlin"`
- After that, `holder.value` in `println(holder.value.length)` can return an integer `123`.

Therefore, the compiler can think that:
> If a `value` property in `OpenHolder` is `open`, a subclass `FlakyHolder` can override (i.e., implement) a custom getter.  
> In this situation, I cannot guarantee that the checked value returns the identical value after checking.
> Thus, allowing a smart cast is not safe even though the `value` property of `OpenHolder` has no custom accessor and is `val`.

## Example 5: A `final` property of an `open` class
Importantly, even though a class is `open`, the smart cast can be available if a property is `final`.  

```kotlin
// The class is `open`, yet the property is not.
open class StableHolder(val value: Any)

fun printLength(holder: StableHolder) {
    if (holder.value is String) {
        println(holder.value.length) // possible!
    }
}
```
Well, the `StableHolder` class is `open class`, and thus it can have its subclass.  
Even so, the `value` property is `final`.
- That means that the subclass cannot change an initial value of `value` and define a custom getter.

As a result, the `value` property itself is stable!
- it is `val`, is `final`, and is without a custom getter.
- **the smart cast for `holder.value` is generally possible!**

## Putting examples together

| Case                                       | Possibility of smart cast |
| ---------------------------------------- | -------------- |
| `class Holder(val value: Any)`           | Possible             |
| `open class Holder(val value: Any)`      | Generally possible         |
| `open class Holder(open val value: Any)` | Impossible (`open` property)            |
| `val value: Any get() = ...`             | Impossible (custom accessor)            |
| `var value: Any`                         | Impossible (not `val`)            |
