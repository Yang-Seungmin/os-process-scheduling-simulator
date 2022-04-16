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
import androidx.compose.ui.res.painterResource
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
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                Column(
                    modifier = Modifier
                    //.verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(horizontal = 16.dp)
                                .border(width = 1.dp, color = MaterialTheme.colors.primaryVariant)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Algorithm : ",
                                style = MaterialTheme.typography.subtitle1
                            )
                            AlgorithmList(
                                algorithms = algorithms,
                                selectedAlgorithm = selectedAlgorithm.value
                            ) {
                                selectedAlgorithm.value = it
                            }
                            RRQuantumSlider(rrQuantum) {
                                rrQuantum = it
                            }

                            Button(
                                onClick = {
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
                                                        val offset = (accumulationLevel * 4).toInt()
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
                            ) {
                                Text(if (isRunning) "STOP" else "RUN")
                            }

                            Text(
                                text = "Time : ${uiState.time}"
                            )
                        }

                        Column(
                            modifier = Modifier.align(Alignment.CenterEnd).width(IntrinsicSize.Min)
                        ) {
                            Image(
                                modifier = Modifier.padding(horizontal = 8.dp).width(200.dp),
                                painter = painterResource("logo_n.png"),
                                contentDescription = ""
                            )

                            Text(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                text = "1st Team v1.0",
                                textAlign = TextAlign.End
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min)
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
                                processes = processes,
                                onProcessAdd = {
                                    processes.add(it)
                                }, onProcessDelete = {
                                    processes.remove(it)
                                },
                                enabled = !isRunning
                            )
                        }

                        //프로세서
                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        ) {
                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = "Processor",
                                style = MaterialTheme.typography.subtitle1
                            )

                            CoresScreen(
                                cores = cores,
                                onProcessorChange = { i, processor ->
                                    cores[i] = processor
                                    uiState =
                                        uiState.copy(ganttChartMap = cores.filterNotNull().associateWith { listOf() })
                                },
                                totalPowerConsumptions = uiState.totalPowerConsumptions,
                                utilization = utilization.mapValues { it.value.last() },
                                enabled = !isRunning
                            )
                        }
                    }

                    /*with(selectedAlgorithm.value.requireReadyQueuePerCore) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = if (this) "Ready Queue (Per Core)" else "Ready Queue",
                            style = MaterialTheme.typography.subtitle1
                        )

                        if (this) {
                            PerCoreReadyQueue(
                                cores = cores,
                                readyQueues = uiState.readyQueue
                            )
                        } else {
                            SingleReadyQueue(uiState.readyQueue[0])
                        }
                    }*/

                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "Ready Queue",
                        style = MaterialTheme.typography.subtitle1
                    )

                    ReadyQueueList(
                        readyQueues = uiState.readyQueue
                    )

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

                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "Result",
                        style = MaterialTheme.typography.subtitle1
                    )

                    ResultScreen(uiState.executeResult)

                    Box(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
