package model

data class ExecuteResult(
    val process: Process,
    val turnaroundTime: Int
)

val ExecuteResult.waitingTime get() = turnaroundTime - process.burstTime
val ExecuteResult.normalizedTurnAroundTime get() = turnaroundTime / process.burstTime.toDouble()