fun main() {
    println("12.345-6.A".split("\\.|-".toRegex()))
    // [12, 345, 6, A]
    // if you use "\.|-".toRegex(), it leads to an error "example.kt:2:33: error: unsupported escape sequence, because of `\.`".

    println("12.345-6.A".split(".", "-"))
    // [12, 345, 6, A]
}