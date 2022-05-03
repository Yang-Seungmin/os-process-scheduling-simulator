package schedulingalgorithm

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import kotlinx.coroutines.*
import model.Core
import model.ExecuteResult
import model.GanttChartItem
import model.Process
import ui.state.CoreState
import ui.state.ReadyQueueState
import ui.state.AlgorithmRunnerState
import java.util.*
import kotlin.collections.ArrayList

class SchedulingAlgorithmRunner(
    val algorithmRunnerState: AlgorithmRunnerState,
    private val coreState: CoreState,
    private val readyQueueState: ReadyQueueState,
    private val totalPowerConsumption: SnapshotStateMap<Core, Double>,
    private val ganttChartRecord: SnapshotStateMap<Core, SnapshotStateList<GanttChartItem>>,
    private val resultTable: SnapshotStateList<ExecuteResult>,
    private val processes: SnapshotStateList<Process>
) {
    val schedulingAlgorithms = listOf(
        FCFS(), RR(), SPN(), SRTN(), HRRN(), CustomAlgorithm()
    )

    val cores get() = coreState.cores.filterNotNull()

    private val utilizationTimeLine = mutableMapOf<Core, MutableList<Double>>()
    private val processRecord = mutableMapOf<Core, MutableList<Process?>>()

    val isEnd: Boolean get() = resultTable.map { it.process }.containsAll(processes)

    private var timerJob: Job? = null
    val isRunning: Boolean get() = timerJob?.isActive ?: false
    var isPaused = false
        private set

    private fun init() {
        totalPowerConsumption.clear()
        ganttChartRecord.clear()
        resultTable.clear()

        utilizationTimeLine.clear()
        processRecord.clear()
        ganttChartRecord.clear()
        coreState.utilizationPerCore.clear()
        coreState.totalPowerConsumptionPerCore.clear()
        cores.forEach {core ->
            utilizationTimeLine[core] = mutableListOf(0.0)
            processRecord[core] = mutableListOf()
            ganttChartRecord[core] = mutableStateListOf()
            coreState.utilizationPerCore[core] = 0.0
            coreState.totalPowerConsumptionPerCore[core] = 0.0
        }

        processes.forEach {
            it.doneWorkload = 0
            it.burstTime = 0
        }
        algorithmRunnerState.schedulingAlgorithm.readyQueue.map { LinkedList(it) }

        algorithmRunnerState.time = 0
        algorithmRunnerState.schedulingAlgorithm.setCores(cores)
        algorithmRunnerState.schedulingAlgorithm.init()
    }

    fun CoroutineScope.run(
        onTimeElapsed: () -> Unit,
        onEnd: () -> Unit,
        interval: Long = 1000
    ) {
        isPaused = false
        timerJob = launch {
            init()
            val beforeProcess: MutableMap<Core, Process?> = mutableMapOf()
            val afterProcess: MutableMap<Core, Process?> = mutableMapOf()

            cores.forEach {
                beforeProcess[it] = null
                afterProcess[it] = null
            }

            with(algorithmRunnerState) {
                while (!isEnd) {
                    if (isPaused) yield()
                    else {
                        /*
                    1. Process의 arrival time이 현재 시간과 일치하면 Scheduling Algorithm에 프로세스 전달
                       Scheduling Algorithm은 전달받은 프로세스를 적절히 Ready Queue에 삽입
                    */
                        schedulingAlgorithm.putProcessIntoReadyQueue(
                            processes.filter { it.arrivalTime == algorithmRunnerState.time }
                        )
                        readyQueueState.readyQueue.value =
                            algorithmRunnerState.schedulingAlgorithm.readyQueue.map { LinkedList(it) }

                        /*
                    2. Work를 수행하기 전(elapsed time 1 증가 전) 해야할 일 수행
                     */
                        schedulingAlgorithm.beforeWork(algorithmRunnerState.time)
                        cores.forEach {
                            beforeProcess[it] = it.process
                        }

                        /*
                    3. Work를 수행 : burst time 증가, done workload의 적절한 증가 소모 전력량 추가
                     */
                        schedulingAlgorithm.onWork()
                        calculatePowerConsumption(schedulingAlgorithm.cores)
                        schedulingAlgorithm.cores.forEach { core ->
                            processRecord[core]?.add(core.process)
                        }

                        // 간트 차트 생성용
                        cores.forEach { core ->
                            if (beforeProcess[core] != null && beforeProcess[core] != afterProcess[core]) {
                                ganttChartRecord[core]?.add(
                                    GanttChartItem(
                                        process = beforeProcess[core]!!,
                                        core = core,
                                        time = IntRange(algorithmRunnerState.time, algorithmRunnerState.time)
                                    )
                                )
                            }
                        }

                        //Work를 수행하면 시간이 1 단위만큼 증가
                        algorithmRunnerState.time++
                        onTimeElapsed()
                        delay(interval)

                        /*
                    4. Work를 수행한 뒤(elapsed time 1 증가 후) 해야할 일 수행
                       완료된 프로세스 처리, 타임라인 계산
                     */
                        val doneProcesses = schedulingAlgorithm.onProcessDone(algorithmRunnerState.time)
                        resultTable.addAll(doneProcesses)
                        schedulingAlgorithm.afterWork(algorithmRunnerState.time)

                        schedulingAlgorithm.cores.forEach { core ->
                            with(processRecord[core] ?: listOf()) {
                                utilizationTimeLine[core]?.add(
                                    mapNotNull { it }.size / size.toDouble()
                                )
                            }
                        }

                        cores.forEach { core ->
                            coreState.utilizationPerCore[core] = utilizationTimeLine[core]?.last() ?: Double.NaN
                        }

                        /* 간트 차트 생성용 */
                        cores.forEach { core ->
                            afterProcess[core] = core.process

                            if (beforeProcess[core] != null && (beforeProcess[core] == afterProcess[core] || afterProcess[core] == null)) {
                                val last = ganttChartRecord[core]?.removeLastOrNull()
                                if (last != null)
                                    ganttChartRecord[core]?.add(
                                        last.copy(time = IntRange(last.time.first, last.time.last + 1))
                                    )
                            }
                        }
                    }
                }
            }
            readyQueueState.readyQueue.value =
                algorithmRunnerState.schedulingAlgorithm.readyQueue.map { LinkedList(it) }

            onEnd()
        }
    }

    fun pause() {
        isPaused = true
    }

    fun restart() {
        isPaused = false
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null
        isPaused = false
    }

    private fun calculatePowerConsumption(cores: Collection<Core>) {
        cores.forEach { core ->
            val powerConsumption = if (core.process == null) core.idlePowerConsumption else core.powerConsumption
            totalPowerConsumption[core] = totalPowerConsumption.getOrDefault(core, 0.0) + powerConsumption
        }
    }
}