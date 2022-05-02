package manager

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import model.Process
import ui.state.ProcessState
import util.hslToRGB
import java.io.File
import kotlin.random.Random

class ProcessManager(
    val processState: ProcessState
) {
    private val processes = processState.processes

    val size get() = processes.size

    var pid = 0

    fun addProcess(
        processName: String,
        processArrivalTime: Int,
        processWorkload: Int
    ) {
        processes.add(
            Process(
                pid = ++pid,
                processName = processName,
                processColor = generateRandomProcessColor(),
                arrivalTime = processArrivalTime,
                workload = processWorkload
            )
        )
    }

    fun addProcesses(
        processes: Collection<Process>
    ) {
        this.processes.addAll(processes)
    }

    fun removeProcess(process: Process) {
        processes.remove(process)
    }

    fun duplicateProcess(process: Process) {
        val index = processes.indexOf(process)

        if (index >= 0) {
            processes.add(
                index,
                process.copy(
                    pid = ++pid,
                    processColor = generateRandomProcessColor()
                )
            )
        } else processes.add(process)
    }

    fun modifyProcess(
        beforeProcess: Process,
        afterProcess: Process
    ) {
        val index = processes.indexOf(beforeProcess)

        if (index >= 0) {
            processes[index] = afterProcess
        } else processes.add(afterProcess)
    }

    fun clearProcess() {
        processes.clear()
    }

    fun importProcessesFromFile(file: File) {
        processes.clear()
        processes.addAll(Json.decodeFromStream<List<Process>>(file.inputStream()))
    }

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