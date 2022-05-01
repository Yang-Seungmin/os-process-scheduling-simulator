package ui

import schedulingalgorithm.SchedulingAlgorithm
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import jdk.jfr.Enabled
import ui.state.AlgorithmRunningState

@Composable
fun AlgorithmList(
    modifier: Modifier = Modifier,
    algorithms: List<SchedulingAlgorithm>,
    selectedAlgorithm: SchedulingAlgorithm,
    enabled: Boolean,
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
            expanded = expanded.value && enabled,
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
fun RowScope.RRQuantumSlider(
    value: Int,
    enabled: Boolean,
    onValueChange: (Int) -> Unit
) {
    Text(
        modifier = Modifier.width(100.dp),
        text = "RR δ"
    )
    BasicTextField(
        modifier = Modifier.customBorder().padding(2.dp),
        value = value.toString(),
        onValueChange = {
            onValueChange(it.toIntOrNull() ?: 0)
        },
        textStyle = TextStyle.Default.copy(textAlign = TextAlign.End),
        enabled = enabled
    )
}

@Composable
fun RowScope.AlgorithmRunController(
    algorithmRunningState: AlgorithmRunningState,
    onStateChanged: (AlgorithmRunningState) -> Unit
) {
    Box(
        modifier = Modifier.padding(start = 12.dp).padding(vertical = 2.dp).width(120.dp)
            .fillMaxHeight()
    ) {
        if (algorithmRunningState == AlgorithmRunningState.Stopped) {
            Button(
                modifier = Modifier.fillMaxSize(),
                onClick = {
                    onStateChanged(AlgorithmRunningState.Running)
                }
            ) {
                Text(
                    text = "RUN!!",
                    style = MaterialTheme.typography.h6
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                //pause and restart
                Button(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    onClick = {
                        if (algorithmRunningState == AlgorithmRunningState.Running) {
                            onStateChanged(AlgorithmRunningState.Paused)
                        } else {
                            onStateChanged(AlgorithmRunningState.Running)
                        }
                    }
                ) {
                    Text(
                        text =
                        if (algorithmRunningState == AlgorithmRunningState.Running) {
                            "⏸︎"
                        } else {
                            "▶︎"
                        },
                        style = MaterialTheme.typography.h6
                    )
                }

                //stop
                Button(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    onClick = {
                        onStateChanged(AlgorithmRunningState.Stopped)
                    }
                ) {
                    Text(
                        text = "■",
                        style = MaterialTheme.typography.h6
                    )
                }
            }
        }
    }
}