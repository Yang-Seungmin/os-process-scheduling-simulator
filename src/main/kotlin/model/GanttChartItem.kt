package model

data class GanttChartItem(
    val process: Process,
    val core: Core,
    val time: IntRange
)

fun IntRange.range(): Int {
    return this.last - this.first
}