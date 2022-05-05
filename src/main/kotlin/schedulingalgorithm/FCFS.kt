package schedulingalgorithm

import model.Process

class FCFS : SchedulingAlgorithm(
    algorithmName = "FCFS",
    readyQueueSize = 1
) {
    override fun beforeStart() {
        // Do nothing
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
     * @param processes arrival time과 현재 time이 일치하는 프로세스 리스트
     */
    override fun beforeWork(time: Int) {
        cores.forEachIndexed { i, core ->
            if (core.process == null && singleReadyQueue.isNotEmpty())
                core.process = singleReadyQueue.poll()
        }
    }

    override fun afterWork(time: Int) {
        // Do nothing
    }
}