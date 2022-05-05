package schedulingalgorithm

import model.Process
import model.remainWorkload

class SRTN : SchedulingAlgorithm(
    algorithmName = "SRTN",
    readyQueueSize = 1
) {

    /**
     * singleReadyQueue에서 가장 작은 workload를 가진 process를 singleReadyQueue에서 꺼내어 반환한다.
     *
     * @return process
     */
    private fun pollShortestRemainingProcess(): Process? {
        val process = singleReadyQueue.minByOrNull { it.remainWorkload }
        singleReadyQueue.remove(process)
        return process
    }

    /**
     * singleReadyQueue에서 가장 작은 workload를 가진 process를 singleReadyQueue에서 찾아서 반환한다.
     *
     * @return process
     */
    private fun peekShortestRemainingProcess(): Process? {
        return singleReadyQueue.minByOrNull { it.remainWorkload }
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
     * singleReadyQueue에 process가 존재할 경우, 빈 core가 없을시 최소 작업량 process 보다 많은 작업량 많은 process를 가진 core를 찾아 선점
     * 빈 core가 있다면 core에 최소 작업량 process을 할당
     * SchedulingAlgorithm class의 abstract func
     *
     */
    override fun beforeWork(time: Int) {
        cores.forEachIndexed { i, core ->
            if (singleReadyQueue.isNotEmpty()) {
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