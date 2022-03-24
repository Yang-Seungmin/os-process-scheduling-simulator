package ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import items.Processor

@Composable
fun ProcessorsScreen(
    modifier: Modifier = Modifier,
    processors: List<Processor?>,
    onProcessorChange: (Int, Processor?) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        processors.forEachIndexed { index, value ->
            ProcessorControlPanel(
                modifier = Modifier.weight(1f / processors.size),
                processor = value,
                coreNumber = index,
                onProcessorChange = {
                    onProcessorChange(index, it)
                }
            )
        }
    }
}


@Composable
fun ProcessorControlPanel(
    modifier: Modifier = Modifier,
    coreNumber: Int,
    processor: Processor?,
    onProcessorChange: (Processor?) -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .height(200.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onBackground,
                shape = MaterialTheme.shapes.large
            )
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp),
            text = "Core $coreNumber",
            fontWeight = FontWeight.Bold
        )

        listOf(
            "Off" to null,
            "P-Core" to Processor.PCore,
            "E-Core" to Processor.ECore
        ).forEach {
            Row(
                modifier = Modifier.selectable(
                    selected = processor == it.second,
                    onClick = { onProcessorChange(it.second) }
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = processor == it.second,
                    onClick = { onProcessorChange(it.second) }
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it.first)
            }
        }
    }
}