package algorithm

import algorithm.queue.SPNQueue
import model.Core
import model.ExecuteResult
import kotlin.math.roundToInt
import kotlin.math.sin


class SPN : SchedulingAlgorithm("SPN") {

    override fun init() {
        super.init()
    }

    private fun pollShortestProcess() : model.Process? {
        val process = singleReadyQueue.minByOrNull { it.workload }
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

fun main() {
    with(SPN()) {
        setCores(
            Core.ECore("E-core 1")
        )
        setProcesses(
            model.Process(
                pid = 1,
                processName = "P1",
                processColor = 0x000000,
                arrivalTime = 0,
                workload = 3
            ),
            model.Process(
                pid = 2,
                processName = "P2",
                processColor = 0x000000,
                arrivalTime = 1,
                workload = 7
            ),
            model.Process(
                pid = 3,
                processName = "P3",
                processColor = 0x000000,
                arrivalTime = 3,
                workload = 2
            ),
            model.Process(
                pid = 4,
                processName = "P4",
                processColor = 0x000000,
                arrivalTime = 5,
                workload = 5
            ),
            model.Process(
                pid = 5,
                processName = "P5",
                processColor = 0x000000,
                arrivalTime = 6,
                workload = 3
            )
        )
        runAll()
        printStatus()
    }
}