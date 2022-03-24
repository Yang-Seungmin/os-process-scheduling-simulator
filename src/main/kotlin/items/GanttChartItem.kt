package items

import androidx.compose.ui.graphics.Color

data class GanttChartItem(
    val process: Process,
    val coreNumber: Int,
    val time : IntRange
)

fun IntRange.range() : Int {
    return this.last - this.first
}