package algorithm

import model.Core
import model.ExecuteResult
import java.util.LinkedList
import java.util.Queue
import kotlin.math.roundToInt


class FCFS : SchedulingAlgorithm("FCFS", 1) {

    override fun run() {
        // Put processes into ready queue
        processes.forEach {
            if (it.arrivalTime == time) {
                singleReadyQueue.offer(it)
            }
        }

        cores.forEachIndexed { i, core ->
            if (core.process == null && readyQueue.isNotEmpty())
                core.process = singleReadyQueue.poll()

            val powerConsumption = if (core.process == null) core.idlePowerConsumption else core.powerConsumption

            _totalPowerConsumption[core] = _totalPowerConsumption.getOrDefault(core, 0.0) + powerConsumption

            if(_processRecord[core] == null) _processRecord[core] = mutableListOf()
            _processRecord[core]!!.add(core.process)
        }

        printStatus()

        cores.forEach { core ->
            core.process?.let { process ->
                process.executedTime += core.processingPowerPerSecond

                if (process.executedTime >= process.burstTime) {
                    _endProcesses.add(ExecuteResult(process, (time - process.arrivalTime)))
                    core.process = null
                }
            }
        }

        time++
    }

    override fun printStatus() {
        print("[%3ds]".format(time))
        cores.forEachIndexed { index, core ->
            print(" Core $index[${core.process?.processName ?: "(Empty)"}][${((totalPowerConsumption[core] ?: 0.0) * 10).roundToInt() / 10.0}W]")
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

private operator fun String.times(i: Int): String {
    return with(StringBuilder()) {
        repeat(i) { append(this@times) }
        toString()
    }
}

fun main() {
    with(FCFS()) {
        setCores(
            Core.ECore("E-core 1")
        )
        setProcesses(
            model.Process(
                pid = 1,
                processName = "P1",
                processColor = 0x000000,
                arrivalTime = 0,
                burstTime = 3
            ),
            model.Process(
                pid = 2,
                processName = "P2",
                processColor = 0x000000,
                arrivalTime = 1,
                burstTime = 7
            ),
            model.Process(
                pid = 3,
                processName = "P3",
                processColor = 0x000000,
                arrivalTime = 3,
                burstTime = 2
            ),
            model.Process(
                pid = 4,
                processName = "P4",
                processColor = 0x000000,
                arrivalTime = 5,
                burstTime = 5
            ),
            model.Process(
                pid = 5,
                processName = "P5",
                processColor = 0x000000,
                arrivalTime = 6,
                burstTime = 3
            )
        )
        runAll()
        printStatus()
    }
}