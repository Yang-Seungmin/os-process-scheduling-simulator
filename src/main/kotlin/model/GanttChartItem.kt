package model

data class GanttChartItem(
    val process: model.Process,
    val core: Core,
    val time : IntRange
)

fun IntRange.range() : Int {
    return this.last - this.first
}