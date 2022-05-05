package model

/**
 * TODO Process가 실행이 완료되었을 때 사용하는 Data class
 *
 * @property process
 * @property turnaroundTime
 */
data class ExecuteResult(
    val process: Process,
    val turnaroundTime: Int
)

val ExecuteResult.waitingTime get() = turnaroundTime - process.burstTime
val ExecuteResult.normalizedTurnAroundTime get() = turnaroundTime / process.burstTime.toDouble()