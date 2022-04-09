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
import model.Core
import kotlin.math.roundToInt

@Composable
fun CoresScreen(
    modifier: Modifier = Modifier,
    cores: List<Core?>,
    enabled: Boolean,
    totalPowerConsumptions: Map<Core, Double>,
    onProcessorChange: (Int, Core?) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        cores.forEachIndexed { index, core ->
            CoreControlPanel(
                modifier = Modifier.weight(1f / cores.size),
                core = core,
                coreNumber = index,
                onProcessorChange = {
                    onProcessorChange(index, it)
                },
                totalPowerConsumption = totalPowerConsumptions[core] ?: 0.0,
                enabled = enabled
            )
        }
    }
}


@Composable
fun CoreControlPanel(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    coreNumber: Int,
    core: Core?,
    totalPowerConsumption: Double,
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

        listOf("OFF", "P-Core", "E-Core").forEach {
            val opc = {
                if (enabled) onProcessorChange(
                    when (it) {
                        "P-Core" -> Core.PCore("Core $coreNumber [${it}]")
                        "E-Core" -> Core.ECore("Core $coreNumber [${it}]")
                        else -> null
                    },
                )
            }

            Row(
                modifier = Modifier.selectable(
                    selected = (core?.name ?: "OFF") == it,
                    onClick = opc
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    enabled = enabled,
                    selected = when (it) {
                        "P-Core" -> core is Core.PCore
                        "E-Core" -> core is Core.ECore
                        else -> core == null
                    },
                    onClick = opc
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it
                )
            }
        }

        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp),
            text = "${(totalPowerConsumption * 10).roundToInt() / 10.0}W",
            style = MaterialTheme.typography.caption
        )
    }
}