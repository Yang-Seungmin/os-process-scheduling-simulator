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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import manager.CoreManager
import model.Core
import util.toPx
import kotlin.math.roundToInt
import kotlin.math.sqrt


@Composable
fun CoresScreen(
    modifier: Modifier = Modifier,
    coreManager: CoreManager,
    coreList: List<Core?>,
    enabled: Boolean,
    totalPowerConsumptions: Map<Core, Double>,
    utilization: Map<Core, Double>,
    onCoreChange: (Int, Core?) -> Unit
) {
    val sum = totalPowerConsumptions.values.sum()
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            val columnSize = if((sqrt(coreList.size.toDouble() * 2)).roundToInt() > 4) (sqrt(coreList.size.toDouble() * 2)).roundToInt() else 4

            coreList.chunked(columnSize).forEachIndexed { index1, cores ->
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    cores.forEachIndexed { index2, core ->
                        CoreControlPanel(
                            modifier = Modifier.weight(1f),
                            core = core,
                            coreNumber = (index1 * columnSize) + index2,
                            onProcessorChange = {
                                onCoreChange((index1 * columnSize) + index2, it)
                            },
                            totalPowerConsumption = totalPowerConsumptions[core] ?: 0.0,
                            utilization = utilization[core] ?: 0.0,
                            enabled = enabled,
                            coreManager = coreManager
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            CoreInfoBox(
                modifier = Modifier.weight(1f),
                name = "Total power consumption",
                value = "${String.format("%.1f", sum)}W"
            )
            CoreInfoBox(
                modifier = Modifier.weight(1f),
                name = "Avg. utilization",
                value = "${String.format("%.2f", utilization.values.average() * 100)}%"
            )
        }
    }
}

@Composable
fun CoreControlPanel(
    modifier: Modifier = Modifier,
    coreManager: CoreManager,
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
            .padding(bottom = 8.dp)
            .heightIn(min = 120.dp, max = 240.dp)
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
                            "P-Core" -> coreManager.setPCore(coreNumber)
                            "E-Core" -> coreManager.setECore(coreNumber)
                            else -> coreManager.setCoreOff(coreNumber)
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

@Composable
fun CoreInfoBox(
    modifier: Modifier = Modifier,
    name: String,
    value: String
) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .customBorder()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name)
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = value,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.subtitle1
        )
    }
}