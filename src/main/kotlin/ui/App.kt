package ui

import schedulingalgorithm.*
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import manager.CoreManager
import model.Process
import ui.state.UiState
import util.toPx
import java.io.File
import kotlin.math.roundToInt


@Composable
@Preview
fun MainScreen(
    schedulingAlgorithmRunner: SchedulingAlgorithmRunner,
    coreManager: CoreManager
) {
    val cores = remember { coreManager.cores.toMutableStateList() }
    //For algorithm
    val coroutineScope = rememberCoroutineScope()
    var isRunning by remember { mutableStateOf(false) }
    var interval by remember { mutableStateOf(100L) }
    val selectedAlgorithm = rememberSaveable { mutableStateOf(schedulingAlgorithmRunner.schedulingAlgorithm) }
    var rrQuantum by remember { mutableStateOf(2) }

    //For chart accumulation
    val maxAccumulation = 160.dp
    var accumulationLevel by remember { mutableStateOf(8f) }
    val accumulationLevelAnimate by animateFloatAsState(accumulationLevel)

    //For scroll
    var autoScrollThreshold by remember { mutableStateOf(0) }
    val scrollState = rememberLazyListState()
    var offset by remember { mutableStateOf(0) }
    offset = (autoScrollThreshold / (maxAccumulation / accumulationLevel).toPx()).roundToInt()

    //For file
    var isFileOpenerOpened by remember { mutableStateOf(false) }
    var isFileSaverOpened by remember { mutableStateOf(false) }

    //For process
    val processScrollState = rememberLazyListState()
    val processes = rememberSaveable { mutableStateListOf<Process>() }

    var uiState by remember { mutableStateOf(UiState.default()) }

    val runButtonClicked: () -> Unit = {
        if (isRunning) {
            schedulingAlgorithmRunner.stop()
            uiState = uiState.copy(time = "0s")
            isRunning = schedulingAlgorithmRunner.isRunning
        } else {
            isRunning = true
            with(schedulingAlgorithmRunner) {
                if (schedulingAlgorithm is RR) (schedulingAlgorithm as RR).rrQuantum = rrQuantum

                setCores(coreManager.cores.filterNotNull())
                setProcesses(processes)

                coroutineScope.run(
                    onTimeElapsed = {

                        uiState = refreshUiState(uiState)

                        coroutineScope.launch {
                            scrollState.animateScrollToItem(with(time - offset) { if (this > 0) this else 0 })
                        }
                    },
                    onEnd = {
                        uiState = uiState.copy(
                            time = "${time}s (END)"
                        )
                        isRunning = false
                    }, interval
                )
            }
        }
    }

    MaterialTheme(
        colors = Colors,
        typography = Typography
    ) {
        if (isFileOpenerOpened) {
            ProcessesJsonFileLoadDialog { dir, file ->
                isFileOpenerOpened = false

                if (file != null) {
                    processes.clear()
                    processes.addAll(Json.decodeFromStream<List<Process>>(File(dir, file).inputStream()))
                    processColorCount = processes.size
                }
            }
        }
        if (isFileSaverOpened) {
            ProcessesJsonFileSaveDialog { dir, file ->
                isFileSaverOpened = false

                if (file != null) {
                    Json.encodeToStream(processes.toList(), File(dir, file).outputStream())
                }
            }
        }

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
                                            selectedAlgorithm = selectedAlgorithm.value
                                        ) {
                                            schedulingAlgorithmRunner.schedulingAlgorithm = it
                                            selectedAlgorithm.value = it
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.padding(top = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RRQuantumSlider(rrQuantum) {
                                            rrQuantum = it
                                        }
                                    }
                                }

                                Button(
                                    modifier = Modifier.padding(start = 12.dp).padding(vertical = 2.dp).width(120.dp)
                                        .fillMaxHeight(),
                                    onClick = runButtonClicked
                                ) {
                                    Text(
                                        text = if (isRunning) "STOP" else "RUN!!",
                                        style = MaterialTheme.typography.h6
                                    )
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
                                            text = "Processes (${processes.size})",
                                            style = MaterialTheme.typography.subtitle1
                                        )

                                        Box(modifier = Modifier.padding(8.dp))

                                        Text(
                                            modifier = Modifier
                                                .clickable {
                                                    isFileOpenerOpened = true
                                                }
                                                .padding(8.dp),
                                            text = "Import from..",
                                            color = MaterialTheme.colors.primary
                                        )

                                        Text(
                                            modifier = Modifier
                                                .clickable {
                                                    isFileSaverOpened = true
                                                }
                                                .padding(8.dp),
                                            text = "Export to..",
                                            color = MaterialTheme.colors.primary
                                        )
                                    }

                                    ProcessesScreen(
                                        modifier = Modifier.fillMaxHeight(),
                                        processes = processes,
                                        onProcessAdd = {
                                            processes.add(it)
                                            coroutineScope.launch {
                                                processScrollState.animateScrollToItem(processes.size)
                                            }
                                        }, onProcessDelete = {
                                            processes.remove(it)
                                        },
                                        enabled = !isRunning,
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
                                        text = "Processor",
                                        style = MaterialTheme.typography.subtitle1
                                    )

                                    Text(
                                        modifier = Modifier.clickable {
                                            if(!isRunning)
                                            coreManager.addCore()
                                        }.padding(8.dp),
                                        text = "+",
                                        style = MaterialTheme.typography.subtitle1,
                                        color = MaterialTheme.colors.primary
                                    )

                                    Text(
                                        modifier = Modifier.clickable {
                                            if(!isRunning)
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
                                    utilization = uiState.utilizationTimeLine.mapValues { it.value.lastOrNull() ?: 0.0 },
                                    enabled = !isRunning,
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
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = "Result (${uiState.executeResult.size})",
                                    style = MaterialTheme.typography.subtitle1
                                )

                                ResultScreen(
                                    modifier = Modifier.fillMaxHeight(),
                                    uiState.executeResult
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
                                    processes = processes,
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
