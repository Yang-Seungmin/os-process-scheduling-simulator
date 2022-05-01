package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import model.GanttChartItem
import model.Core
import model.Process
import model.range
import util.toDp
import util.toPx

@Composable
fun GanttChart(
    modifier: Modifier = Modifier,
    accumulation: Dp,
    processes: List<Process>,
    ganttChartItems: Map<Core, List<GanttChartItem>>,
    state: LazyListState
) {
    val scrollAmount = state.firstVisibleItemIndex * accumulation.toPx() + state.firstVisibleItemScrollOffset
    Box(
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        GanttChartArrivalBar(
            accumulation,
            processes,
            state.firstVisibleItemIndex * accumulation.toPx() + state.firstVisibleItemScrollOffset
        )

        LazyColumn(
            modifier = modifier.padding(vertical = 20.dp)
        ) {
            val entries = ganttChartItems.entries.toList()

            items(ganttChartItems.entries.size) {
                GanttChartBar(
                    accumulation,
                    entries[it].key,
                    entries[it].value,
                    scrollAmount
                )
            }
        }

        GanttChartScale(
            accumulation, state)
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
        Box(
            modifier = Modifier
                .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
                .fillMaxHeight()
        ) {
            processes.forEachIndexed { i, _ ->
                if (scrollAmount <= accumulation.toPx() * (processes[i].arrivalTime))
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
                text = "${core.name}",
                color = if (core == null) MaterialTheme.colors.error else MaterialTheme.colors.onBackground,
                maxLines = 1
            )
        }

        Box(
            modifier = Modifier
                .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
                .fillMaxHeight()
        ) {
            ganttChartItems.forEachIndexed { i, ganttChartItem ->
                if (scrollAmount.toDp() <= accumulation * (ganttChartItem.time.last)) {
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