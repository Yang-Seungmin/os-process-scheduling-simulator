package schedulingalgorithm

import model.Process
import model.remainWorkload

class SRTN : SchedulingAlgorithm(
    algorithmName = "SRTN",
    readyQueueSize = 1
) {

    private fun pollShortestProcess(): Process? {
        val process = singleReadyQueue.minByOrNull { it.remainWorkload }
        singleReadyQueue.remove(process)
        return process
    }

    private fun peekShortestProcess(): Process? {
        val process = singleReadyQueue.minByOrNull { it.remainWorkload }
        return process
    }

    override fun putProcessIntoReadyQueue(processes: List<Process>) {
        processes.forEach {
            singleReadyQueue.offer(it)
        }
    }

    override fun beforeWork(time: Int) {
        cores.forEachIndexed { i, core ->
            if (readyQueue.isNotEmpty()) {
                if (core.process == null) core.process = pollShortestProcess()
                else {
                    if ((peekShortestProcess()?.remainWorkload ?: Int.MAX_VALUE) < core.process!!.remainWorkload) {
                        singleReadyQueue.add(core.process)
                        core.process = pollShortestProcess()
                    }
                }
            }
        }
    }

    override fun afterWork(time: Int) {
        cores.forEach { core ->
            core.process?.let { process ->
                val processRemainingWorkload = process.workload - process.doneWorkload

                val shortestProcessRemainingWorkload =
                    peekShortestProcess()?.let { it.workload - it.doneWorkload } ?: Int.MAX_VALUE

                if (processRemainingWorkload >= shortestProcessRemainingWorkload) {
                    singleReadyQueue.add(process)
                    core.process = null
                }
            }
        }
    }
}