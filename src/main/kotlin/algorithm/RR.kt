package algorithm

import model.Core
import model.ExecuteResult
import java.util.LinkedList
import kotlin.math.roundToInt

class RR : SchedulingAlgorithm("RR") {

    var rrQuantum = 2
    val resideTimes = mutableMapOf<Core, Pair<model.Process?, Int>>()

    override fun init() {
        super.init()
        resideTimes.clear()
        cores.forEach { core ->
            resideTimes[core] = (null to 0)
        }
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
                core.process = singleReadyQueue.poll()

            // Increase cpu total power consumption
            val powerConsumption = if (core.process == null) core.idlePowerConsumption else core.powerConsumption
            _totalPowerConsumption[core] = _totalPowerConsumption.getOrDefault(core, 0.0) + powerConsumption

            resideTimes[core]?.let {
                resideTimes[core] = core.process to ( if(it.first == core.process) it.second + 1 else 1 )
            }

            // For gantt chart
            if(_processRecord[core] == null) _processRecord[core] = mutableListOf()
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
                    // If reside time is greater than rr quantum, preempt
                    if((resideTimes[core]?.second ?: 0) >= rrQuantum) {
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
        print(
            " Reside time: ${
                resideTimes.values.joinToString(
                    prefix = "[",
                    postfix = "]"
                ) { (process, resideTime) -> "${process?.processName ?: "Empty"} : $resideTime" }
            }"
        )
        println()
    }
}

/*
    fork_mutexes : Semaphore array <- [1, 1, 1, 1, 1]

    (Pi) P0 P1 P2 P3 P4
    ...
        P(fork_mutexes[i])
        P(fork_mutexes[(i + 1) % 5])

    /* CS start */
    forks[i] = 0
    forks[(i + 1) % 5] = 0

    //EAT

    forks[i] = 1
    forks[(i + 1) % 5] = 1

    /* CS END */

    V(fork_mutexes[i])
    V(fork_mutexes[(i + 1) % 5])
 */