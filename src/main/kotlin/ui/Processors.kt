package ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import items.Core

@Composable
fun ProcessorsScreen(
    modifier: Modifier = Modifier,
    cores: List<Core?>,
    onProcessorChange: (Int, Core?) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        cores.forEachIndexed { index, value ->
            ProcessorControlPanel(
                modifier = Modifier.weight(1f / cores.size),
                core = value,
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
    core: Core?,
    onProcessorChange: (Core?) -> Unit
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
            "P-Core" to Core.PCore(),
            "E-Core" to Core.ECore()
        ).forEach {
            Row(
                modifier = Modifier.selectable(
                    selected = core == it.second,
                    onClick = { onProcessorChange(it.second) }
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = core == it.second,
                    onClick = { onProcessorChange(it.second) }
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it.first)
            }
        }
    }
}