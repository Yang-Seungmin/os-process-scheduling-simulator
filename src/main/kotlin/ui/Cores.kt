package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    utilization: Map<Core, Double>,
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
                utilization = utilization[core] ?: 0.0,
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
    onProcessorChange: (Core?) -> Unit,
    utilization: Double
) {
    Box(
        modifier
            .padding(horizontal = 8.dp)
            .fillMaxHeight()
            .customBorder(),
        contentAlignment = Alignment.TopEnd
    ) {
        core?.process?.let {
            Box(
                modifier = Modifier.height(20.dp)
                    .defaultMinSize(minWidth = 20.dp)
                    .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
                    .background(Color(it.processColor))
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = it.processName, style = MaterialTheme.typography.subtitle1)
            }
        }

        Column(
            modifier = Modifier.fillMaxHeight()
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
                    ).weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        modifier = Modifier,
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
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                text = "${(totalPowerConsumption * 10).roundToInt() / 10.0}W\n${(utilization * 10000).roundToInt() / 100.0}%",
                style = MaterialTheme.typography.caption
            )
        }
    }
}