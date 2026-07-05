class User(val id: Int, val name: String, val address: String)

// (1) A function with repetitive code
fun saveUser(user: User) {
    // The two `if` statements are duplicated.
    if (user.name.isEmpty()) {
        // throw IllegalArgumentException(
        //     "Can't save user ${user.id}: empty Name")
        println("IllegalArgumentException: Can't save user ${user.id}: empty Name")
    }

    if (user.address.isEmpty()) {
        // throw IllegalArgumentException(
        //     "Can't save user ${user.id}: empty Address")
        println("IllegalArgumentException: Can't save user ${user.id}: empty Address")
    }

    // Save user to the database
}

// (2) Extracting a local function to avoid repetition
fun saveUser2(user: User) {

    // Declaring a local function
    fun validate(user: User,
                 value: String,
                 fieldName: String) {
        if (value.isEmpty()) {
            // throw IllegalArgumentException(
            //     "Can't save user ${user.id}: empty $fieldName")
            println("IllegalArgumentException: Can't save user ${user.id}: empty $fieldName")
        }
    }
    
    validate(user, user.name, "Name")
    validate(user, user.address, "Address")

    // Save user to the database
}

// (3) Accessing outer function parameters in a local function
fun saveUser3(user: User) {

    // avoid to duplicate the `user` parameter
    fun validate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            // throw IllegalArgumentException(
            //     "Can't save user ${user.id}: " +
            //         "empty $fieldName")
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

// (4) Extracting the logic into an extension function
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
// the function simplified by the extension function
fun saveUser4(user: User) {
    user.validateBeforeSave()   // just call the extension function

    // Save user to the database
    // ...
}

fun main() {
    saveUser(User(1, "", ""))
    // IllegalArgumentException: Can't save user 1: empty Name
    // IllegalArgumentException: Can't save user 1: empty Address

    saveUser2(User(1, "", ""))
    // IllegalArgumentException: Can't save user 1: empty Name
    // IllegalArgumentException: Can't save user 1: empty Address

    saveUser3(User(1, "", ""))
    // IllegalArgumentException: Can't save user 1: empty Name
    // IllegalArgumentException: Can't save user 1: empty Address

    saveUser4(User(1, "", ""))
    // IllegalArgumentException: Can't save user 1: empty Name
    // IllegalArgumentException: Can't save user 1: empty Address
}