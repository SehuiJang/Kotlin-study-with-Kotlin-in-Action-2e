# What chapter in 'Kotlin in Action'?
- Chapter 4.2.5 Changing accessor visibility (2/e version)

# What did I look?
- You can set the visibility of accessors.
  - For example, `private set`, `protected set`, etc.
- The default visibility of accessors is identical to the one of the property itself.
  - If needed, you can adjust the visibility of `set`.
  - However, the property visibility is practically the getter visibility. So, it is usually impossible to make a `private get` of the `public` property.
- Note that the visibility of the getter is supposed to be equal to or wider than the one of the setter.
  - (1) `property visibility = getter visibility` (in effect)
  - (2) `setter visibility <= property visibility`


# Codes
In the case below, the `var counter` is publicly readable but privately mutable.  
This property is `public` by itself.
- You cannot make the `get` of it as `private`.

```kotlin
class LengthCounter {
    var counter: Int = 0
        private set
        // private get  // Error!
        // example.kt:4:9: error: getter visibility must be the same as property visibility.
        // private get
        // ^^^^^^^
    
    fun addWord(word: String) {
        counter += word.length
    }
}

fun main() {
    val lengthCounter = LengthCounter()
    lengthCounter.addWord("Hi!")
    println(lengthCounter.counter)
    // 3

    // lengthCounter.counter = 0
    // Error: Cannot assign to 'counter': the setter is private in 'LengthCounter'
}
```

# Note
## Tip
If you want for both `get` and `set` of a property to be `private` or `protected`, just declare the property itself as  `private` or `protected`.
- That is, just use `private var` or `private protected`.

## Upcoming contents about properties
In "Kotlin in Action 2/e", there are some later chapters discussing 'properties'.  
- The `lateinit` modifier on a non-null property &rarr; section 7.9
- Lazy-initialized properties are related to *delegated properties* &rarr; section 9.5
- Annotations that emulate Java features in Kotlin for compatibility with Java frameworks &rarr; chapter 12
- The `const` modifier &rarr; section 12.1.1
