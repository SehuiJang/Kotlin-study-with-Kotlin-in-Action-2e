# What chapter in 'Kotlin in Action'?
- Chapter 3.5.2 Regular expressions and triple-quoted strings (2/e version)

# What did I look?
- Some `String` extension functions such as:
  - `substringBeforeLast`
  - `substringAfterLast`
- Writing a regular expression in a *triple-quoted string* (i.e., raw string)

# A *triple-quoted string* (raw string)
In triple-quoted strings, `\` is not used as an escape character.  

For example, the following lines exhibit the same behavior.
```kotlin
/* Example 1 */
val str1 = "\\abc"  // actual String: \abc
val str2 = """\abc"""   // actual String: \abc
```
```kotlin
/* Example 2 */
val str1 = "\\.".toRegex()
// actual String: \.
// regex engine: a literal dot "."

val str2 = """\.""".toRegex()
// actual String: \.
// regex engine: a literal dot "."
```

# Codes
When you split an absolute path of a file into the three components (i.e., a directory, a filename, and an extension), you have two choices:
- An extension function `split` on `String`
- An extension function `split` on a regular expression

## Using `split` on plain texts (`String`)
Well, it is not bad.
```kotlin
fun parsePath(path: String) {
    val directory = path.substringBeforeLast("/")
    val fullName = path.substringAfterLast("/")
    val fileName = fullName.substringBeforeLast(".")
    val extension = fullName.substringAfterLast(".")

    println("Dir: $directory, name: $fileName, ext: $extension")
}

fun main() {
    parsePath("/Users/yole/kotlin-book/chapter.adoc")
    // Dir: /Users/yole/kotlin-book, name: chapter, ext: adoc
}
```

## Making use of a regular expression
You can also consider a regular expression.  
When you express a regular expression, a *triple-quoted string* can be convenient.  
You can easily see and check what is being passed to the regex engine, since the backslash is not used as an escape character.

```kotlin
fun parsePathRegex(path: String) {
    val regex = """(.+)/(.+)\.(.+)""".toRegex()
    // source code: """(.+)/(.+)\.(.+)"""
    // actual String: (.+)/(.+)\.(.+)
    // regex engine interpretation: (one or more characters) + (a slash) + (one or more characters) + (a literal dot) + (one or more characters)

    val matchResult = regex.matchEntire(path)  // finds a pattern `regex` from `path`
    if (matchResult != null) {
        val (directory, filename, extension) = matchResult.destructured  // using a destructured property
        println("Dir: $directory, name: $filename, ext: $extension")
    }
}

fun main() {
    parsePathRegex("/Users/yole/kotlin-book/chapter.adoc")
    // Dir: /Users/yole/kotlin-book, name: chapter, ext: adoc
}
```

