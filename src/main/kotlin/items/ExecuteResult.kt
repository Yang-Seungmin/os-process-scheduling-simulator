package items

data class ExecuteResult(
    val process: Process,
    val waitingTime: Int,
    val turnaroundTime: Int
)
