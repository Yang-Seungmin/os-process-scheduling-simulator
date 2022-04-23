package algorithm

import model.Core
import model.ExecuteResult
import java.util.LinkedList
import kotlin.math.roundToInt

class SRTN : SchedulingAlgorithm("SRTN") {

    private fun pollShortestProcess(): model.Process? {
        val process = singleReadyQueue.minByOrNull { it.workload - it.doneWorkload }
        singleReadyQueue.remove(process)
        return process
    }

    private fun peekShortestProcess(): model.Process? {
        val process = singleReadyQueue.minByOrNull { it.workload - it.doneWorkload }
        return process
    }

    override fun run() {
        // Put processes into ready queue
        processes.forEach {
            if (it.arrivalTime == time) {
                singleReadyQueue.offer(it)
            }
        }

        cores.forEachIndexed { i, core ->
            if (core.process == null && readyQueue.isNotEmpty())
                core.process = pollShortestProcess()

            // Increase cpu total power consumption
            val powerConsumption = if (core.process == null) core.idlePowerConsumption else core.powerConsumption
            _totalPowerConsumption[core] = _totalPowerConsumption.getOrDefault(core, 0.0) + powerConsumption

            // For gantt chart
            if (_processRecord[core] == null) _processRecord[core] = mutableListOf()
            _processRecord[core]!!.add(core.process)
        }

        printStatus()

        time++

        cores.forEach { core ->
            core.process?.let { process ->
                process.burstTime += 1
                process.doneWorkload += core.processingPowerPerSecond

                if (process.doneWorkload >= process.workload) {
                    _endProcesses.add(ExecuteResult(process, (time - process.arrivalTime)))
                    core.process = null
                } else {
                    val processRemainingWorkload = process.workload - process.doneWorkload
                    val shortestProcessRemainingWorkload =
                        peekShortestProcess()?.let { it.workload - it.doneWorkload } ?: Int.MAX_VALUE

                    if (processRemainingWorkload >= shortestProcessRemainingWorkload) {
                        singleReadyQueue.add(process)
                        core.process = null
                    }
                }
            }
        }
    }

    override fun printStatus() {
        print("[%3ds]".format(time))
        cores.forEachIndexed { index, core ->
            core.process?.let {
                print(" Core $index[${it.processName}, ${it.workload - it.doneWorkload}][${((totalPowerConsumption[core] ?: 0.0) * 10).roundToInt() / 10.0}W]")
            }
        }
        print(" Ready Queue: ${singleReadyQueue.joinToString(prefix = "[", postfix = "]") { it.processName }} ")
        print(
            " Turnaround Time: ${
                endProcesses.joinToString(
                    prefix = "[",
                    postfix = "]"
                ) { "${it.process.processName} : ${it.turnaroundTime}" }
            }"
        )
        println()
    }
}