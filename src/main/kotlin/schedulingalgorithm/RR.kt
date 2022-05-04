package schedulingalgorithm

import model.Core
import model.Process

class RR : SchedulingAlgorithm("RR") {

    var rrQuantum = 2
    private val resideTimes = mutableMapOf<Core, Pair<Process?, Int>>()

    override fun init() {
        super.init()
        resideTimes.clear()
        cores.forEach { core ->
            resideTimes[core] = (null to 0)
        }
    }

    override fun beforeStart() {
        //Do nothing
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
        // If reside time is greater than rr quantum, preempt
        cores.forEach { core ->
            resideTimes[core]?.let {
                resideTimes[core] = core.process to (if (it.first == core.process) it.second + 1 else 1)
            }
            core.process?.let { process ->
                if ((resideTimes[core]?.second ?: 0) >= rrQuantum) {
                    resideTimes[core] = (null to 0)
                    singleReadyQueue.add(process)
                    core.process = null
                }
            }
        }
    }
}