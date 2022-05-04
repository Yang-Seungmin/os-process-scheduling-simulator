package schedulingalgorithm

import model.Process

class SPN : SchedulingAlgorithm(
    algorithmName = "SPN",
    readyQueueSize = 1
) {
    private fun pollShortestProcess(): Process? {
        val process = singleReadyQueue.minByOrNull { it.workload }
        singleReadyQueue.remove(process)
        return process
    }

    override fun beforeStart() {
        // Do nothing
    }

    override fun putProcessIntoReadyQueue(processes: List<Process>) {
        processes.forEach {
            singleReadyQueue.offer(it)
        }
    }

    override fun beforeWork(time: Int) {
        cores.forEachIndexed { i, core ->
            if (core.process == null && readyQueue.isNotEmpty())
                core.process = pollShortestProcess()
        }
    }

    override fun afterWork(time: Int) {
        // Do nothing!!
    }
}