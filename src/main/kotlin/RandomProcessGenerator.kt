import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import manager.ProcessManager
import ui.exportFromJsonFileDialog
import util.generateRandomProcesses

val sizeMaxRange = 0..15
val arrivalTimeMaxRange = 0..30
val workloadMaxRange = 1..50

@Composable
fun RandomProcessGenerator(
    processManager: ProcessManager,
    onCloseRequest: () -> Unit
) = Window(
    title = "Random Process Generator",
    onCloseRequest = onCloseRequest,
    state = rememberWindowState(
        size = DpSize(400.dp, 300.dp)
    ),
    alwaysOnTop = true,
    resizable = false
) {
    val coroutineScope = rememberCoroutineScope()
    val itemCount = remember { mutableStateOf(1f) }
    val arrivalTimeRange = remember { mutableStateOf(0f..1f) }
    val workloadRange = remember { mutableStateOf(0f..1f) }

    val actualItemCount = calculateValue(itemCount.value, sizeMaxRange)
    val actualArrivalTimeRange = calculateRange(arrivalTimeRange.value, arrivalTimeMaxRange)
    val actualWorkloadRange = calculateRange(workloadRange.value, workloadMaxRange)

    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp)
    ) {
        SliderItem(
            modifier = Modifier.fillMaxWidth(),
            text = "Item Count : $actualItemCount",
            value = itemCount.value,
            onValueChange = {
                itemCount.value = it
            }
        )
        RangeSliderItem(
            modifier = Modifier.fillMaxWidth(),
            text = "Arrival Time Range : (previous arrival time) + [${actualArrivalTimeRange.first} ~ ${actualArrivalTimeRange.last}]",
            value = arrivalTimeRange.value,
            onValueChange = {
                arrivalTimeRange.value = it
            }
        )
        RangeSliderItem(
            modifier = Modifier.fillMaxWidth(),
            text = "Workload Range : [${actualWorkloadRange.first} ~ ${actualWorkloadRange.last}]",
            value = workloadRange.value,
            onValueChange = {
                workloadRange.value = it
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    coroutineScope.launch {
                        processManager.clearProcess()
                        delay(10)
                        processManager.addProcesses(
                            generateRandomProcesses(
                                size = actualItemCount,
                                btRange = actualWorkloadRange,
                                atRange = actualArrivalTimeRange
                            )
                        )
                    }
                }) {
                Text("Generate")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    exportFromJsonFileDialog(ComposeWindow())?.let { file ->
                        Json.encodeToStream(
                            generateRandomProcesses(
                                size = actualItemCount,
                                btRange = actualWorkloadRange,
                                atRange = actualArrivalTimeRange
                            ), file.outputStream()
                        )
                    }
                }) {
                Text("Save to...")
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RangeSliderItem(
    modifier: Modifier = Modifier,
    text: String,
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text)
        RangeSlider(
            values = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
private fun SliderItem(
    modifier: Modifier = Modifier,
    text: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text)
        Slider(
            value = value,
            onValueChange = onValueChange
        )
    }
}

fun calculateValue(
    value: Float,
    intRange: IntRange
) = (value * (intRange.last - intRange.first) + intRange.first).toInt()

fun calculateRange(
    closedFloatingPointRange: ClosedFloatingPointRange<Float>,
    intRange: IntRange
): IntRange =
    (closedFloatingPointRange.start * (intRange.last - intRange.first) + intRange.first).toInt()..
            (closedFloatingPointRange.endInclusive * (intRange.last - intRange.first) + intRange.first).toInt()
