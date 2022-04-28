package schedulingalgorithm

import model.Process

class HRRN : SchedulingAlgorithm(
    algorithmName = "HRRN",
    readyQueueSize = 1
) {
    override fun putProcessIntoReadyQueue(processes: List<Process>) {
        processes.forEach {
            singleReadyQueue.offer(it)
        }
    }

    override fun beforeWork(time: Int) {
        cores.forEachIndexed { i, core ->
            if (core.process == null && readyQueue.isNotEmpty())
                core.process = pollHighResponseRatioProcess(time)
        }
    }

    override fun afterWork(time: Int) {
        // Do nothing
    }

    private fun pollHighResponseRatioProcess(time: Int): Process? {
        var process: Process? = null
        singleReadyQueue.forEach {
            if (process == null) {
                process = it
            } else {
                if (((process!!.workload + time - process!!.arrivalTime) / process!!.workload) < ((it.workload + time - it.arrivalTime) / it.workload)) {
                    process = it
                }
            }
        }
        singleReadyQueue.remove(process)
        return process
    }
}