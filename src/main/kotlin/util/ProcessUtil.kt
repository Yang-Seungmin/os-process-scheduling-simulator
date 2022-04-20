package util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Process
import processColors
import kotlin.random.Random
import kotlin.random.nextInt

fun generateRandomProcesses(size: Int, btRange: IntRange, arrivalTimeRange: IntRange): List<Process> {
    val list = mutableListOf<Process>()

    var arrivalTime = 0

    repeat(size) {
        list.add(
            Process(
                pid = it,
                processName = "P$it",
                processColor = processColors[it % processColors.size],
                arrivalTime = arrivalTime,
                workload = Random.nextInt(btRange)
            )
        )

        arrivalTime += Random.nextInt(arrivalTimeRange)
    }

    return list
}

fun main() {
    val processes = generateRandomProcesses(100, 1..100, 0..10)
    println(Json.encodeToString(processes))
}