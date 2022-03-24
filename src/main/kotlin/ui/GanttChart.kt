package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import items.GanttChartItem
import items.Processor
import items.range
import kotlinx.coroutines.launch
import util.toDp
import util.toPx

@Composable
fun GanttChart(
    accumulation: Dp,
    processors: List<Processor?>,
    processes: List<items.Process>,
    ganttChartItems: List<GanttChartItem>
) {
    val state = rememberLazyListState()

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        GanttChartArrivalBar(
            accumulation,
            processes,
            state.firstVisibleItemIndex * accumulation.toPx() + state.firstVisibleItemScrollOffset
        )
        processors.forEachIndexed { i, processor ->
            GanttChartBar(
                accumulation,
                processor,
                i,
                ganttChartItems,
                state.firstVisibleItemIndex * accumulation.toPx() + state.firstVisibleItemScrollOffset
            )
        }
        GanttChartScale(accumulation, state)
    }
}

@Composable
fun GanttChartArrivalBar(
    accumulation: Dp,
    processes: List<items.Process>,
    scrollAmount: Float
) {
    Row(
        Modifier.height(24.dp)
            .fillMaxWidth()
            .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
    ) {
        Box(
            modifier = Modifier.height(24.dp)
                .width(150.dp)
                .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
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
                .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
                .height(24.dp)
        ) {
            processes.forEachIndexed { i, _ ->
                if (scrollAmount < accumulation.toPx() * (processes[i].arrivalTime))
                    Box(
                        modifier = Modifier.height(24.dp)
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
    processor: Processor?,
    coreNumber: Int,
    ganttChartItems: List<GanttChartItem>,
    scrollAmount: Float
) {
    Row(
        Modifier.height(24.dp)
            .fillMaxWidth()
            .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
    ) {
        Box(
            modifier = Modifier.height(24.dp)
                .width(150.dp)
                .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Core $coreNumber [${processor?.name ?: "OFF"}]",
                color = if (processor == null) MaterialTheme.colors.error else MaterialTheme.colors.onBackground,
                maxLines = 1
            )
        }

        Box(
            modifier = Modifier
                .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
                .height(24.dp)
        ) {
            ganttChartItems.forEachIndexed { i, ganttChartItem ->
                if (ganttChartItem.coreNumber == coreNumber) {
                    if(scrollAmount.toDp() < accumulation * (ganttChartItem.time.last)) {
                        val padding = accumulation * ganttChartItem.time.first

                        Box(
                            modifier = Modifier.height(24.dp)
                                .padding(start = if(scrollAmount.toDp() < padding) padding - scrollAmount.toDp() else 0.dp)
                                .width(accumulation * ganttChartItem.time.range() - if(scrollAmount.toDp() > padding) scrollAmount.toDp() - padding else 0.dp)
                                .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
                                .background(Color(ganttChartItem.process.processColor))
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ganttChartItem.process.processName,
                                maxLines = 1,
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun GanttChartScale(
    accumulation: Dp,
    state: LazyListState
) {
    val bigScaleDp = (200 / accumulation.value).toInt() * accumulation

    Row {
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
                .height(24.dp),
            state = state
        ) {
            items(accumulation.value.toInt() * 20) { i ->
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
                        modifier = Modifier.width(accumulation).height(18.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Box(
                            modifier = Modifier.height(9.dp).width(1.dp)
                                .background(MaterialTheme.colors.onBackground)
                        )
                    }
                }
            }
        }
    }
}