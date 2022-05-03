package ui.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import model.Core
import model.GanttChartItem
import kotlin.math.roundToInt

class GanttChartState {
    var ganttChartWidth by mutableStateOf(0.dp)
    var accumulationLevel by mutableStateOf(8f)

    val ganttChartLazyListState = LazyListState()

    val ganttChartMapState = mutableStateMapOf<Core, SnapshotStateList<GanttChartItem>>()

    suspend fun scrollGanttChartToTime(time: Int, accumulation: Dp) {
        val autoScrollThreshold = (ganttChartWidth * 0.65f).value
        val offset = (autoScrollThreshold / accumulation.value).roundToInt()

        ganttChartLazyListState.animateScrollToItem(with(time - offset) { if (this > 0) this else 0 })
    }

    companion object {
        val maxAccumulation = 160.dp
    }
}

@Composable
fun rememberGanttChartState() = remember { GanttChartState() }