package ui

import algorithm.SchedulingAlgorithm
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AlgorithmList(
    modifier: Modifier = Modifier,
    algorithms: List<SchedulingAlgorithm>,
    selectedAlgorithm: SchedulingAlgorithm,
    onDropdownSelected: (SchedulingAlgorithm) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    Box(
        modifier = modifier.width(160.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().clickable {
                expanded.value = true
            }.padding(8.dp),
            text = "${selectedAlgorithm.algorithmName}  ▾",
            textAlign = TextAlign.End
        )

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            algorithms.forEach {
                DropdownMenuItem(
                    onClick = {
                        onDropdownSelected(it)
                        expanded.value = false
                    }
                ) {
                    Text(it.algorithmName)
                }
            }
        }
    }
}

@Composable
fun RRQuantumSlider(
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Row {
        Text(
            modifier = Modifier.width(100.dp),
            text = "RR δ"
        )
        BasicTextField(
            value = value.toString(),
            onValueChange = {
                onValueChange(it.toIntOrNull() ?: 0)
            }
        )
    }
}

@Composable
fun Modifier.customBorder(): Modifier {
    return this
        .border(
            width = 1.dp,
            color = MaterialTheme.colors.surface
        )
}