package schedulingalgorithm

import model.Process

class SPN : SchedulingAlgorithm(
    algorithmName = "SPN",
    readyQueueSize = 1
) {


    /**
     * singleReadyQueue에서 가장 작은 workload를 가진 process를 singleReadyQueue에서 꺼내어 반환한다.
     *
     * @return process
     */
    private fun pollShortestProcess(): Process? {
        val process = singleReadyQueue.minByOrNull { it.workload }
        singleReadyQueue.remove(process)
        return process
    }

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
     * singleReadyQueue에 process가 있는 경우, 빈 core에 singleReadyQueue에서 가장 짧은 process를 꺼내서 전달
     * SchedulingAlgorithm class의 abstract func
     *
     */
    override fun beforeWork(time: Int) {
        cores.forEach { core ->
            if (core.process == null && singleReadyQueue.isNotEmpty())
                core.process = pollShortestProcess()
        }
    }

    override fun afterWork(time: Int) {
        // Do nothing!!
    }
}