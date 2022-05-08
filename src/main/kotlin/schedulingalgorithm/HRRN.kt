package schedulingalgorithm

import model.Process
import model.getResponseRatio

class HRRN : SchedulingAlgorithm(
    algorithmName = "HRRN",
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
     * singleReadyQueue에서 가장 높은 Response Ratio를 가진 process 꺼내서 반환한다.
     *
     * @return process
     */
    private fun pollHighResponseRatioProcess(time: Int): Process? {
        var highResponseRatioProcess: Process? = null
        singleReadyQueue.forEach {process ->
            if (highResponseRatioProcess == null) {
                highResponseRatioProcess = process
            } else {
                if (highResponseRatioProcess!!.getResponseRatio(time) < process.getResponseRatio(time)) {
                    highResponseRatioProcess = process
                }
            }
        }
        singleReadyQueue.remove(highResponseRatioProcess)
        return highResponseRatioProcess
    }

    /**
     * singleReadyQueue에서 가장 높은 Response Ratio를 가진 process를 core에 할당한다.
     *
     */
    override fun beforeWork(time: Int) {
        cores.forEach { core ->
            if (core.process == null && singleReadyQueue.isNotEmpty())
                core.process = pollHighResponseRatioProcess(time)
        }
    }

    override fun afterWork(time: Int) {
        // Do nothing
    }

}