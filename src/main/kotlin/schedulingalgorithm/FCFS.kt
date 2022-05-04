package schedulingalgorithm

import model.Process

class FCFS : SchedulingAlgorithm(
    algorithmName = "FCFS",
    readyQueueSize = 1
) {
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
                core.process = singleReadyQueue.poll()
        }
    }

    override fun afterWork(time: Int) {
        // Do nothing
    }
}