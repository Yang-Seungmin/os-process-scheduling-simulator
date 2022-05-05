package manager

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import model.Process
import ui.state.ProcessState
import util.hslToRGB
import java.io.File
import kotlin.random.Random

/**
 * @author Seungmin Yang
 * @see Process
 * 프로세스의 상태를 변경할 수 있는 클래스. 프로세스의 추가/삭제/변경/복제, 파일로 내보내기/가져오기 기능을 제공한다.
 *
 * @property processState
 */
class ProcessManager(
    val processState: ProcessState
) {
    private val processes = processState.processes

    /**
     * Process State List의 Process 개수
     */
    val size get() = processes.size

    var pid = 0

    /**
     * 프로세스를 Process State List에 추가한다.
     *
     * @param processName 프로세스명
     * @param processArrivalTime 프로세스의 Arrival Time
     * @param processWorkload 프로세스의 Workload(Burst Time)
     */
    fun addProcess(
        processName: String,
        processArrivalTime: Int,
        processWorkload: Int
    ) {
        processes.add(
            Process(
                pid = processes.size + 1,
                processName = processName,
                processColor = generateRandomProcessColor(),
                arrivalTime = processArrivalTime,
                workload = processWorkload
            )
        )
    }

    /**
     * 프로세스 여러 개를 Process State List에 추가한다.
     *
     * @param processes
     */
    fun addProcesses(
        processes: Collection<Process>
    ) {
        this.processes.addAll(processes)
    }

    /**
     * 프로세스를 Process State List에서 삭제한다.
     *
     * @param process 삭제할 프로세스
     */
    fun removeProcess(process: Process) {
        processes.remove(process)
    }

    /**
     * 프로세스를 복제하여 Process State List에 추가한다.
     * 만약 전달받은 프로세스가 Process State List에 없을 경우 복제하지 않고 추가한다.
     *
     * @param process 복제할 프로세스, Process State List에 없는 프로세스일 경우 복제를 수행하지 않고 추가.
     */
    fun duplicateProcess(process: Process) {
        val index = processes.indexOf(process)

        if (index >= 0) {
            processes.add(
                index,
                process.copy(
                    pid = processes.size,
                    processColor = generateRandomProcessColor()
                )
            )
        } else processes.add(process)
    }

    /**
     * Process State List 내의 프로세스를 교체한다.
     *
     * @param beforeProcess 교체 전 프로세스
     * @param afterProcess 교체 후 프로세스
     */
    fun modifyProcess(
        beforeProcess: Process,
        afterProcess: Process
    ) {
        val index = processes.indexOf(beforeProcess)

        if (index >= 0) {
            processes[index] = afterProcess
        } else processes.add(afterProcess)
    }

    /**
     * Process State List 내의 프로세스를 모두 지운다
     *
     */
    fun clearProcess() {
        processes.clear()
    }

    /**
     * File 객체로부터 프로세스를 가져온다.
     *
     * @param file
     */
    fun importProcessesFromFile(file: File) {
        processes.clear()
        processes.addAll(Json.decodeFromStream<List<Process>>(file.inputStream()))
    }

    /**
     * File 객체로 현재 Process State List의 프로세스들을 파일로 내보낸다.
     *
     * @param file
     */
    fun exportProcessesToFile(file: File) {
        Json.encodeToStream(processes.toList(), file.outputStream())
    }

    companion object {
        private var beforeRandom = 180f

        fun generateRandomProcessColor(): Long {
            var random = Random.nextFloat() * 360f
            while (beforeRandom - 40f < random && random < beforeRandom + 40f) {
                random = Random.nextFloat() * 360f
            }
            beforeRandom = random

            val randomColorArray = hslToRGB(
                random,
                Random.nextFloat() * 0.9f,
                0.6f,
                1f
            )

            return 0xff000000 or
                    ((randomColorArray[0] shl 16).toLong()) or
                    ((randomColorArray[1] shl 8).toLong()) or
                    randomColorArray[2].toLong()
        }
    }
}