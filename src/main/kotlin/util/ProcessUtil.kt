package util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import manager.ProcessManager
import model.Process
import processColors
import java.io.File
import kotlin.random.Random
import kotlin.random.nextInt

fun generateRandomProcesses(size: Int, btRange: IntRange, atRange: IntRange): List<Process> {
    val list = mutableListOf<Process>()

    var arrivalTime = 0

    repeat(size) {
        list.add(
            Process(
                pid = it,
                processName = "P$it",
                processColor = ProcessManager.generateRandomProcessColor(),
                arrivalTime = arrivalTime,
                workload = Random.nextInt(btRange)
            )
        )

        arrivalTime += Random.nextInt(atRange)
    }

    return list
}

fun main() {
    val processes = generateRandomProcesses(300, 20..40, 0..1)
    println(Json.encodeToStream(processes, File("processes.json").outputStream()))
}