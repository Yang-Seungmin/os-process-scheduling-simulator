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
    private val onlyOneRemainingTimeProcessReadyQueue get() = readyQueue[1]

    private fun pollLongestRemainingProcess(queue: Queue<Process>): Process? {
        val process = queue.maxByOrNull { it.remainWorkload }
        queue.remove(process)
        return process
    }

    private fun pollShortestRemainingProcess(queue: Queue<Process>): Process? {
        val process = queue.minByOrNull { it.remainWorkload }
        queue.remove(process)
        return process
    }

    override fun putProcessIntoReadyQueue(processes: List<Process>) {
        processes.forEach { process ->
            if (process.remainWorkload > 1) mainReadyQueue.offer(process)
            else onlyOneRemainingTimeProcessReadyQueue.offer(process)
        }
    }

    override fun beforeWork(time: Int) {
        cores.filterIsInstance<Core.PCore>().forEach { core ->
            if (mainReadyQueue.isNotEmpty()) {
                if (core.process == null) {
                    core.process = pollLongestRemainingProcess(queue = mainReadyQueue)
                }
            }
        }
        cores.filterIsInstance<Core.ECore>().forEach { core ->
            if (onlyOneRemainingTimeProcessReadyQueue.isNotEmpty()) {
                if (core.process == null) {
                    core.process = pollShortestRemainingProcess(queue = onlyOneRemainingTimeProcessReadyQueue)
                } else {
                    if (core.process!!.remainWorkload > 1) {
                        mainReadyQueue.offer(core.process)
                        core.process = onlyOneRemainingTimeProcessReadyQueue.poll()
                    }
                }
            } else if (mainReadyQueue.isNotEmpty()) {
                if (core.process == null) {
                    core.process = pollShortestRemainingProcess(queue = mainReadyQueue)
                }
            }
        }
        cores.filterIsInstance<Core.PCore>().forEach { core ->
            if (onlyOneRemainingTimeProcessReadyQueue.isNotEmpty() && mainReadyQueue.isEmpty()) {
                if (core.process == null) {
                    core.process = pollLongestRemainingProcess(queue = onlyOneRemainingTimeProcessReadyQueue)
                }
            }
        }

    }

    override fun afterWork(time: Int) {
        cores.forEach { core ->
            if (core is Core.PCore) {
                core.process?.let { process ->
                    if (process.remainWorkload == 1) {
                        onlyOneRemainingTimeProcessReadyQueue.offer(process)
                        core.process = null
                    }
                }
            } else if (core is Core.ECore && core.process != null) {
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
    }
}
