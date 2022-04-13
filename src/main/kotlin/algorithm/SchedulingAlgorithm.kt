package algorithm

import kotlinx.coroutines.*
import model.Core
import model.ExecuteResult
import model.Process
import java.util.*
import kotlin.math.roundToInt

abstract class SchedulingAlgorithm(
    val algorithmName: String
) {
    protected val _readyQueue : MutableList<Queue<Process>> = (1..1).map { LinkedList<Process>() }.toMutableList()
    val readyQueue : List<Queue<Process>> get() = _readyQueue

    protected val singleReadyQueue: Queue<Process> get() = _readyQueue[0]

    private val _cores = mutableListOf<Core>()
    val cores : List<Core> get() = _cores

    protected val _totalPowerConsumption = cores.associateWith { 0.0 }.toMutableMap()
    val totalPowerConsumption : Map<Core, Double> get() = _totalPowerConsumption

    private val _processes = mutableListOf<Process>()
    val processes : List<Process> get() = _processes

    protected val _endProcesses = mutableListOf<ExecuteResult>() // Process : Turnaround Time
    val endProcesses : List<ExecuteResult> get() = _endProcesses

    protected val _processRecord = cores.associateWith { mutableListOf<Process?>() }.toMutableMap()
    val processRecord : Map<Core, List<Process?>> get() = _processRecord

    var time = -1
    protected set

    private var timerJob : Job? = null
    val isRunning : Boolean get() = timerJob?.isActive ?: false

    fun setCores(cores: Collection<Core>) {
        _cores.clear()
        _cores.addAll(cores)
    }

    fun setCores(vararg cores: Core) {
        _cores.clear()
        _cores.addAll(cores)
    }

    fun setProcesses(processes: Collection<Process>) {
        _processes.clear()
        _processes.addAll(processes)
    }

    fun setProcesses(vararg processes: Process) {
        _processes.clear()
        _processes.addAll(processes)
    }

    abstract fun run()

    open fun init() {
        _cores.forEach {
            it.process = null
        }
        _totalPowerConsumption.clear()
        _totalPowerConsumption.putAll(cores.associateWith { 0.0 }.toMutableMap())
        _processes.forEach {
            it.burstTime = 0
            it.doneWorkload = 0
        }
        _processRecord.clear()
        _endProcesses.clear()
        _readyQueue.clear()
        _readyQueue.add(LinkedList())
        time = 0
    }

    fun runAll() {
        init()
        while(!endProcesses.map { it.process }.containsAll(processes)) {
            run()
        }
        println()
    }

    open fun printStatus() {
        print("[%3ds]".format(time))
        cores.forEachIndexed { index, core ->
            print(" Core $index[${core.process?.processName ?: "(Empty)"}][${((totalPowerConsumption[core] ?: 0.0) * 10).roundToInt() / 10.0}W]")
        }
        print(" Turnaround Time: ${endProcesses.joinToString(prefix = "[", postfix = "]") { "${it.process.processName} : ${it.turnaroundTime}" }}")
        println()
    }

    fun runWithTimer(coroutineScope: CoroutineScope, onTimeElapsed: () -> Unit, onEnd: () -> Unit, intervalMilliseconds: Long = 1000) {
        init()
        timerJob = coroutineScope.launch {
            while(!endProcesses.map { it.process }.containsAll(processes)) {
                run()
                onTimeElapsed()
                delay(intervalMilliseconds)
            }
            onEnd()
        }
    }

    fun stop() {
        timerJob?.cancel()
    }
}