package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import schedulingalgorithm.RR
import schedulingalgorithm.SchedulingAlgorithmRunner
import ui.state.AlgorithmRunningState

@Composable
fun ApplicationBranding(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.padding(8.dp).fillMaxHeight(),
            painter = painterResource("logo_n.png"),
            contentDescription = "",
            contentScale = ContentScale.FillHeight
        )

        Column {
            Text(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                text = "Process Scheduling Simulator",
                style = MaterialTheme.typography.h5,
                maxLines = 1
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                text = "1st Team v1.0.0"
            )
        }
    }
}

@Composable
fun AlgorithmRunnerTool(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope,
    schedulingAlgorithmRunner: SchedulingAlgorithmRunner,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .customBorder()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        with(schedulingAlgorithmRunner.algorithmRunnerState) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(100.dp),
                        text = "Algorithm : ",
                        style = MaterialTheme.typography.subtitle1
                    )
                    AlgorithmList(
                        modifier = Modifier.customBorder(),
                        algorithms = schedulingAlgorithmRunner.schedulingAlgorithms,
                        selectedAlgorithm = schedulingAlgorithm,
                        enabled = algorithmRunningState == AlgorithmRunningState.Stopped || algorithmRunningState == AlgorithmRunningState.Ended
                    ) {
                        schedulingAlgorithm = it
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RRQuantumSlider(
                        rrQuantum,
                        enabled = algorithmRunningState == AlgorithmRunningState.Stopped || algorithmRunningState == AlgorithmRunningState.Ended
                    ) {
                        rrQuantum = it
                    }
                }
            }


            AlgorithmControllerButtons(algorithmRunningState) { newState ->
                if (algorithmRunningState == AlgorithmRunningState.Stopped && newState == AlgorithmRunningState.Running ||
                    algorithmRunningState == AlgorithmRunningState.Ended && newState == AlgorithmRunningState.Running
                ) {
                    if (schedulingAlgorithm is RR) (schedulingAlgorithm as RR).rrQuantum = rrQuantum

                    with(schedulingAlgorithmRunner) {
                        coroutineScope.run(
                            onTimeElapsed = {},
                            onEnd = {
                                algorithmRunningState = AlgorithmRunningState.Ended
                            }, algorithmRunnerState.interval
                        )
                    }

                } else if (algorithmRunningState == AlgorithmRunningState.Running && newState == AlgorithmRunningState.Paused) {
                    schedulingAlgorithmRunner.pause()
                } else if (algorithmRunningState == AlgorithmRunningState.Paused && newState == AlgorithmRunningState.Running) {
                    schedulingAlgorithmRunner.restart()
                } else if (newState == AlgorithmRunningState.Stopped) {
                    schedulingAlgorithmRunner.stop()
                }

                algorithmRunningState = newState
            }

            Column(
                modifier = Modifier.width(150.dp).padding(8.dp),
            ) {
                Text(
                    modifier = Modifier,
                    text = "Time : ${time}s${if (algorithmRunningState == AlgorithmRunningState.Ended) " (END)" else ""}",
                    style = MaterialTheme.typography.subtitle1
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Interval "
                    )
                    BasicTextField(
                        modifier = Modifier.weight(1f).customBorder().padding(2.dp),
                        value = interval.toString(),
                        onValueChange = {
                            interval = it.toLongOrNull() ?: 100
                        },
                        textStyle = TextStyle.Default.copy(textAlign = TextAlign.End)
                    )
                    Text(
                        text = " ms"
                    )
                }
            }
        }
    }
}