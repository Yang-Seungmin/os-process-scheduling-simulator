package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import coreColors
import model.GanttChartItem
import model.Core
import model.range
import util.toDp
import util.toPx
import kotlin.math.*

@Composable
fun GanttChart(
    modifier: Modifier = Modifier,
    accumulation: Dp,
    processes: List<model.Process>,
    ganttChartItems: Map<Core, List<GanttChartItem>>,
    powerConsumptions: Map<Core, List<Double>>,
    ratios: Map<Core, List<Double>>,
    state: LazyListState
) {
    val scrollAmount = state.firstVisibleItemIndex * accumulation.toPx() + state.firstVisibleItemScrollOffset
    Column(
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        GanttChartArrivalBar(
            accumulation,
            processes,
            state.firstVisibleItemIndex * accumulation.toPx() + state.firstVisibleItemScrollOffset
        )
        ganttChartItems.entries.forEach { (core, list) ->
            GanttChartBar(
                accumulation,
                core,
                list,
                scrollAmount
            )
        }
        GanttChartGraph("Power consumption graph", "W", accumulation, powerConsumptions.mapKeys { it.key.name }, scrollAmount)
        GanttChartGraph("Utilization", "%", accumulation, ratios.mapKeys { it.key.name }.mapValues { it.value.map { (it * 10000).roundToInt() / 100.0 } }, scrollAmount)

        GanttChartScale(accumulation, state)
    }
}

@Composable
fun ColumnScope.GanttChartGraph(
    graphName: String,
    unit: String,
    accumulation: Dp,
    values: Map<String, List<Double>>,
    scrollAmount: Float
) {
    val onlyValues = values.values.flatten()
    val max = (onlyValues.maxOfOrNull { it } ?: (1 / 1.1)) * 1.1
    Box(
        Modifier.weight(4f)
            .fillMaxWidth()
            .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
    ) {
        Box(
            modifier = Modifier
                .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
                .padding(start = 1.dp)
                .fillMaxHeight()
        ) {
            val colors = coreColors.map { Color(it) }
            var height = 0
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .onGloballyPositioned {
                        height = it.size.height
                    }
            ) {
                values.entries.forEachIndexed { index, (name, items) ->
                    var x1 = 0f
                    var y1 = 0f
                    items.forEachIndexed { time, value ->
                        if (scrollAmount <= accumulation.toPx() * time + 149.dp.toPx()) {
                            val x2 = accumulation.toPx() * (time + 1) - scrollAmount + 149.dp.toPx()
                            val y2 = ((1 - value / max) * height).toFloat()
                            drawCircle(
                                color = colors[index],
                                radius = 1.dp.toPx(),
                                center = Offset(x2, y2)
                            )

                            if (!x1.isNaN() && !y1.isNaN()) {
                                drawLine(
                                    color = colors[index],
                                    start = Offset(x1, y1),
                                    end = Offset(x2, y2),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }

                            x1 = x2
                            y1 = y2
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .height(120.dp)
                .width(150.dp)
                .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = graphName,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.align(Alignment.TopEnd),
                text = "${((max * 10).roundToInt() / 10.0)}$unit",
                maxLines = 1,
                style = MaterialTheme.typography.overline
            )
            Text(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = "0$unit",
                maxLines = 1,
                style = MaterialTheme.typography.overline
            )
        }
    }
}

@Composable
fun ColumnScope.GanttChartArrivalBar(
    accumulation: Dp,
    processes: List<model.Process>,
    scrollAmount: Float
) {
    Row(
        Modifier.weight(1f, true)
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
fun ColumnScope.GanttChartBar(
    accumulation: Dp,
    core: Core,
    ganttChartItems: List<GanttChartItem>,
    scrollAmount: Float
) {
    Row(
        Modifier.weight(1f)
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
fun ColumnScope.GanttChartScale(
    accumulation: Dp,
    state: LazyListState
) {
    val bigScaleDp = (200 / accumulation.value).toInt() * accumulation

    Row(
        modifier = Modifier.weight(1f)
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

fun Number.toDegree(): Double {
    return Math.toDegrees(this.toDouble())
}