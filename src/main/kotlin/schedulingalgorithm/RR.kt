package schedulingalgorithm

import model.Core
import model.Process

class RR : SchedulingAlgorithm("RR") {

    var rrQuantum = 2
    private val resideTimes = mutableMapOf<Core, Pair<Process?, Int>>() //각 core의 특정 Process 실행 시간을 위한 member

    override fun beforeStart() {
        resideTimes.clear()
        cores.forEach { core ->
            resideTimes[core] = (null to 0)
        }
    }

    /**
     * processes에서 singleReadyQueue에 process들을 순서대로 삽입한다.
     * SchedulingAlgorithm class의 abstract func
     *
     * @param processes arrival time과 현재 time이 일치하는 프로세스 리스트
     */
    override fun putProcessIntoReadyQueue(processes: List<Process>) {
        processes.forEach {
            singleReadyQueue.offer(it)
        }
    }

    /**
     * singleReadyQueue에 process가 있는 경우, 빈 core에 singleReadyQueue에 맨 앞에 있는 process를 꺼내서 전달
     * SchedulingAlgorithm class의 abstract func
     *
     */
    override fun beforeWork(time: Int) {
        cores.forEach { core ->
            if (core.process == null && singleReadyQueue.isNotEmpty())
                core.process = singleReadyQueue.poll()
        }
    }

    /**
     * 실행 시간이 rrQuantum 이상일시, 해당 core의 resideTime을 초기화 시키고 core에서 작업 중인 process를 singleReadyQueue에 추가한 뒤, core를 null로 바꾼다.
     * resideTime에 core별로 실행한 process와 process별 실행 시간을 증가시킨다.
     * SchedulingAlgorithm class의 abstract func
     *
     */
    override fun afterWork(time: Int) {
        // If reside time is greater than rr quantum, preempt
        cores.forEach { core ->
            resideTimes[core]?.let {
                resideTimes[core] = core.process to
                        (if (it.first == core.process) it.second + 1 else 1)
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