package ui.state

import model.Core
import model.ExecuteResult
import model.GanttChartItem
import model.Process
import java.util.*

data class UiState(
    val totalPowerConsumptions: Map<Core, Double>,
    val utilization: Map<Core, Double>,
    val readyQueue: List<Queue<Process>>,
    val ganttChartMap: Map<Core, List<GanttChartItem>>,
    val executeResult: List<ExecuteResult>,
    val powerConsumptionTimeLine : Map<Core, MutableList<Double>>,
    val utilizationTimeLine : Map<Core, MutableList<Double>>,
    val time: String
) {
    companion object {
        fun default() = UiState(
            totalPowerConsumptions = mapOf(),
            utilization = mapOf(),
            readyQueue = listOf(
                LinkedList()
            ),
            ganttChartMap = mapOf(
                Core.PCore("Core 0 [P-Core]") to listOf(),
                Core.PCore("Core 1 [P-Core]") to listOf(),
                Core.ECore("Core 2 [E-Core]") to listOf(),
                Core.ECore("Core 3 [E-Core]") to listOf()
            ),
            executeResult = listOf(),
            time = "0s",
            powerConsumptionTimeLine = mapOf(),
            utilizationTimeLine = mapOf()
        )
    }
}