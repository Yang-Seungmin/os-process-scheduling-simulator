package schedulingalgorithm

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.Core
import model.ExecuteResult
import model.GanttChartItem
import model.Process
import ui.state.UiState

class SchedulingAlgorithmRunner {
    val schedulingAlgorithms = listOf(
        FCFS(), RR(), SPN(), SRTN(), HRRN()
    )

    var schedulingAlgorithm: SchedulingAlgorithm = schedulingAlgorithms[0]

    private val _cores = mutableListOf<Core>()
    val cores: List<Core> get() = _cores

    private val totalPowerConsumption = mutableMapOf<Core, Double>()
    private val ganttChartRecord = mutableMapOf<Core, MutableList<GanttChartItem>>()
    private val processes = mutableListOf<Process>()
    private val resultTable = mutableListOf<ExecuteResult>()
    private val powerConsumptionTimeLine = mutableMapOf<Core, MutableList<Double>>()
    private val utilizationTimeLine = mutableMapOf<Core, MutableList<Double>>()

    private val processRecord = mutableMapOf<Core, MutableList<Process?>>()

    var time = 0
    val isEnd: Boolean get() = resultTable.map { it.process }.containsAll(processes)

    private var timerJob: Job? = null
    val isRunning: Boolean get() = timerJob?.isActive ?: false

    fun CoroutineScope.run(
        onTimeElapsed: () -> Unit,
        onEnd: () -> Unit,
        interval: Long = 1000
    ) {
        timerJob = launch {
            init()
            val beforeProcess: MutableMap<Core, Process?> = mutableMapOf()
            val afterProcess: MutableMap<Core, Process?> = mutableMapOf()

            cores.forEach {
                beforeProcess[it] = null
                afterProcess[it] = null
            }

            while (!isEnd) {
                /*
                1. Process의 arrival time이 현재 시간과 일치하면 Scheduling Algorithm에 프로세스 전달
                   Scheduling Algorithm은 전달받은 프로세스를 적절히 Ready Queue에 삽입
                */
                schedulingAlgorithm.putProcessIntoReadyQueue(
                    processes.filter { it.arrivalTime == time }
                )

                /*
                2. Work를 수행하기 전(elapsed time 1 증가 전) 해야할 일 수행
                 */
                schedulingAlgorithm.beforeWork(time)
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
                    if (afterProcess[core] == null && beforeProcess[core] != null) {
                        ganttChartRecord[core]?.add(
                            GanttChartItem(
                                process = beforeProcess[core]!!,
                                core = core,
                                time = IntRange(time, time)
                            )
                        )
                    }
                }


                //Work를 수행하면 시간이 1 단위만큼 증가
                time++
                onTimeElapsed()
                delay(interval)

                /*
                4. Work를 수행한 뒤(elapsed time 1 증가 후) 해야할 일 수행
                   완료된 프로세스 처리, 타임라인 계산
                 */
                resultTable.addAll(schedulingAlgorithm.onProcessDone(time))
                schedulingAlgorithm.afterWork(time)

                schedulingAlgorithm.cores.forEach { core ->
                    powerConsumptionTimeLine[core]?.add(totalPowerConsumption[core] ?: 0.0)

                    with(processRecord[core] ?: listOf()) {
                        utilizationTimeLine[core]?.add(
                            mapNotNull { it }.size / size.toDouble()
                        )
                    }
                }

                /* 간트 차트 생성용 */
                cores.forEach { core ->
                    afterProcess[core] = core.process

                    if(beforeProcess[core] != null && (beforeProcess[core] == afterProcess[core] || afterProcess[core] == null)) {
                        val last = ganttChartRecord[core]?.removeLastOrNull()
                        if (last != null)
                            ganttChartRecord[core]?.add(
                                last.copy(time = IntRange(last.time.first, last.time.last + 1))
                            )
                    }
                }
            }

            onEnd()
        }
    }

    private fun init() {
        totalPowerConsumption.clear()
        ganttChartRecord.clear()
        resultTable.clear()
        with(powerConsumptionTimeLine) {
            clear()
            cores.forEach { this[it] = mutableListOf(0.0) }
        }
        with(utilizationTimeLine) {
            clear()
            cores.forEach { this[it] = mutableListOf(0.0) }
        }
        with(processRecord) {
            clear()
            cores.forEach { this[it] = mutableListOf() }
        }
        with(ganttChartRecord) {
            clear()
            cores.forEach { this[it] = mutableListOf() }
        }
        processes.forEach {
            it.doneWorkload = 0
            it.burstTime = 0
        }
        time = 0

        schedulingAlgorithm.setCores(cores)
        schedulingAlgorithm.init()
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun calculatePowerConsumption(cores: Collection<Core>) {
        cores.forEach { core ->
            val powerConsumption = if (core.process == null) core.idlePowerConsumption else core.powerConsumption
            totalPowerConsumption[core] = totalPowerConsumption.getOrDefault(core, 0.0) + powerConsumption
        }
    }

    fun setProcesses(processes: Collection<Process>) {
        this.processes.clear()
        this.processes.addAll(processes)
    }

    fun setProcesses(vararg processes: Process) {
        this.processes.clear()
        this.processes.addAll(processes)
    }

    fun setCores(cores: Collection<Core>) {
        _cores.clear()
        _cores.addAll(cores)
    }

    fun setCores(vararg cores: Core) {
        _cores.clear()
        _cores.addAll(cores)
    }

    fun refreshUiState(uiState: UiState) = uiState.copy(
        totalPowerConsumptions = totalPowerConsumption,
        readyQueue = schedulingAlgorithm.readyQueue,
        executeResult = resultTable,
        time = "${time}s",
        ganttChartMap = ganttChartRecord,
        powerConsumptionTimeLine = powerConsumptionTimeLine,
        utilizationTimeLine = utilizationTimeLine
    )

}