# What chapter in 'Kotlin in Action'?
- Chapter 3.5 Working with strings and regular expressions (2/e version)
- Chapter 3.5.1 Splitting strings (2/e version)

# What did I look?
- How does `split` method of `String` in Java work?
- How can we use `split` method of `String` in Kotlin?
  - With one or more deliminators (a regular expression or plain-text string)

# The `split` method of `String`: Java vs Kotlin
## Java
Using the `split` method, you basically have to specify a deliminator as its parameter.  
Importantly, the deliminator which the `split` method gets is a regular expression.  

Thus, in the following example, you will get an empty array:
```java
"12.345-6.A".split(".")
// result: []
```
If `"."` is used as a regular expression, it means 'any character'.

## Kotlin
Well, the `String` class of Kotlin is equal to the one of Java.  
Even so, the confusing `split` method in Java is hided in Kotlin.  
Instead, there are a few overloaded extensions of which name is also `split`.  
- However, these extensions have different arguments.  
  - An extension of them takes a regular expression (type `Regex` or `Pattern`).
  - The other extension requires one or more plain-text strings (type `String`).


# Codes
※ The codes are written in Kotlin.

## 1. The `split` method with a regular expression
```kotlin
fun main() {
    println("12.345-6.A".split("\\.|-".toRegex()))
    // [12, 345, 6, A]
}
```
- An extension function `toRegex`
  - You can explicitly convert a plain-text string into a regular expression.
- `"\\.|-".toRegex()` means "a literal dot or literal hyphen"
  - In the regular expression, `|` means **OR**.
  - `-` means a literal hyphen.
  - You need to pay attention to `\\.`.
    - In the regular expression, `\.` means a literal dot.
    - So, when you turn a plain-text string into a regular expression with `toRegex`, you have to pass `\.` to `toRegex`.
    - However, if you execute `"\.".toRegex()`, 
      - (1) a creation of a plain-text `String` instance `"\."` is tried,
      - (2) and then `\.` in the plain text would be recognized as an unsupported escape sequence.
      - In practice,
      ```
      the compiler
      -> firstly focuses on `"\."`,
      -> sees that a plain text includes `\.`,
      -> judges it as an unsupported escape sequence,
      -> and throws a compile-time error.
      ```
    - Therefore, `"\\."` is correct! When you execute `"\\.".toRegex()`
      - (1) `\\` in the plain text is treated as a literal `\`.
      - (2) As a result, a plain-text `String` instance `"\."` will be maded.
      - (3) Accordingly, `toRegex` can get `"\."` and interpret it as 'a literal dot' in terms of a regular expression.
- If you execute `"12.345-6.A".split("\.|-".toRegex())`, it leads to an error:
    ```text
    example.kt:2:33: error: unsupported escape sequence.
    println("12.345-6.A".split("\.|-".toRegex()))
                                ^^
    ```

## 2. The `split` method with one or more plain-text strings
When you handles a simple case, using a regular expression is unnecessary.  
You can pass one or more plain-text strings (type `String`) to `split`.

```kotlin
fun main() {
    println("12.345-6.A".split(".", "-"))
    // [12, 345, 6, A]
}
```
- It means splitting the given string by `"."` or `"-"`.
- Note that the `split` function for `String` can accept plain-text strings as arguments.

---

# Bonus: how about `"\n".toRegex()` and `"\\n".toRegex()`?
In a nutshell, the two expressions shows an identical behavior in the level of a regular expression, yet different behavior in terms of a Kotlin string (not regular expression).  

## `"\n".toRegex()`
Its behavior is:
```text
Kotlin source code: "\n"
actual String: a newline character
regex: a newline character match
```
Thus, it is an action to pass a newline character itself to regex pattern.  
In the next step, the regex engine also treats it as a newline character.

## `"\\n".toRegex()`
It would behave as follows:
```text
Kotlin source code: "\\n"
actual String: \n
regex: a newline character match
```
Therefore, it is an action to pass a character "\" and a "n" to regex pattern.  
When the compiler grasp an actual string, it does not convert `"\\n"` to a newline character but a literal `\n`.  
After that, the regex engine interprets it as a supported sequence (newline character).

## Do you want to express a literal `\n` in a regular expression?
If you want to use a literal `\n` (not a newline character) as a regular expression, you must express an actual String `\\n` to the regex engine.  

To do that, just write a following source code:
```kotlin
"\\\\n".toRegex()
```

Let us break it down step by step:
```text
Kotlin source code: "\\\\n"
actual String: \\n
regex: literal backslash (\) + n
```

So, you can expect the following usage:
```kotlin
val text = "Hello\\nKotlin" // actual String: Hello\nKotlin (13 characters including a literal backslash)

println(text)
// Hello\nKotlin

println(text.split("\\\\n".toRegex()))
// [Hello, Kotlin]
```
