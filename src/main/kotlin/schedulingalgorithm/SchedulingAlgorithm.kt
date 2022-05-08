package schedulingalgorithm

import model.Core
import model.ExecuteResult
import model.Process
import java.util.*

/**
 * 스케줄링 알고리즘, 어떤 스케줄링 알고리즘을 적용하고 싶으면 이 클래스를 상속하여 구현한다.
 *
 * @property algorithmName 알고리즘의 이름
 * @property readyQueueSize 레디큐 개수, 기본 1
 */
abstract class SchedulingAlgorithm(
    val algorithmName: String,
    private val readyQueueSize: Int = 1
) {

    private val _readyQueue = (1..1).map { LinkedList<Process>() }.toMutableList()

    /**
     * 스케줄링 알고리즘에서 사용할 레디 큐
     */
    val readyQueue: List<Queue<Process>> get() = _readyQueue

    /**
     * 스케줄링 알고리즘을 구현할 때 레디 큐 개수가 하나일 경우 사용하기 용이하다.
     */
    protected val singleReadyQueue: Queue<Process> get() = _readyQueue[0]

    private val _cores = mutableListOf<Core>()

    /**
     * 스케줄링 알고리즘을 동작시킬 때 사용힐 코어
     */
    val cores: List<Core> get() = _cores

    /**
     * 프로세스 스케줄링을 시작하기 전에 호출되는 함수, 스케줄링 알고리즘 구현 시 이를 구현하여 초기화 작업 등을 진행
     *
     */
    abstract fun beforeStart()

    /**
     * 매 초(time 증가 전) 이 함수를 호출하며 Arrival Time과 현재 시간이 일치하는 프로세스가 있을 경우 processes에 그 프로세스가 들어 있다.
     * 이 함수를 구현할 때는 알고리즘의 특성에 맞게 적절히 주어진 프로세스를 레디 큐에 넣는 작업을 수행할 수 있도록 한다.
     *
     * @param processes arrival time과 현재 time이 일치하는 프로세스 리스트
     */
    abstract fun putProcessIntoReadyQueue(processes: List<Process>)

    /**
     * 매 초(time 증가 전) 이 함수를 호출한다.
     * 이 함수를 구현할 때는 알고리즘의 특성에 맞게 레디 큐에서 적절한 조건에 맞춰 프로세스를 꺼내 프로세서에 추가하는 작업 등을 수행할 수 있도록 한다.
     *
     * @param time 현재 시간 단위
     */
    abstract fun beforeWork(time: Int)

    /**
     * 매 초(time 증가 후) 이 함수를 호출한다.
     * 이 함수를 구현할 때는 알고리즘의 특성에 맞게 프로세서가 점유하고 있는 프로세스를 선점(Preemption) 하는 작업 등을 수행할 수 있도록 한다.
     *
     * @param time 현재 시간 단위
     */
    abstract fun afterWork(time: Int)

    /**
     * beforeWork function과 time 증가 코드 실행 사이에서 코어 내에 있는 프로세스의 burstTime과 doneWorkload를 계산하여 적용한다.
     *
     */
    fun onWork() {
        cores.forEach { core ->
            core.process?.let { process ->
                process.burstTime += 1
                process.doneWorkload += core.processingPowerPerSecond
            }
        }
    }

    /**
     * 어떤 프로세스의 workload를 모두 마쳤을 때 ExecuteResult를 만들어 추가하는 과정을 수행한다.
     *
     * @param time 현재 시간 단위
     * @return ExecuteResult List
     */
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

    /**
     * 스케줄링 알고리즘을 수행하기 전 초기화 과정
     *
     */
    fun init() {
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