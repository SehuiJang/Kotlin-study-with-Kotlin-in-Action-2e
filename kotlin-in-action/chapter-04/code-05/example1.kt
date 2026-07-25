interface Expr

class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr

// Expr is a regular interface (not sealed interface)
// -> It is necessary for `when` expression to have an `else` branch.
fun eval(e: Expr): Int =
    when (e) {
        is Num -> e.value
        is Sum -> eval(e.left) + eval(e.right)
        else -> throw IllegalArgumentException("Unknown expression")    // if you comment out this line, you would encounter an exception
    }

fun main() {
    print(eval(Sum(Num(1), Num(2))))
}