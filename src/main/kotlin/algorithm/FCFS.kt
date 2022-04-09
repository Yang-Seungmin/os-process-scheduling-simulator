package algorithm

import items.Core
import java.util.LinkedList
import java.util.Queue
import kotlin.math.roundToInt


class FCFS : SchedulingAlgorithm("FCFS", false) {
    private val cores: List<Core> = listOf(
        Core.ECore()
    )

    private val totalPowerConsumption = cores.map { 0.0 }.toMutableList()

    private val readyQueue : Queue<items.Process> = LinkedList()

    private val processes = listOf(
        items.Process(
            pid = 1,
            processName = "P1",
            processColor = 0x000000,
            arrivalTime = 0,
            burstTime = 3
        ),
        items.Process(
            pid = 2,
            processName = "P2",
            processColor = 0x000000,
            arrivalTime = 1,
            burstTime = 7
        ),
        items.Process(
            pid = 3,
            processName = "P3",
            processColor = 0x000000,
            arrivalTime = 3,
            burstTime = 2
        ),
        items.Process(
            pid = 4,
            processName = "P4",
            processColor = 0x000000,
            arrivalTime = 5,
            burstTime = 5
        ),
        items.Process(
            pid = 5,
            processName = "P5",
            processColor = 0x000000,
            arrivalTime = 6,
            burstTime = 3
        )
    )
    private val endProcesses = mutableListOf<Pair<items.Process, Int>>() // Process : Turnaround Time
    private var time = 0

    fun runAll() {
        while(!endProcesses.map { it.first }.containsAll(processes)) {
            run()
        }
    }

    fun run() {
        // Put processes into ready queue
        processes.forEach {
            if(it.arrivalTime == time) {
                readyQueue.offer(it)
            }
        }

        cores.forEachIndexed { i, core ->
            if(core.process == null && readyQueue.isNotEmpty())
                core.process = readyQueue.poll()

            totalPowerConsumption[i] += if(core.process == null) core.idlePowerConsumption else core.powerConsumption
        }

        time++

        printStatus()

        cores.forEach { core ->
            core.process?.let { process ->
                process.executedTime += core.processingPowerPerSecond

                if(process.executedTime >= process.burstTime) {
                    endProcesses.add(process to (time - process.arrivalTime))
                    core.process = null
                }
            }
        }
    }

    fun printStatus() {
        print("[%3ds]".format(time))
        cores.forEachIndexed { index, processor ->
            print(" Core $index[${processor.process?.processName ?: "(Empty)"}][${(totalPowerConsumption[index] * 10).roundToInt() / 10.0}W]")
        }
        print(" Ready Queue: ${readyQueue.joinToString(prefix = "[", postfix = "]") { it.processName }} ")
        print(" Turnaround Time: ${endProcesses.joinToString(prefix = "[", postfix = "]") { "${it.first.processName} : ${it.second}" }}")
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
        runAll()
        printStatus()
    }
}