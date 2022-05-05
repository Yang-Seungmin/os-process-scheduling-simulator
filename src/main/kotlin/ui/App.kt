package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import kotlinx.coroutines.CoroutineScope
import manager.CoreManager
import manager.ProcessManager
import schedulingalgorithm.SchedulingAlgorithmRunner
import ui.state.GanttChartState
import ui.state.ReadyQueueState
import ui.state.ResultState
import ui.state.AlgorithmRunningState


@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun FrameWindowScope.MainScreen(
    coroutineScope: CoroutineScope,
    schedulingAlgorithmRunner: SchedulingAlgorithmRunner,
    coreManager: CoreManager,
    processManager: ProcessManager,
    readyQueueState: ReadyQueueState,
    ganttChartState: GanttChartState,
    resultState: ResultState,
    randomProcessGeneratorOpen: () -> Unit
) {

    MaterialTheme(
        colors = Colors,
        typography = Typography
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onBackground) {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background).padding(8.dp)) {
                Column {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.height(60.dp)
                        ) {
                            ApplicationBranding(modifier = Modifier.weight(1f).fillMaxHeight())
                            AlgorithmRunnerTool(
                                coroutineScope = coroutineScope,
                                schedulingAlgorithmRunner = schedulingAlgorithmRunner
                            )
                        }
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.weight(3f)
                            ) {
                                ProcessesScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    processManager = processManager,
                                    enabled = schedulingAlgorithmRunner.algorithmRunnerState.algorithmRunningState != AlgorithmRunningState.Running,
                                )
                            }

                            Column(
                                modifier = Modifier.weight(4f)
                            ) {
                                CoresScreen(
                                    modifier = Modifier.weight(1f),
                                    coreManager = coreManager,
                                    onCoreChange = { i, core ->
                                    },
                                    enabled = schedulingAlgorithmRunner.algorithmRunnerState.algorithmRunningState != AlgorithmRunningState.Running
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.weight(3f)
                            ) {
                                ResultScreen(
                                    modifier = Modifier.fillMaxHeight(),
                                    resultState = resultState
                                )
                            }

                            Column(
                                modifier = Modifier.weight(4f)
                            ) {
                                ReadyQueue(readyQueueState = readyQueueState)
                                GanttChart(
                                    ganttChartState = ganttChartState,
                                    processState = processManager.processState,
                                    algorithmRunnerState = schedulingAlgorithmRunner.algorithmRunnerState
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
