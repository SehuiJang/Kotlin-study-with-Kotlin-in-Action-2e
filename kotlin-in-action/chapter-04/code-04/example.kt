import java.io.Serializable
import java.io.ObjectOutputStream
import java.io.ByteArrayOutputStream

interface State : Serializable

interface View {
    fun getCurrentState(): State
    fun restoreState(state: State) { /* ... */ }
}

class Button : View {
    override fun getCurrentState(): State = ButtonState()
    override fun restoreState(state: State) { /* ... */ }

    // Kotlin nested class: static-like by default
    class ButtonState : State { /* ... */ }
}

class ButtonWithInnerState : View {
    override fun getCurrentState(): State = ButtonState()
    override fun restoreState(state: State) { /* ... */ }

    // Kotlin inner class: holds a reference to the outer instance
    inner class ButtonState : State {
        fun getOuterButton(): ButtonWithInnerState = this@ButtonWithInnerState
    }
}

// a temporal `serialize` function
fun serializeToByteArray(value: Any): ByteArray {
    val byteStream = ByteArrayOutputStream()

    ObjectOutputStream(byteStream).use { objectStream ->
        objectStream.writeObject(value)
    }

    return byteStream.toByteArray()
}

fun main() {
    val nestedButton = Button()
    val nestedState = nestedButton.getCurrentState()
    // success
    val bytes1 = serializeToByteArray(nestedState)

    val innerButton = ButtonWithInnerState()
    val innerState = innerButton.getCurrentState()
    // fail
    val bytes2 = serializeToByteArray(innerState)  // Exception in thread "main" java.io.NotSerializableException: ButtonWithInnerState

}