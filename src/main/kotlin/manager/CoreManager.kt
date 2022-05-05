package manager

import androidx.compose.runtime.mutableStateListOf
import model.Core
import ui.state.CoreState
import ui.state.GanttChartState

/**
 * @see Core
 * Core의 상태 변경(코어 수, P-Core, E-Core, OFF)을 수행하는 클래스
 *
 * @property coreState
 * @property ganttChartState
 */
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

    /**
     * Core State List의 index 위치의 코어를 P-Core로 설정한다.
     *
     * @param index core index in core state list
     * @return core instance after changed P-Core
     */
    fun setPCore(index: Int): Core? {
        coreState.cores[index] = Core.PCore("Core $index [P-Core]", index)
        refreshGanttChart()
        return coreState.cores[index]
    }

    /**
     * Core State List의 index 위치의 코어를 E-Core로 설정한다.
     *
     * @param index core index in core state list
     * @return core instance after changed E-Core
     */
    fun setECore(index: Int): Core? {
        coreState.cores[index] = Core.ECore("Core $index [E-Core]", index)
        refreshGanttChart()
        return coreState.cores[index]
    }

    /**
     * Core State List의 index 위치의 코어를 null로 설정한다.
     *
     * @param index core index in core state list
     * @return null
     */
    fun setCoreOff(index: Int): Core? {
        coreState.cores[index] = null
        refreshGanttChart()
        return coreState.cores[index]
    }

    /**
     * Gantt Chart를 새로 그릴 수 있도록 Gantt Chart State를 update한다.
     *
     */
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