sealed class Expr

class Num(val value: Int) : Expr()
class Sum(val left: Expr, val right: Expr) : Expr()

// Expr is a regular sealed interface
// -> It is not necessary for the `when` expression to contain an `else` branch.
fun eval(e: Expr): Int =
    when (e) {
        is Num -> e.value
        is Sum -> eval(e.left) + eval(e.right)
    }

fun main() {
    print(eval(Sum(Num(1), Num(2))))
}