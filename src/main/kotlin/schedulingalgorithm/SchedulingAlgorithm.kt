package schedulingalgorithm

import model.Core
import model.ExecuteResult
import model.Process
import java.util.*

abstract class SchedulingAlgorithm(
    val algorithmName: String,
    private val readyQueueSize: Int = 1
) {
    private val _readyQueue = (1..1).map { LinkedList<Process>() }.toMutableList()
    val readyQueue: List<Queue<Process>> get() = _readyQueue

    protected val singleReadyQueue: Queue<Process> get() = _readyQueue[0]

    private val _cores = mutableListOf<Core>()
    val cores: List<Core> get() = _cores

    abstract fun beforeStart()
    abstract fun putProcessIntoReadyQueue(processes: List<Process>)
    abstract fun beforeWork(time: Int)
    abstract fun afterWork(time: Int)
    fun onWork() {
        cores.forEach { core ->
            core.process?.let { process ->
                process.burstTime += 1
                process.doneWorkload += core.processingPowerPerSecond
            }
        }
    }

    fun onProcessDone(time: Int): List<ExecuteResult> {
        val endProcesses = mutableListOf<ExecuteResult>()
        cores.forEach { core ->
            core.process?.let { process ->
                if (process.doneWorkload >= process.workload) {
                    endProcesses.add(ExecuteResult(process, (time - process.arrivalTime)))
                    core.process = null
                }
            }
        }
        return endProcesses
    }

    open fun init() {
        _cores.forEach {
            it.process = null
        }
        _readyQueue.clear()
        repeat(readyQueueSize) { _readyQueue.add(LinkedList()) }
    }

    fun setCores(cores: Collection<Core>) {
        _cores.clear()
        _cores.addAll(cores)
    }
}