package ec.edu.uisek.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// --- ESTRUCTURAS DE DATOS ---

// 1. Estado: Lo que la UI necesita saber para renderizarse.
data class CalculatorState(
    val display: String = "0"
)

// 2. Eventos: Las acciones que la UI envía al ViewModel.
sealed class CalculatorEvent {
    data class Number(val number: String) : CalculatorEvent()
    data class Operator(val operator: String) : CalculatorEvent()
    object Clear : CalculatorEvent() // Borrar último
    object AllClear : CalculatorEvent() // Borrar todo
    object Calculate : CalculatorEvent() // Igual
    object Decimal : CalculatorEvent() // Punto decimal
}


// --- VIEW MODEL (LÓGICA DE NEGOCIO) ---

class CalculatorViewModel : ViewModel() {

    // Variables internas para la lógica de la calculadora (no observadas por la UI directamente)
    private var number1: String = ""
    private var number2: String = ""
    private var operator: String? = null

    // Estado observable por la UI
    var state by mutableStateOf(CalculatorState())
        private set

    // Router de eventos: La única función pública para interactuar con el ViewModel
    fun onEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.Number -> enterNumber(event.number)
            is CalculatorEvent.Operator -> enterOperator(event.operator)
            is CalculatorEvent.Decimal -> enterDecimal()
            is CalculatorEvent.AllClear -> clearAll()
            is CalculatorEvent.Clear -> clearLast()
            is CalculatorEvent.Calculate -> performCalculation()
        }
    }

    // --- LÓGICA DE IMPLEMENTACIÓN ---

    private fun enterNumber(number: String) {
        if (operator == null) {
            // Maneja el caso de empezar después de un '0' o un 'Error'
            if (number1 == "0" || state.display == "Error") {
                number1 = number
            } else {
                number1 += number
            }
            state = state.copy(display = number1)
        } else {
            // Número 2 (después de un operador)
            number2 += number
            state = state.copy(display = number2)
        }
    }

    private fun enterOperator(op: String) {
        // Asigna el operador solo si el número 1 ya fue ingresado
        if (number1.isNotBlank()) {
            operator = op
            // Opcional: Podrías mostrar el operador en la pantalla si lo deseas,
            // pero para este diseño simple, solo actualizamos el estado interno.
        }
    }

    private fun enterDecimal() {
        val currentNumber = if (operator == null) number1 else number2

        // Asegura que no haya ya un punto decimal
        if (!currentNumber.contains(".")) {
            if (operator == null) {
                number1 = if (number1.isBlank()) "0." else "$number1."
                state = state.copy(display = number1)
            } else {
                number2 = if (number2.isBlank()) "0." else "$number2."
                state = state.copy(display = number2)
            }
        }
    }

    private fun performCalculation() {
        val num1 = number1.toDoubleOrNull()
        val num2 = number2.toDoubleOrNull()

        if (num1 != null && num2 != null && operator != null) {
            val result = when (operator) {
                "+" -> num1 + num2
                "−" -> num1 - num2
                "×" -> num1 * num2
                "÷" -> if (num2 != 0.0) num1 / num2 else Double.NaN // División por cero
                else -> 0.0
            }

            // Muestra y prepara el resultado
            val resultString = if (result.isNaN())
                "Error"
            else
            // Elimina el ".0" si el número es entero (ej: 2.0 -> 2)
                result.toString().removeSuffix(".0")

            // Restablece la lógica para una nueva operación
            clearAll()

            // Si no fue un error, el resultado pasa a ser el número 1 para encadenar operaciones
            number1 = if (resultString == "Error") "" else resultString
            state = state.copy(display = resultString)
        }
    }

    private fun clearLast() {
        // Botón 'C': Borrar el último dígito o el operador
        if (operator == null) {
            if (number1.isNotBlank()) {
                number1 = number1.dropLast(1)
                // Si borramos todo, volvemos a "0"
                state = state.copy(display = if (number1.isBlank()) "0" else number1)
            }
        } else {
            if (number2.isNotBlank()) {
                number2 = number2.dropLast(1)
                state = state.copy(display = if (number2.isBlank()) "0" else number2)
            } else {
                // Si no hay número 2, borramos el operador y volvemos a mostrar el número 1
                operator = null
                state = state.copy(display = number1)
            }
        }
    }

    private fun clearAll() {
        // Botón 'AC': Limpiar todo
        number1 = ""
        number2 = ""
        operator = null
        state = state.copy(display = "0")
    }
}