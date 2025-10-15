package ec.edu.uisek.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.uisek.calculator.ui.theme.Purple40
import ec.edu.uisek.calculator.ui.theme.UiSekBlue

// Definición de Color (Solo si no está en ui.theme/Color.kt)
val BurgundyAC = Color(0xFF800020)

@Composable
fun CalculatorScreen(
    // 1. Inyecta el ViewModel
    viewModel: CalculatorViewModel = viewModel()
) {
    // 2. Lee el estado que observa la UI
    val state = viewModel.state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        // Pantalla de visualización (Muestra el estado 'display')
        Text(
            // Muestra el valor que proviene del ViewModel
            text = state.display,
            color = Color.White,
            fontSize = 80.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp, bottom = 20.dp)
        )

        // Cuadrícula de botones (Pasa la función de manejo de eventos)
        CalculatorGrid(onEvent = viewModel::onEvent)
    }
}

// ---

@Composable
fun CalculatorGrid(onEvent: (CalculatorEvent) -> Unit) {

    // Lista de botones para el mapeo de eventos
    val buttons = listOf(
        "7", "8", "9", "÷",
        "4", "5", "6", "×",
        "1", "2", "3", "−",
    )

    Column(modifier = Modifier.fillMaxWidth()) {

        // Sección 1: Cuadrícula principal (3x4 de números/operadores y la fila 0)
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            // Botones 7 al - (3 filas)
            items(buttons) { label ->
                CalculatorButton(label = label) {
                    // Mapeo de etiqueta a evento del ViewModel
                    when (label) {
                        in "0".."9" -> onEvent(CalculatorEvent.Number(label))
                        "÷", "×", "−", "+" -> onEvent(CalculatorEvent.Operator(label))
                        else -> Unit // Caso por defecto
                    }
                }
            }

            // Fila 4: Botón '0' (ocupa 2 columnas)
            item(span = { GridItemSpan(2) }) {
                ZeroButton(label = "0") { onEvent(CalculatorEvent.Number("0")) }
            }

            // Fila 4: Botón '.'
            item {
                CalculatorButton(label = ".", color = Purple40, shape = CircleShape) {
                    onEvent(CalculatorEvent.Decimal)
                }
            }

            // Fila 4: Botón '='
            item {
                CalculatorButton(label = "=", color = Purple40, shape = CircleShape) {
                    onEvent(CalculatorEvent.Calculate)
                }
            }
        }

        // Sección 2: Botones AC y C (Fila inferior, usa Row y weight para el ancho)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp), // Espacio extra al final
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botón AC (All Clear) - Ocupa 2/3 del ancho disponible
            CalculatorButton(
                label = "AC",
                color = BurgundyAC,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(2f)
                    .aspectRatio(2.5f)
            ) {
                onEvent(CalculatorEvent.AllClear)
            }

            // Botón C (Clear/Delete) - Ocupa 1/3 del ancho disponible y es circular
            CalculatorButton(
                label = "C",
                color = BurgundyAC,
                shape = CircleShape,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                onEvent(CalculatorEvent.Clear)
            }
        }
    }
}


// --- Composables auxiliares para la presentación (sin lógica) ---

@Composable
fun ZeroButton(label: String, onClick: () -> Unit) {
    // Usa RoundedCornerShape y Alignment.CenterStart para imitar el diseño de '0'
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2.1f)
            .clip(RoundedCornerShape(30.dp))
            .background(UiSekBlue)
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(start = 24.dp)
        )
    }
}

@Composable
fun CalculatorButton(
    label: String,
    modifier: Modifier = Modifier.aspectRatio(1f),
    color: Color = if (label in listOf("÷", "×", "−", "+", "=", ".")) Purple40 else UiSekBlue,
    shape: androidx.compose.ui.graphics.Shape = CircleShape,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    // El Preview también usa el ViewModel por defecto
    CalculatorScreen()
}