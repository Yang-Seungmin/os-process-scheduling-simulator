package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import items.ExecuteResult
import items.GanttChartItem
import items.Process
import items.Processor
import processColors

@Composable
@Preview
fun MainScreen() {
    val processes = rememberSaveable { mutableStateListOf<Process>() }
    val processors = rememberSaveable {
        mutableStateListOf<Processor?>(Processor.PCore, Processor.PCore, Processor.ECore, Processor.ECore)
    }
    var algorithm by remember { mutableStateOf("FCFS") }
    var rrQuantum by remember { mutableStateOf(2) }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp).alignByBaseline(),
                        text = "OS Process Scheduling Simulator",
                        style = MaterialTheme.typography.h4
                    )

                    Text(
                        modifier = Modifier.alignByBaseline().padding(start = 4.dp),
                        text = "1st Team v1.0")

                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp).border(width = 1.dp, color = MaterialTheme.colors.secondary).padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Algorithm : ")
                        AlgorithmList(
                            dropdownItem = algorithm
                        ) {
                            algorithm = it
                        }
                        RRQuantumSlider(rrQuantum) {
                            rrQuantum = it
                        }

                        Button(
                            onClick = {}
                        ) {
                            Text("Run!")
                        }
                    }

                }


                Row(
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    // 프로세스
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "Processes (${processes.size})",
                            style = MaterialTheme.typography.subtitle1
                        )

                        ProcessesScreen(
                            processes = processes,
                            onProcessAdd = {
                                processes.add(it)
                            }, onProcessDelete = {
                                processes.remove(it)
                            }
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

                        ProcessorsScreen(
                            processors = processors,
                            onProcessorChange = { i, processor ->
                                processors[i] = processor
                            }
                        )
                    }
                }

                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Ready Queue",
                    style = MaterialTheme.typography.subtitle1
                )

                ReadyQueue(
                    processors = processors,
                    readyQueues = listOf(
                        listOf(),
                        listOf(
                            /*Process(
                                pid = 1,
                                processName = "adf",
                                arrivalTime = 1,
                                burstTime = 2,
                                processColor = processColors[1]
                            )*/
                        ),
                        listOf(),
                        listOf()
                    )
                )

                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Gantt Chart",
                    style = MaterialTheme.typography.subtitle1
                )

                GanttChart(
                    accumulation = 20.dp,
                    processes = processes,
                    processors = processors,
                    ganttChartItems = listOf(
                        /*GanttChartItem(
                            process = processes[0],
                            coreNumber = 1,
                            time = 2..6
                        )*/
                    )
                )

                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Result",
                    style = MaterialTheme.typography.subtitle1
                )

                ResultScreen(
                    listOf(
                       /* ExecuteResult(
                            processes[0],
                            4,
                            12
                        )*/
                    )
                )
            }
        }
    }
}