package schedulingalgorithm

import model.Process
import model.remainWorkload

class SRTN : SchedulingAlgorithm(
    algorithmName = "SRTN",
    readyQueueSize = 1
) {

    private fun pollShortestRemainingProcess(): Process? {
        val process = singleReadyQueue.minByOrNull { it.remainWorkload }
        singleReadyQueue.remove(process)
        return process
    }

    private fun peekShortestRemainingProcess(): Process? {
        return singleReadyQueue.minByOrNull { it.remainWorkload }
    }

    override fun putProcessIntoReadyQueue(processes: List<Process>) {
        processes.forEach {
            singleReadyQueue.offer(it)
        }
    }

    override fun beforeWork(time: Int) {
        cores.forEachIndexed { i, core ->
            if (readyQueue.isNotEmpty()) {
                if (core.process == null) core.process = pollShortestRemainingProcess()
                else {
                    if ((peekShortestRemainingProcess()?.remainWorkload
                            ?: Int.MAX_VALUE) < core.process!!.remainWorkload
                    ) {
                        singleReadyQueue.add(core.process)
                        core.process = pollShortestRemainingProcess()
                    }
                }
            }
        }
    }

    override fun afterWork(time: Int) {}
}