sealed class Expr

class Num(val value: Int) : Expr()
class Sum(val left: Expr, val right: Expr) : Expr()
class Mul(val left: Expr, val right: Expr) : Expr()

fun eval(e: Expr): Int =
    when (e) {
        is Num -> e.value
        is Sum -> eval(e.left) + eval(e.right)
        is Mul -> eval(e.left) * eval(e.right)
    }

/*
If you comment out the `is Mul` branch, you must see the following message:

example3.kt:8:5: error: 'when' expression must be exhaustive. Add the 'is Mul' branch or an 'else' branch.
    when (e) {
    ^^^^
*/

fun main() {
    print(eval(Mul(Sum(Num(1), Num(2)), Num(10))))
}