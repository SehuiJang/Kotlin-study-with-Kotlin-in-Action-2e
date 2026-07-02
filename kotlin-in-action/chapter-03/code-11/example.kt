// Using vararg -> getting Int arguments, regardless of the number of them
fun sumAll(vararg numbers: Int): Int {
    println(numbers.javaClass)	// class [I
    							// '[' means 'array' and 'I' means 'int' (a primitive type in Java)
                                // 'class [I' means int[] (integer array) which is JVM internal symbol
    var total = 0
    for (num in numbers) {
        total += num
    }
    return total
}

fun main() {
    println(sumAll(1, 2))
    // class [I
    // 3
    println(sumAll(10, 20, 30, 40))
    // class [I
    // 100
    println(sumAll())
    // class [I
    // 0 (The absence of arguments is also OK)
    
    val numbersArray = intArrayOf(10, 20, 30)	// Its javaClass is also 'class [I'

    // pass `numbersArray` with a spread operator (*) before it
    val result = sumAll(*numbersArray) 
    println(result)
    // class [I
    // 60
}
