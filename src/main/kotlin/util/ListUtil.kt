package util

import model.Core
import model.GanttChartItem

fun Map<Core, List<model.Process?>>.toGanttChart(): Map<Core, List<GanttChartItem>> {

    val resultMap = mutableMapOf<Core, MutableList<GanttChartItem>>()

    this.entries.forEach { (core, list) ->
        var time = 0

        resultMap[core] = mutableListOf()
        list.forEach { process ->
            if (process != null) {
                if (resultMap[core]?.lastOrNull()?.process != process) {
                    resultMap[core]?.add(
                        GanttChartItem(
                            process = process,
                            core = core,
                            time = IntRange(time, time + 1)
                        )
                    )
                } else {
                    val start = resultMap[core]?.lastOrNull()?.time?.first ?: time

                    resultMap[core]?.let { list ->
                        if (list.isNotEmpty())
                            resultMap[core]?.add(list.removeLast().copy(time = IntRange(start, time + 1)))
                    }
                }
            }

            time++
        }

    }

    return resultMap
}