package manager

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import model.Process
import ui.processColorCount
import util.hslToRGB
import java.io.File
import kotlin.random.Random

class ProcessManager {
    private val _processes = mutableListOf<Process>().toMutableStateList()
    val processes: List<Process> get() = _processes
    val processesStateList: SnapshotStateList<Process> get() = _processes

    val size get() = _processes.size

    var pid = 0

    fun addProcess(
        processName: String,
        processArrivalTime: Int,
        processWorkload: Int
    ) {
        _processes.add(
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
        _processes.addAll(processes)
    }

    fun removeProcess(process: Process) {
        _processes.remove(process)
    }

    fun duplicateProcess(process: Process) {
        val index = _processes.indexOf(process)

        if (index >= 0) {
            _processes.add(
                index,
                process.copy(
                    pid = ++pid,
                    processColor = generateRandomProcessColor()
                )
            )
        } else _processes.add(process)
    }

    fun modifyProcess(
        beforeProcess: Process,
        afterProcess: Process
    ) {
        val index = _processes.indexOf(beforeProcess)

        if (index >= 0) {
            _processes[index] = afterProcess
        } else _processes.add(afterProcess)
    }

    fun clearProcess() {
        _processes.clear()
    }

    fun importProcessesFromFile(file: File) {
        _processes.clear()
        _processes.addAll(Json.decodeFromStream<List<Process>>(file.inputStream()))
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
                Random.nextFloat() * 0.3f + 0.5f,
                0.85f,
                1f
            )


            return 0xff000000 or
                    ((randomColorArray[0] shl 16).toLong()) or
                    ((randomColorArray[1] shl 8).toLong()) or
                    randomColorArray[2].toLong()
        }
    }
}