package manager

import androidx.compose.runtime.mutableStateListOf
import model.Core
import ui.state.CoreState
import ui.state.GanttChartState

class CoreManager(
    val coreState: CoreState,
    val ganttChartState: GanttChartState
) {
    fun addCore() {
        coreState.cores.add(Core.PCore("Core ${coreState.cores.size} [P-Core]", coreState.cores.size))
        refreshGanttChart()
    }

    fun removeCore() {
        coreState.cores.removeLast()
        refreshGanttChart()
    }

    fun setPCore(index: Int): Core? {
        coreState.cores[index] = Core.PCore("Core $index [P-Core]", index)
        refreshGanttChart()
        return coreState.cores[index]
    }

    fun setECore(index: Int): Core? {
        coreState.cores[index] = Core.ECore("Core $index [E-Core]", index)
        refreshGanttChart()
        return coreState.cores[index]
    }

    fun setCoreOff(index: Int): Core? {
        coreState.cores[index] = null
        refreshGanttChart()
        return coreState.cores[index]
    }

    fun refreshGanttChart() {
        with(ganttChartState.ganttChartMapState) {
            clear()
            coreState.cores.filterNotNull().forEach {
                put(it, mutableStateListOf())
            }
        }
    }

    companion object {
        val coreTypes = listOf("OFF", "P-Core", "E-Core")
    }
}