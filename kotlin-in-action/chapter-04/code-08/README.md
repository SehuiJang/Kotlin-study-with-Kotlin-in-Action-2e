# What chapter in 'Kotlin in Action'?
- Chapter 4.2.3 Implementing properties declared in interfaces (2/e version)

# What did I look?
- The case in which an interface contains abstract property declarations


# Codes
## An `interface` with an abstract property declaration
```kotlin
interface User {
    val nickname: String    // it doesn't contain any state.
}
```
When classes implement the `interface User`, they have to override `nickname`.  
- Way1: initializing the `nickname` property with a value. I mean using a property initializer.
- Way2: declaring a getter of `nickname`.

## Some examples implementing an interface property
Let's see a few implementations presented in 'Kotlin in Action 2/e'.  
There are three classes: `PrivateUser`, `SubscribingUser`, and `SocialUser`.

```kotlin
class PrivateUser(override val nickname: String) : User

class SubscribingUser(val email: String) : User {
    override val nickname: String
        get() = email.substringBefore('@')
}

class SocialUser(val accountId: Int) : User {
    override val nickname = getNameFromSocialNetwork(accountId)
}

fun getNameFromSocialNetwork(accountId: Int) =
    "kodee$accountId"

fun main() {
    println(PrivateUser("kodee").nickname)
    // kodee
    println(SubscribingUser("test@kotlinlang.org").nickname)
    // test
    println(SocialUser(123).nickname)
    // kodee123
}
```

### `PrivateUser`
It makes use of its primary constructor to override `nickname`.  
- You can insert the `override` keyword into the primary constructor.
- If the constructor takes a value as the `nickname` parameter and executes, `override val nickname` is declared and initialized with the parameter value.

### `SubscribingUser`
In this case, the class overrides `nickname` by declaring a custom getter.
- There is no backing field of `nickname`.
- The value of `nickname` is calculated **every time** `nickname` is accessed by outside.
  - This way could be costly depending on what operations `getNameFromSocialNetwork` contains.

### `SocialUser`
Here, `nickname` is overridden by a property initializer.
- You can directly assign a value to `nickname`.
- The `getNameFromSocialNetwork` function is invoked only one time during initialization.
  - The return value of the function is stored in the field.
  - It means you can reduce the invocation cost of the function.

## Properties in `interface` can have getters (and setters)!
Definitely, properties declared in `interface` cannot have their backing field.  
Even so, interfaces are not limited to abstract property declarations.
- They can also contain properties with custom getters and setters.

```kotlin
interface EmailUser {
    val email: String   // abstract property declaration
    val nickname: String    // It still don't have a backing field, which is not permitted, but it has a custom getter.
        get() = email.substringBefore('@')
}
```
- Subclasses must override the first property `email`.
- On the other hand, the `nickname` property with a custom getter can be simply inherited.
  - If needed, it can also be overridden in subclasses.

# Note
## Preference showdown: "property with a custom getter" vs. "member function without parameters"
The following two source code blocks show the identical behavior.
```kotlin
// Using a property with a custom getter
class User(val firstName: String, val lastName: String) {
    val fullName: String
        get() = "$firstName $lastName"
}

fun main() {
    val user = User("first",  "last")
    print(user.fullname)    // syntax of a property
}
```

```kotlin
// Using a parameterless member function
class User(val firstName: String, val lastName: String) {
    fun fullName(): String = "$firstName $lastName"
}

fun main() {
    val user = User("first",  "last")
    print(user.fullname())    // syntax of a member function
}
```

When using a property is preferred? And when a function is preferred?

Well, there are three items to judge when to prefer properties over member functions:
- (1) It throws no exception.
- (2) It is cheap to calculate or use a backing field for caching a value on the first run.
- (3) It returns the same result while the object state is maintained. To put it another way, it represents a characteristic of an object.

Using a property would be a good choice if it falls into at least one of the three items.  
In other words, it would be safe even if it looks like a class property.  
It is because when users use a syntax for accessing a property, they can think that:
- It is just reading an object property.
- It might be cheap.
- It might return the same value if the state of the object is not changed.
- I think anything terrible such as errors will happen.

Contrarily, if it doesn't meet any items, using a member function is not bad.

### Takeaways
When to prefer read-only properties:
- the value represents a characteristic of the object
- it is cheap or cached
- it does not throw exceptions
- it returns the same result as long as the object state has not changed

When to prefer parameterless member functions:
- the operation is expensive
- it may fail
- it has side effects or represents an action rather than a stable characteristic
