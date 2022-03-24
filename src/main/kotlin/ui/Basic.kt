package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AlgorithmList(
    modifier: Modifier = Modifier,
    dropdownItem: String,
    onDropdownSelected: (String) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    Box(
        modifier = modifier.width(160.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().clickable {
                expanded.value = true
            }.padding(8.dp),
            text = "$dropdownItem  ▾",
            textAlign = TextAlign.End
        )

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            listOf(
                "FCFS", "RR"
            ).forEach {
                DropdownMenuItem(
                    onClick = {
                        onDropdownSelected(it)
                        expanded.value = false
                    }
                ) {
                    Text(it)
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
            text = "RR δ")
        BasicTextField(
            value = value.toString(),
            onValueChange = {
                onValueChange(it.toIntOrNull() ?: 0)
            }
        )
    }
}