package model

/**
 * Process가 실행이 완료되었을 때 사용하는 클래스
 *
 * @property process 실행이 완료된 프로세스
 * @property turnaroundTime 실행이 완료된 프로세스의 turnaround time
 */
data class ExecuteResult(
    val process: Process,
    val turnaroundTime: Int
)

/**
 * 프로세스의 대기 시간(turnaround time - burst time)
 */
val ExecuteResult.waitingTime get() = turnaroundTime - process.burstTime

/**
 * 프로세스의 Normalized Turnaround Time (turnaround time / burst time)
 */
val ExecuteResult.normalizedTurnAroundTime get() = turnaroundTime / process.burstTime.toDouble()