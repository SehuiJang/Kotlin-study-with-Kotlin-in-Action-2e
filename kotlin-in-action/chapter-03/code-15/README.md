# What chapter in 'Kotlin in Action'?
- Chapter 3.5.3 Multiline triple-quoted strings (2/e version)

# What did I look?
- Triple-quoted strings being able to avoid escaping characters can include any characters!
  - including line breaks (newline) and indents.
  - The `trimIndent` method of triple-quoted strings
- The embedded expression `${...}` normally works even in triple-quoted strings.
  - It is useful when you want to insert a literal dollar sign `$` or an Unicode symbol.

# Codes
## Triple-quoted strings including line breaks and indents
```kotlin
val kotlinLogo =
    """
    | //
    |//
    |/ \
    """.trimIndent()

val kotlinLogo2 =
    """
    | //
    |//
    |/ \
    """

fun main() {
    println(kotlinLogo)
    // | //
    // |//
    // |/ \
    println(kotlinLogo2)
    // 
    //     | //
    //     |//
    //     |/ \
    // 
}
```

The `trimIndent` method:
- eliminates the common minimal indent from all the lines.
- cuts out the first and last line which are blank.

### Note
- Special characters such as `\n` do not work in triple-quoted strings.
- `\` is not used as an escape character, and thus you can write Windows-style path slightly simpler.
  - When using a regular string:
    ```kotlin
    "C:\\Users\\yole\\kotlin-book"
    ```
  - If writing the path in a triple-quoted string:
    ```kotlin
    """C:\Users\yole\kotlin-book"""
    ```

## How to use a literal dollar sign or an escaped Unicode symbol (such as `\uD83E\uDD14`)?
### 1. As mentioned above, the backslash `\` does not act as an escape character.  
- If you write `"""Hum \uD83E\uDD14"""` and print it with `println`, `Hum \uD83E\uDD14` would be printed as is.
- `"""\uD83E\uDD14"""` is not printed in the form of an Unicode symbol.

To solve this situation, it is a solution to take advantage of the embedded expression!
```kotlin
// `\uD83E\uDD14`: a code notation of 🤔
fun main() {
    val think1 = """Hmm \uD83E\uDD14"""
    println(think1)
    // Hmm \uD83E\uDD14

    val think2 = """Hmm ${"\uD83E\uDD14"}"""
    println(think2)
    // Hmm 🤔

    val tmp = "\uD83E\uDD14"
    val think3 = """Hmm ${tmp}"""    // equivalent to `think2`
    println(think3)
    // Hmm 🤔
}
```
- `think1`: The character `\` is just a literal backslash in the triple-quoted string. Thus, you cannot see the escaped Unicode symbol.
- `think2` and `think3`: The embedded expression is applied, and the expression in the curly braces `{}` is a regular string.
  - As you know, the regular string `"\uD83E\uDD14"` is sucessfully evaluated as a Unicode symbol `🤔`!
  - Accordingly, the Unicode symbol is inserted into the triple-quoted string through the embedded expression `${...}` (string template).


### 2. Even in triple-quoted strings, the string template, especially the embedded expression, still works!
- Such as `"""Hello, ${name}"""` and `"""Goodbye, $name"""`
- However, `"""The variable is \$name"""` may cause an error.
  - It is because `\` does not work as an escape character.
  - `$name` right after `\` acts as the embedding expression. So, if there is not the variable `name`, it throws an error.

If you wanna write the literal dollar sign, the embedded expression is a key once again.
```kotlin
fun main() {
    // val text1 = """The variable is \$name""" // error: unresolved reference
    // println(text1)

    val text2 = """The variable is ${'$'}name"""
    println(text2)
    // The variable is $name

    val tmp = '$'
    val text3 = """The variable is ${tmp}name"""    // equivalent to `text2`
    println(text3)
    // The variable is $name
}
```
- `text1`: It would raise an error:
    ```text
    example.kt:39:42: error: unresolved reference 'name'.
            val text1 = """The variable is \$name""" // error: unresolved reference
                                            ^^^^
    ```
- `text2` and `text3`: The regular string containing a single dollar sign `$` is inserted into the triple-quoted string through the embedded expression.

# Misc.
When you tests your program, you can face the situation in which you have to compare the output of the program and the expected output.  
- The actual program output and the expected output have the specific format such as HTML, XML, JSON, and so on.
This kind of situations could be common than we think.  

## Example situation
For instance, a Kotlin function makes a segment of HTML:
```kotlin
fun renderUser(name: String, age: Int): String {
    return """
        <div class="user">
            <span class="name">$name</span>
            <span class="age">$age</span>
        </div>
    """.trimIndent()
}
```
You want to test whether this function returns the correct result, and you have an expected output (a sort of answer key).  

In this situation, you can declare a test function and directly write down the expected output in the form of a triple-quoted string.
```kotlin
fun testRenderUser() {
    val actual = renderUser("Alice", 30)

    val expected = """
        <div class="user">
            <span class="name">Alice</span>
            <span class="age">30</span>
        </div>
    """.trimIndent()

    check(actual == expected)
}
```

**In this type of situation, triple-quoted strings are useful for writing the multiline string into Kotlin code.**  
Although this example is as for HTML, the same applies to JSON.

### Why it is more convenient than regular strings (`"..."`)?
Just imagine that you have to write the expected output in a regular string.  
Writing it in a regular string would bother you.
- It requires a method to escape some character such as `"` in the middle of each line.
- To express a newline (line break), you have to explicitly use `\n`.
- You must connect and stitch together the lines with `+`.

If you only use a regular string, the expected output of the program would be written as follows:
```kotlin
val expected =
    "<div class=\"user\">\n" +
    "    <span class=\"name\">Alice</span>\n" +
    "    <span class=\"age\">30</span>\n" +
    "</div>"
```
This is fairly inconvenient than using triple-quoted strings.  
**If using raw strings (i.e., triple-quoted strings), you can write the expected output exactly as it is.**

