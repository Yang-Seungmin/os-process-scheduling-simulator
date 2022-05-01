package ui

import RandomProcessGenerator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.Key.Companion.Menu
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.WindowScope
import kotlinx.coroutines.launch
import manager.CoreManager
import manager.ProcessManager
import model.normalizedTurnAroundTime
import schedulingalgorithm.*
import ui.state.AlgorithmRunningState
import ui.state.UiState
import util.toPx
import kotlin.math.roundToInt


@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun FrameWindowScope.MainScreen(
    schedulingAlgorithmRunner: SchedulingAlgorithmRunner,
    coreManager: CoreManager,
    processManager: ProcessManager,
    randomProcessGeneratorOpen: () -> Unit
) {
    //For algorithm
    val coroutineScope = rememberCoroutineScope()
    var interval by remember { mutableStateOf(100L) }
    val selectedAlgorithm = rememberSaveable { mutableStateOf(schedulingAlgorithmRunner.schedulingAlgorithm) }
    var rrQuantum by remember { mutableStateOf(2) }
    var algorithmRunningState: AlgorithmRunningState by remember { mutableStateOf(AlgorithmRunningState.Stopped) }

    //For chart accumulation
    val maxAccumulation = 160.dp
    var accumulationLevel by remember { mutableStateOf(8f) }
    val accumulationLevelAnimate by animateFloatAsState(accumulationLevel)

    //For scroll
    var autoScrollThreshold by remember { mutableStateOf(0) }
    val scrollState = rememberLazyListState()
    var offset by remember { mutableStateOf(0) }
    offset = (autoScrollThreshold / (maxAccumulation / accumulationLevel).toPx()).roundToInt()

    //For process
    val processScrollState = rememberLazyListState()

    //For result
    val resultScrollState = rememberLazyListState()

    //UI State
    var uiState by remember { mutableStateOf(UiState.default()) }

    val onExport: () -> Unit = {
        exportFromJsonFileDialog(ComposeWindow())?.let { file ->
            processManager.exportProcessesToFile(file)
        }
    }

    val onImport: () -> Unit = {
        importFromJsonFileDialog(ComposeWindow())?.let { file ->
            processManager.importProcessesFromFile(file)
        }
        coroutineScope.launch {
            processScrollState.scrollToItem(processManager.size)
        }
    }

    MenuBar {
        Menu(
            text = "Process",
            mnemonic = 'P'
        ) {
            Item(
                text = "Import process list from JSON file",
                onClick = onImport,
                shortcut = KeyShortcut(Key.I, ctrl = true)
            )
            Item(
                text = "Export process list to JSON file",
                onClick = onExport,
                shortcut = KeyShortcut(Key.E, ctrl = true)
            )
            Separator()
            Item(
                text = "Open random process generator",
                onClick = randomProcessGeneratorOpen
            )
        }
    }

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
                            Row(
                                modifier = Modifier.weight(1f).fillMaxHeight(),
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

                            Row(
                                modifier = Modifier
                                    //.weight(4f)
                                    .padding(horizontal = 8.dp)
                                    .customBorder()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
                                            selectedAlgorithm = selectedAlgorithm.value,
                                            enabled = algorithmRunningState == AlgorithmRunningState.Stopped
                                        ) {
                                            schedulingAlgorithmRunner.schedulingAlgorithm = it
                                            selectedAlgorithm.value = it
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.padding(top = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RRQuantumSlider(
                                            rrQuantum,
                                            enabled = algorithmRunningState == AlgorithmRunningState.Stopped
                                        ) {
                                            rrQuantum = it
                                        }
                                    }
                                }

                                AlgorithmRunController(algorithmRunningState) { newState ->
                                    if (algorithmRunningState == AlgorithmRunningState.Stopped && newState == AlgorithmRunningState.Running) {
                                        with(schedulingAlgorithmRunner) {
                                            if (schedulingAlgorithm is RR) (schedulingAlgorithm as RR).rrQuantum =
                                                rrQuantum

                                            setCores(coreManager.cores.filterNotNull())
                                            setProcesses(processManager.processes)

                                            coroutineScope.run(
                                                onTimeElapsed = {

                                                    uiState = refreshUiState(uiState)

                                                    coroutineScope.launch {
                                                        scrollState.scrollToItem(with(time - offset) { if (this > 0) this else 0 })
                                                        resultScrollState.scrollToItem(if (uiState.executeResult.size - 1 < 0) 0 else uiState.executeResult.size - 1)
                                                    }
                                                },
                                                onEnd = {
                                                    uiState = uiState.copy(
                                                        time = "${time}s (END)"
                                                    )
                                                    algorithmRunningState = AlgorithmRunningState.Stopped
                                                }, interval
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
                                        text = "Time : ${uiState.time}",
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
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            //프로세서
                            Row(
                                modifier = Modifier.weight(3f)
                            ) {
                                // 프로세스
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row {
                                        Text(
                                            modifier = Modifier.padding(8.dp),
                                            text = "Processes (${processManager.processesStateList.size})",
                                            style = MaterialTheme.typography.subtitle1
                                        )

                                        Box(modifier = Modifier.padding(8.dp))

                                        Text(
                                            modifier = Modifier
                                                .clickable {
                                                    processManager.clearProcess()
                                                }
                                                .padding(8.dp),
                                            text = "Clear processes",
                                            color = MaterialTheme.colors.primary
                                        )

                                        Text(
                                            modifier = Modifier
                                                .clickable {
                                                    onImport()
                                                }
                                                .padding(8.dp),
                                            text = "Import from..",
                                            color = MaterialTheme.colors.primary
                                        )

                                        Text(
                                            modifier = Modifier
                                                .clickable {
                                                    onExport()
                                                }
                                                .padding(8.dp),
                                            text = "Export to..",
                                            color = MaterialTheme.colors.primary
                                        )
                                    }

                                    ProcessesScreen(
                                        modifier = Modifier.fillMaxHeight(),
                                        processes = processManager.processesStateList,
                                        onProcessAdd = { name, arrivalTime, workload ->
                                            processManager.addProcess(name, arrivalTime, workload)
                                            coroutineScope.launch {
                                                processScrollState.animateScrollToItem(processManager.size)
                                            }
                                        },
                                        onProcessUpdate = { before, after ->
                                            processManager.modifyProcess(before, after)
                                        },
                                        onProcessDuplicate = { process ->
                                            processManager.duplicateProcess(process)
                                        },
                                        onProcessDelete = {
                                            processManager.removeProcess(it)
                                        },
                                        enabled = algorithmRunningState != AlgorithmRunningState.Running,
                                        scrollState = processScrollState
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.weight(4f)
                            ) {
                                //프로세서
                                Row {
                                    Text(
                                        modifier = Modifier.padding(8.dp),
                                        text = "Processor (${coreManager.coreState.size})",
                                        style = MaterialTheme.typography.subtitle1
                                    )

                                    Text(
                                        modifier = Modifier.clickable {
                                            if (algorithmRunningState != AlgorithmRunningState.Running)
                                                coreManager.addCore()
                                        }.padding(8.dp),
                                        text = "+",
                                        style = MaterialTheme.typography.subtitle1,
                                        color = MaterialTheme.colors.primary
                                    )

                                    Text(
                                        modifier = Modifier.clickable {
                                            if (algorithmRunningState != AlgorithmRunningState.Running)
                                                coreManager.removeCore()
                                        }.padding(8.dp),
                                        text = "-",
                                        style = MaterialTheme.typography.subtitle1,
                                        color = MaterialTheme.colors.primary
                                    )
                                }

                                CoresScreen(
                                    modifier = Modifier.weight(1f),
                                    coreManager = coreManager,
                                    onCoreChange = { i, core ->
                                        uiState =
                                            uiState.copy(
                                                ganttChartMap = coreManager.cores.filterNotNull()
                                                    .associateWith { listOf() }
                                            )
                                    },
                                    totalPowerConsumptions = uiState.totalPowerConsumptions,
                                    utilization = uiState.utilizationTimeLine.mapValues {
                                        it.value.lastOrNull() ?: 0.0
                                    },
                                    enabled = algorithmRunningState != AlgorithmRunningState.Running,
                                    coreList = coreManager.coreState
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            //결과
                            Column(
                                modifier = Modifier.weight(3f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(
                                        modifier = Modifier.padding(8.dp),
                                        text = "Result (${uiState.executeResult.size})",
                                        style = MaterialTheme.typography.subtitle1
                                    )

                                    Text(
                                        modifier = Modifier.padding(8.dp),
                                        text = "Average NTT : ${
                                            String.format(
                                                "%.3f",
                                                uiState.executeResult.map { it.normalizedTurnAroundTime }.average()
                                            )
                                        }",
                                        style = MaterialTheme.typography.body1
                                    )
                                }


                                ResultScreen(
                                    modifier = Modifier.fillMaxHeight(),
                                    uiState.executeResult,
                                    resultScrollState
                                )
                            }

                            Column(
                                modifier = Modifier.weight(4f)
                            ) {
                                //레디큐
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = "Ready Queue",
                                    style = MaterialTheme.typography.subtitle1
                                )

                                ReadyQueueList(
                                    readyQueues = uiState.readyQueue
                                )

                                //간트차트
                                Row {
                                    Text(
                                        modifier = Modifier.padding(8.dp),
                                        text = "Gantt Chart",
                                        style = MaterialTheme.typography.subtitle1
                                    )

                                    Text(
                                        modifier = Modifier.clickable {
                                            if (accumulationLevel > 1)
                                                accumulationLevel /= 2
                                        }.padding(8.dp),
                                        text = "+",
                                        style = MaterialTheme.typography.subtitle1,
                                        color = MaterialTheme.colors.primary
                                    )

                                    Text(
                                        modifier = Modifier.clickable {
                                            if (accumulationLevel < 32)
                                                accumulationLevel *= 2
                                        }.padding(8.dp),
                                        text = "-",
                                        style = MaterialTheme.typography.subtitle1,
                                        color = MaterialTheme.colors.primary
                                    )
                                }

                                GanttChart(
                                    modifier = Modifier.onGloballyPositioned {
                                        autoScrollThreshold = (it.size.width * 0.65).toInt()
                                    },
                                    accumulation = maxAccumulation / accumulationLevelAnimate,
                                    processes = processManager.processesStateList,
                                    ganttChartItems = uiState.ganttChartMap,
                                    state = scrollState
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
