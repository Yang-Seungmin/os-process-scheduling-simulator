package schedulingalgorithm

import model.Core
import model.Process
import model.remainWorkload
import java.util.*

class CustomAlgorithm : SchedulingAlgorithm(
    algorithmName = "Custom",
    readyQueueSize = 2
) {
    private val mainReadyQueue get() = readyQueue[0]
    private val onlyOneRemainingWorkloadReadyQueue get() = readyQueue[1]

    private var isEcoreInProcessor = false

    /**
     * queue에서 남은 workload가 가장 큰 프로세스를 꺼내는 함수
     *
     * @param queue 프로세스를 꺼내올 큐
     */
    private fun pollLongestRemainingProcess(queue: Queue<Process>): Process? {
        val process = queue.maxByOrNull { it.remainWorkload }
        queue.remove(process)
        return process
    }

    /**
     * queue에서 남은 wokrload가 가장 작은 프로세스를 꺼내는 함수
     *
     * @param queue 프로세스를 꺼내올 queue
     */
    private fun pollShortestRemainingProcess(queue: Queue<Process>): Process? {
        val process = queue.minByOrNull { it.remainWorkload }
        queue.remove(process)
        return process
    }

    /**
     * P코어만 있는 경우 P코어에서 남은 workload가 1인 프로세스를 선점이 일어나지 않도록 처리하기 위해
     * 시스템에 E코어가 존재하는지를 확인하도록 구현.
     */
    override fun beforeStart() {
        isEcoreInProcessor = cores.find { it is Core.ECore } != null
    }

    /**
     * 프로세스의 남은 workload 즉, remainWorkload가 1이라면 이를 E코어에서 우선적으로 처리하도록 onlyOneRemainingTimeProcessReadyQueue에 넣고
     * 1보다 크다면 P코어에서 처리할 수 있도록 mainReadyQueue에 넣도록 구현.
     *
     * @param processes arrival time과 현재 time이 일치하는 프로세스 리스트
     */
    override fun putProcessIntoReadyQueue(processes: List<Process>) {
        processes.forEach { process ->
            if (process.remainWorkload > 1) mainReadyQueue.offer(process)
            else onlyOneRemainingWorkloadReadyQueue.offer(process)
        }
    }

    /**
     * E코어에서 작업중인 프로세스의 remainWorkload가 1보다 크고 작업하고 있지 않은 P코어의 수가 mainReadyQueue의 프로세스 수보다 많을 때
     * P코어에서 작업할 수 있도록 E코어에서 선점을 일으켜 프로세스를 mainReadyQueue에 넣음.
     * P코어가 우선적으로 mainReadyQueue에서 remainWorkload가 가장 큰 프로세스를 꺼내와 작업.
     * E코어는 onlyOneRemainingTimeProcessReadyQueue에서 프로세스를 꺼내와 작업, 이미 작업중인 프로세스가 있다면 선점.
     * onlyOneRemainingTimeProcessReadyQueue가 비어있다면 mainReadyQueue에서 프로세스를 꺼내서 onlyOneRemainingTimeProcessReadyQueue에 프로세스가 들어올 때까지 작업.
     * P코어가 mainReadyQueue에서 프로세스를 꺼낼 때 mainReadyQueue가 비어있다면 onlyOneRemainingTimeProcessReadyQueue에서 프로세스를 꺼내 작업을 하도록 구현.
     *
     * @param time 현재 시간 단위
     */
    override fun beforeWork(time: Int) {
        cores.forEach { core ->
            if (core is Core.ECore && core.process != null) {
                if (core.process!!.remainWorkload > 1) {
                    if(cores.count { findCore ->
                            findCore is Core.PCore && findCore.process == null
                        } > mainReadyQueue.size) {
                        mainReadyQueue.offer(core.process)
                        core.process = null
                    }
                }
            }
        }
        cores.filterIsInstance<Core.PCore>().forEach { core ->
            if (mainReadyQueue.isNotEmpty()) {
                if (core.process == null) {
                    core.process = pollLongestRemainingProcess(queue = mainReadyQueue)
                }
            }
        }
        cores.filterIsInstance<Core.ECore>().forEach { core ->
            if (onlyOneRemainingWorkloadReadyQueue.isNotEmpty()) {
                if (core.process == null) {
                    core.process = pollShortestRemainingProcess(queue = onlyOneRemainingWorkloadReadyQueue)
                } else {
                    if (core.process!!.remainWorkload > 1) {
                        mainReadyQueue.offer(core.process)
                        core.process = onlyOneRemainingWorkloadReadyQueue.poll()
                    }
                }
            } else if (mainReadyQueue.isNotEmpty()) {
                if (core.process == null) {
                    core.process = pollShortestRemainingProcess(queue = mainReadyQueue)
                }
            }
        }
        cores.filterIsInstance<Core.PCore>().forEach { core ->
            if (onlyOneRemainingWorkloadReadyQueue.isNotEmpty() && mainReadyQueue.isEmpty()) {
                if (core.process == null) {
                    core.process = pollLongestRemainingProcess(queue = onlyOneRemainingWorkloadReadyQueue)
                }
            }
        }

    }

    /**
     * P코어에서 작업 중인 process의 remainWorkload가 1이되면 선점하여 해당 프로세스를
     * onlyOneRemainingTimeProcessReadyQueue에 넣음. (해당 선점은 E코어가 있을 경우에만 일어남.)
     *
     * @param time 현재 시간 단위
     */
    override fun afterWork(time: Int) {
        cores.forEach { core ->
            if (core is Core.PCore) {
                core.process?.let { process ->
                    if (isEcoreInProcessor && process.remainWorkload == 1) {
                        onlyOneRemainingWorkloadReadyQueue.offer(process)
                        core.process = null
                    }
                }
            }
        }
    }
}
