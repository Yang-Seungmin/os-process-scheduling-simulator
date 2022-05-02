package ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import model.Core
import model.GanttChartItem
import model.Process
import model.range
import ui.state.GanttChartState
import ui.state.ProcessState
import ui.state.AlgorithmRunnerState
import util.toDp
import util.toPx

@Composable
fun GanttChart(
    modifier: Modifier = Modifier,
    ganttChartState: GanttChartState,
    processState: ProcessState,
    algorithmRunnerState: AlgorithmRunnerState
) {
    val lazyListState = ganttChartState.ganttChartLazyListState
    val ganttChartMapState = ganttChartState.ganttChartMapState

    val accumulation = animateDpAsState(GanttChartState.maxAccumulation / ganttChartState.accumulationLevel)
    val accumulationPx = accumulation.value.toPx()
    val scrollAmount = lazyListState.firstVisibleItemIndex * accumulationPx + lazyListState.firstVisibleItemScrollOffset

    val entries = ganttChartMapState.entries.sortedBy { it.key.number }

    LaunchedEffect(algorithmRunnerState.time) {
        ganttChartState.scrollGanttChartToTime(algorithmRunnerState.time, accumulation.value)
    }

    Column {
        Column {
            Row {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Gantt Chart",
                    style = MaterialTheme.typography.subtitle1
                )

                Text(
                    modifier = Modifier.clickable {
                        if (ganttChartState.accumulationLevel > 1)
                            ganttChartState.accumulationLevel /= 2
                    }.padding(8.dp),
                    text = "+",
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary
                )

                Text(
                    modifier = Modifier.clickable {
                        if (ganttChartState.accumulationLevel < 32)
                            ganttChartState.accumulationLevel *= 2
                    }.padding(8.dp),
                    text = "-",
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary
                )
            }

            BoxWithConstraints(
                modifier = modifier.padding(horizontal = 8.dp)
            ) {
                LaunchedEffect(maxWidth) {
                    ganttChartState.ganttChartWidth = maxWidth
                }

                GanttChartArrivalBar(
                    accumulation.value,
                    processState.processes,
                    scrollAmount
                )

                LazyColumn(
                    modifier = modifier.padding(vertical = 20.dp)
                ) {

                    items(entries.size) {
                        GanttChartBar(
                            accumulation.value,
                            entries[it].key,
                            entries[it].value,
                            scrollAmount
                        )
                    }
                }

                GanttChartScale(accumulation.value, lazyListState)
            }
        }
    }
}

@Composable
fun GanttChartArrivalBar(
    accumulation: Dp,
    processes: List<Process>,
    scrollAmount: Float
) {
    Row(
        Modifier.height(20.dp)
            .fillMaxWidth()
            .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
    ) {
        Box(
            modifier = Modifier.fillMaxHeight()
                .width(150.dp)
                .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Arrival Time",
                maxLines = 1
            )
        }
        BoxWithConstraints(
            modifier = Modifier
                .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
                .fillMaxHeight()
        ) {
            var width by remember { mutableStateOf(0.dp) }

            LaunchedEffect(maxWidth) {
                width = maxWidth
            }

            processes.forEachIndexed { i, _ ->
                if (scrollAmount.toDp() <= accumulation * (processes[i].arrivalTime) &&
                    accumulation * (processes[i].arrivalTime) <= scrollAmount.toDp() + width
                )
                    Box(
                        modifier = Modifier.height(20.dp)
                            .padding(start = accumulation * (processes[i].arrivalTime) - scrollAmount.toDp()),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Box(
                            modifier = Modifier.height(12.dp)
                                .background(Color(processes[i].processColor))
                        ) {
                            Text(
                                text = processes[i].processName,
                                maxLines = 1,
                                style = MaterialTheme.typography.caption
                            )
                        }
                        Box(
                            modifier = Modifier.fillMaxHeight().width(1.dp)
                                .background(MaterialTheme.colors.onBackground)
                        )
                    }
            }
        }
    }
}

@Composable
fun GanttChartBar(
    accumulation: Dp,
    core: Core,
    ganttChartItems: List<GanttChartItem>,
    scrollAmount: Float
) {
    Row(
        Modifier.height(20.dp)
            .fillMaxWidth()
            .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
    ) {
        Box(
            modifier = Modifier.fillMaxHeight()
                .width(150.dp)
                .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = core.name,
                color = MaterialTheme.colors.onBackground,
                maxLines = 1
            )
        }

        BoxWithConstraints(
            modifier = Modifier
                .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
                .fillMaxHeight()
        ) {
            var width by remember { mutableStateOf(0.dp) }

            LaunchedEffect(maxWidth) {
                width = maxWidth
            }

            ganttChartItems.forEachIndexed { i, ganttChartItem ->
                if (scrollAmount.toDp() <= accumulation * (ganttChartItem.time.last) &&
                    accumulation * (ganttChartItem.time.first) <= scrollAmount.toDp() + width
                ) {
                    val padding = accumulation * ganttChartItem.time.first

                    Box(
                        modifier = Modifier.height(20.dp)
                            .padding(start = if (scrollAmount.toDp() < padding) padding - scrollAmount.toDp() else 0.dp)
                            .width(accumulation * ganttChartItem.time.range() - if (scrollAmount.toDp() > padding) scrollAmount.toDp() - padding else 0.dp)
                            .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
                            .background(Color(ganttChartItem.process.processColor))
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ganttChartItem.process.processName,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BoxScope.GanttChartScale(
    accumulation: Dp,
    state: LazyListState
) {
    val bigScaleDp = (200 / accumulation.value).toInt() * accumulation

    Row(
        modifier = Modifier.height(20.dp).align(Alignment.BottomStart)
    ) {
        Box(
            modifier = Modifier.width(149.5.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                modifier = Modifier.padding(end = 4.dp),
                text = "(seconds)",
                maxLines = 1,
                style = MaterialTheme.typography.caption,
                fontSize = 10.sp
            )
        }
        LazyRow(
            modifier = Modifier
                .fillMaxHeight(),
            state = state
        ) {
            items(Int.MAX_VALUE) { i ->
                if ((accumulation * i).value.toInt() % bigScaleDp.value.toInt() == 0) {
                    Box(
                        modifier = Modifier.width(accumulation).height(18.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Box(
                            modifier = Modifier.height(18.dp).width(1.dp)
                                .background(MaterialTheme.colors.onBackground)
                        )
                        Text(
                            modifier = Modifier.padding(start = 2.dp),
                            text = i.toString(),
                            maxLines = 1,
                            style = MaterialTheme.typography.caption,
                            fontSize = 8.sp
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.width(accumulation).fillMaxHeight(0.9f),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Box(
                            modifier = Modifier.fillMaxHeight(0.45f).width(1.dp)
                                .background(MaterialTheme.colors.onBackground)
                        )
                    }
                }
            }
        }
    }
}