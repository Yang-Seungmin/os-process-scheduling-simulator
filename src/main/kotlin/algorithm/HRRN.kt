package algorithm

import model.ExecuteResult
import kotlin.math.roundToInt

class HRRN : SchedulingAlgorithm("HRRN") {
    override fun init() {
        super.init()
    }

    private fun pollShortestProcess() : model.Process? {
        var process : model.Process? = null
        singleReadyQueue.forEach {
            if (process == null) {
                process = it
            } else {
                if (((process!!.workload + time - process!!.arrivalTime) / process!!.workload) < ((it.workload + time - it.arrivalTime) / it.workload)) {
                    process = it
                }
            }
        }
        singleReadyQueue.remove(process)
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

            val powerConsumption = if (core.process == null) core.idlePowerConsumption else core.powerConsumption

            _totalPowerConsumption[core] = _totalPowerConsumption.getOrDefault(core, 0.0) + powerConsumption

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
                }
            }
        }
    }

    override fun printStatus() {
        print("[%3ds]".format(time))
        cores.forEachIndexed { index, core ->
            print(" Core $index[${core.process?.processName ?: "(Empty)"}][${((totalPowerConsumption[core] ?: 0.0) * 10).roundToInt() / 10.0}W]")
        }
        print(
            " Ready Queue: ${
                readyQueue.joinToString(prefix = "{", postfix = "}") {
                    it.joinToString(
                        prefix = "[",
                        postfix = "]"
                    ) { it.processName }
                }
            } "
        )
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