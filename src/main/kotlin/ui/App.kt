package ui

import algorithm.FCFS
import algorithm.RR
import algorithm.SPN
import algorithm.SchedulingAlgorithm
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import model.Process
import model.Core
import util.toGanttChart
import java.io.File


@Composable
@Preview
fun MainScreen() {
    val maxAccumulation = 160.dp
    var isFileOpenerOpened by remember { mutableStateOf(false) }
    var isFileSaverOpened by remember { mutableStateOf(false) }
    var isRunning by remember { mutableStateOf(false) }
    var interval by remember { mutableStateOf(100L) }
    var accumulationLevel by remember { mutableStateOf(8f) }
    val accumulationLevelAnimate by animateFloatAsState(accumulationLevel)
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    val processes = rememberSaveable { mutableStateListOf<Process>() }
    val cores = rememberSaveable {
        mutableStateListOf<Core?>(
            Core.PCore("Core 0 [P-Core]"),
            Core.PCore("Core 1 [P-Core]"),
            Core.ECore("Core 2 [E-Core]"),
            Core.ECore("Core 3 [E-Core]")
        )
    }
    val powerConsumptions = rememberSaveable {
        mutableStateMapOf<Core, MutableList<Double>>()
    }
    val utilization = rememberSaveable {
        mutableStateMapOf<Core, MutableList<Double>>()
    }

    var uiState by remember { mutableStateOf(UiState.default()) }

    val algorithms = listOf<SchedulingAlgorithm>(
        FCFS(), RR(), SPN()
    )
    val selectedAlgorithm = rememberSaveable {
        mutableStateOf<SchedulingAlgorithm>(algorithms[0])
    }
    var rrQuantum by remember { mutableStateOf(2) }

    val runButtonClicked = {
        if (isRunning) {
            selectedAlgorithm.value.stop()
            uiState = uiState.copy(time = "0s")
            isRunning = selectedAlgorithm.value.isRunning
        } else {
            isRunning = true
            with(selectedAlgorithm.value) {
                setCores(cores.filterNotNull())
                setProcesses(processes)
                powerConsumptions.clear()
                cores.forEach { if (it != null) powerConsumptions[it] = mutableListOf() }
                utilization.clear()
                cores.forEach { if (it != null) utilization[it] = mutableListOf() }

                runWithTimer(
                    coroutineScope,
                    onTimeElapsed = {
                        totalPowerConsumption.forEach { t, u -> powerConsumptions[t]?.add(u) }
                        cores.forEach {
                            if (it != null) {
                                with(processRecord[it]!!) {
                                    utilization[it]?.add(mapNotNull { it }.size / size.toDouble())
                                }
                            }
                        }
                        uiState = uiState.copy(
                            totalPowerConsumptions = totalPowerConsumption,
                            readyQueue = readyQueue,
                            executeResult = endProcesses,
                            time = "${time}s",
                            ganttChartMap = processRecord.toGanttChart()
                        )
                        coroutineScope.launch {
                            val offset = (accumulationLevel * 2.5).toInt()
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
                                        text = "1st Team v1.0"
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
                                            algorithms = algorithms,
                                            selectedAlgorithm = selectedAlgorithm.value
                                        ) {
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

                                Text(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    text = "Time : ${uiState.time}",
                                    style = MaterialTheme.typography.subtitle1
                                )
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
                                        }, onProcessDelete = {
                                            processes.remove(it)
                                        },
                                        enabled = !isRunning
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.weight(4f)
                            ) {
                                //프로세서
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = "Processor",
                                    style = MaterialTheme.typography.subtitle1
                                )

                                CoresScreen(
                                    modifier = Modifier.weight(1f),
                                    cores = cores,
                                    onProcessorChange = { i, processor ->
                                        cores[i] = processor
                                        uiState =
                                            uiState.copy(
                                                ganttChartMap = cores.filterNotNull().associateWith { listOf() })
                                    },
                                    totalPowerConsumptions = uiState.totalPowerConsumptions,
                                    utilization = utilization.mapValues { it.value.lastOrNull() ?: 0.0 },
                                    enabled = !isRunning
                                )

                                //레디큐
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = "Ready Queue",
                                    style = MaterialTheme.typography.subtitle1
                                )

                                ReadyQueueList(
                                    readyQueues = uiState.readyQueue
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
                                    text = "Result",
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
                                    accumulation = maxAccumulation / accumulationLevelAnimate,
                                    processes = processes,
                                    ganttChartItems = uiState.ganttChartMap,
                                    state = scrollState,
                                    powerConsumptions = powerConsumptions,
                                    ratios = utilization
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
