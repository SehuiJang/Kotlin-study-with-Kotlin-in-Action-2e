# What chapter in 'Kotlin in Action'?
- Chapter 3.6 Making your code tidy: Local functions and extensions (2/e version)

# What did I look?
- Declaring a local function
- Accessing outer function parameters from the local function
- Extracting the logic which is used in a certain place, not everywhere, into an extension function

# Codes
It is a situation in which
- the information of users are managed by a class `User`,
- you have to implement a function to validate a `User` instance and then save it to the database,
- and you are currently composing a logic to validate a `User` instance.

The following steps are about how to reduce the repetition of a logic and make the code look tidy.

## Step 1: A function with duplicate logic
This is the initial code, and you can see a repetitive logic.
```kotlin
class User(val id: Int, val name: String, val address: String)

fun saveUser(user: User) {
    if (user.name.isEmpty()) {
        println("IllegalArgumentException: Can't save user ${user.id}: empty Name")
    }

    if (user.address.isEmpty()) {
        println("IllegalArgumentException: Can't save user ${user.id}: empty Address")
    }

    // Save user to the database
}

fun main() {
    saveUser(User(1, "", ""))
    // IllegalArgumentException: Can't save user 1: empty Name
    // IllegalArgumentException: Can't save user 1: empty Address
}
```
- The two `if` statements in the `saveUser` function are duplicated.

## Step 2: Introducing a local function for reducing duplication
```kotlin
class User(val id: Int, val name: String, val address: String)

fun saveUser2(user: User) {
    // Declaring a local function
    fun validate(user: User,
                 value: String,
                 fieldName: String) {
        if (value.isEmpty()) {
            println("IllegalArgumentException: Can't save user ${user.id}: empty $fieldName")
        }
    }
    
    validate(user, user.name, "Name")
    validate(user, user.address, "Address")

    // Save user to the database
}

fun main() {
    saveUser2(User(1, "", ""))
    // IllegalArgumentException: Can't save user 1: empty Name
    // IllegalArgumentException: Can't save user 1: empty Address
}
```
- The `saveUser2` function embraces a local function `validate`.
  - The `if` statement is moved into the local function.
- Introducing a local function can make your code tidy!

## Step 3: Accessing a variable of the enclosing (i.e., outer) function from a local function
It is worthy remembering the fact that a local function can refer to the paramters and local variables of the enclosing function that contains it.
```kotlin
class User(val id: Int, val name: String, val address: String)

fun saveUser3(user: User) {
    // avoid to duplicate the `user` parameter
    fun validate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            println(
                "IllegalArgumentException: " +
                "Can't save user ${user.id}: " +    // access parameters of the outer function (`saveUser3`) directly
                "empty $fieldName")
        }
    }

    validate(user.name, "Name")
    validate(user.address, "Address")

    // Save user to the database
}

fun main() {
    saveUser3(User(1, "", ""))
    // IllegalArgumentException: Can't save user 1: empty Name
    // IllegalArgumentException: Can't save user 1: empty Address
}
```
- You are able to find that the parameter `user` of the enclosing function is available inside the `validate` function.
  - The local function can use the parameter (and the variable) of the function enclosing it.
- Thus, it is unnecessary for the `validate` function has the `user` parameter.

## Step 4: Encapsulating the logic in an extension function
Using an extension function can give you even tidier code.
```kotlin
class User(val id: Int, val name: String, val address: String)

// The extension function
fun User.validateBeforeSave() {
    fun validate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            // throw IllegalArgumentException(
            //     "Can't save user $id: empty $fieldName")
            println("IllegalArgumentException: "+
                "Can't save user $id: empty $fieldName")    // access the property of User directly: `$id` instead of `${user.id}`
        }
    }

    validate(name, "Name")
    validate(address, "Address")
}

fun saveUser4(user: User) {
    user.validateBeforeSave()   // just call the extension function

    // Save user to the database
    // ...
}

fun main() {
    saveUser4(User(1, "", ""))
    // IllegalArgumentException: Can't save user 1: empty Name
    // IllegalArgumentException: Can't save user 1: empty Address
}
```
- It is also a good approach to move the entire logic into the extension function `User.validateBeforeSave`
- Let's consider the situation:
  - (1) The `User` class belongs to your codebase.
  - (2) The logic to validate a `User` instance is to be used just before the logic to save the instance.
    - The validation logic is not utilized in any other places.
  - (3) Thus, you don't want to declare the validation logic as a regular member of the `User` class.
    - That is, you want to avoid contaminating the `User` class with a single-use logic (i.e., the validation logic).
  - (4) Basically, we have been constructing the function `saveUser`.
    - This function essentially saves a `User` instance.
    - Therefore, it will be ugly if the logic to validate an instance occupies too many lines in the `saveUser` function.
  - To summarize: 
    ```text
    1. The validation logic should not be a regular method of the `User` class.
    2. The validation logic should not take up too many lines of the `saveUser` function.
    -> The extension function is a good compromise.
    ```
- You can see that the `saveUser4` function itself become fairly tidy!
  - Since the validation logic is an extension function of the `User` class, you can invoke it **as if it is a regular member function**.
  - Simultaneously, the validation logic is not a regular member function but **just an extension function in fact**. So, the code of the `User` class actually **remains clear**!
  - In the `saveUser4` function, the code relevent to the validation of a `User` instance is just one line! So, you can focus on the subsequent logic for saving user information.
