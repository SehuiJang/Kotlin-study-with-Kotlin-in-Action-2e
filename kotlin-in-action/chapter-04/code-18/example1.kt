@JvmInline
value class UsdCent(val amount: Int)

fun addExpense(expense: UsdCent) {
    // save the expense as USD cent
    println("${expense.amount}¢ is saved!")
}

fun main() {
    addExpense(UsdCent(147))
    // 147¢ is saved!
}