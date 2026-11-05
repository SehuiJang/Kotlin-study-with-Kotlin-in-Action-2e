# What chapter in 'Kotlin in Action'?
- Chapter 4.4.4 Object expressions: Anonymous inner classes rephrased (2/e version)

# What did I look at?
- You can use an object expression to create an *anonymous object* with no name.
  - It is similar to an anonymous inner class in Java.
- The syntax of an object expression is almost the same as that of a regular object declaration.
  - However, an object expression does not include an object name.
- Each time an object expression is evaluated, a new instance of the anonymous object is created.
- An object expression effectively declares an anonymous class and creates an instance of that class at the same time.
  - You can use an object expression directly as an argument when calling a function or constructor.
  - An object expression itself has no object name. However, if you want to keep a reference to the created object, you can assign the object expression to a variable.
- An important point is that code inside an object expression can access variables and declarations from its surrounding context.
  - This is different from a regular object declaration, which cannot capture local variables from an enclosing function.
  - In particular, an anonymous object can capture local variables from its enclosing lexical scopes.
  - If it captures a local `var`, it can also modify that variable.

# Codes
## Declaring an *anonymous object* by using an object expression
Based on an example from the second edition of `Kotlin in Action`, you can write code like the following.
```kotlin
interface MouseListener {
    fun onEnter()
    fun onClick()
}

class Button(private val listener: MouseListener) {
    fun run() {
        listener.onClick()
    }
}

fun main() {
    // (1) Passing an object expression (anonymous object) to the constructor of `Button`
    Button(object : MouseListener {
        override fun onEnter() { /* ... */ }
        override fun onClick() { /* ... */ }
    })

    // (2) You can also assign an anonymous object to a variable.
    val listener = object : MouseListener {
        override fun onEnter() { /* ... */ }
        override fun onClick() { /* ... */ }
    }
}
```

As you can see from this example, the syntax of an object expression is almost the same as that of a regular object declaration.  
**However, an object expression does not specify an object name.**  
In addition, an anonymous object may implement any number of interfaces.
- It can implement one interface.
- It can implement no interfaces at all.
- It can also implement multiple interfaces.

Examples of how anonymous objects created through object expressions can be used are shown below.
- An object expression both defines an anonymous class and creates an instance of that class. Therefore, you can use the object expression itself directly as an argument to a function or constructor.
- An object expression itself has no object name. However, if you want to keep a reference to the anonymous object created by the expression, you can store it in a variable.
  - In that case, you use the variable name to refer to the object.

### Is it a singleton?
Regular object declarations create singletons. In other words, an object declaration defines one singleton object.  

Object expressions behave differently from regular object declarations.  
If the same object expression is evaluated multiple times, a new anonymous-object instance is created each time.  

## You can access and modify local variables from an anonymous object!
Using an example based on the second edition of `Kotlin in Action`, you can write code like the following.
```kotlin
interface MouseListener {
    fun onEnter()
    fun onClick()
}

class Button(private val listener: MouseListener) {
    fun run() {
        listener.onClick()
    }
}

fun main() {
    // (3) Accessing and modifying a local variable from an anonymous object
    var clickCount = 0
    val button = Button(object : MouseListener {
        override fun onEnter() { /* ... */ }
        override fun onClick() {
            clickCount++
            println(clickCount)
        }
    })
    button.run()
    // 1
    button.run()
    // 2
    button.run()
    // 3
}
```

In the example above, pay attention to the variable `clickCount`.  
Code inside the object expression can access this variable and modify it.  
In other words, you can describe the behavior as follows:
```text
An object expression can capture local variables from its enclosing lexical/static scopes.
```

The local variables that can be accessed from inside an object expression are not limited to variables declared in the immediately enclosing block.
- An anonymous object can capture local variables declared in an enclosing function or code block.
- **In particular, if the captured variable is a `var`, the anonymous object can modify its value, unlike a Java anonymous class, where captured local variables must be final or effectively final.**
- The relevant variable does not have to come from the immediately enclosing scope. Variables declared in an outer enclosing block can also be captured.
- Top-level properties can also be accessed, although they are not *captured local variables*; they are simply accessible declarations in scope.

In addition, an object expression can access **properties of the containing class**.
- Suppose an object expression is created inside an instance member of a class. The anonymous object can then access members of the containing class instance through the enclosing `this`.
- If such a property is a `var`, the anonymous object can modify it as well.
    ```kotlin
    class Window {
        var clickCount = 0

        fun createListener(): MouseListener {
            return object : MouseListener {
                override fun onClick() {
                    clickCount++   // `Window` instance's property
                    // this@Window.clickCount++
                }
            }
        }
    }
    ```

### Accessing a variable vs. Capturing a variable
Inside an object expression, **accessing a variable** simply means that the variable can be referred to and used.
By contrast, **capturing a variable** means that the anonymous object retains **the necessary access to that variable** so that it can continue using it even after the enclosing function has finished executing.

Consider the following example.
- As you can see, calling `createListener` returns an anonymous object.
- At first glance, it may seem that the local variable `clickCount` should cease to exist once the execution of `createListener` finishes.
- However, the anonymous object **captures** `clickCount`, which is a local variable from its enclosing scope.
- As a result, the object returned by `createListener` can continue to access and modify `clickCount` even after the function has returned.

```kotlin
fun createListener(): MouseListener {
    var clickCount = 0

    return object : MouseListener {
        override fun onClick() {
            clickCount++
            println(clickCount)
        }
    }
}

fun main() {
    val listener = createListener()

    listener.onClick() // 1
    listener.onClick() // 2
}
```

As mentioned earlier, code inside an object expression can also **access** a top-level property.  
However, in this case, we do not normally describe the anonymous object as **capturing** the top-level property.  
In the example below, even after `createListener` finishes executing, `globalCount` continues to exist independently of that function.  
- Therefore, the anonymous object does not need to preserve a local variable from an enclosing function in order to keep using `globalCount`.
- For this reason, the term *capture* is not normally used for this case.

```kotlin
var globalCount = 0

fun createListener(): MouseListener {
    return object : MouseListener {
        override fun onClick() {
            globalCount++
        }
    }
}
```

What about a property of an outer class?  
In the example below, code inside the object expression can **access** `clickCount`.  
- More precisely, `clickCount` is not a local variable of `createListener`; it is a property of the enclosing `Window` instance, that is, `this@Window`.
- In this case, you can think of the anonymous object as retaining a reference to the enclosing `Window` instance and accessing `clickCount` through that instance.

```kotlin
class Window {
    var clickCount = 0

    fun createListener(): MouseListener {
        return object : MouseListener {
            override fun onClick() {
                clickCount++
                // or this@Window.clickCount++
            }
        }
    }
}
```

# Side note
Object expressions are useful when you want to override two or more methods from an interface or superclass.  
If you only need to implement an interface with a single abstract method, another option is SAM conversion.
- SAM stands for "Single Abstract Method."
- `Kotlin in Action` discusses this in a later chapter. In the second edition, it appears in Chapter 5.
