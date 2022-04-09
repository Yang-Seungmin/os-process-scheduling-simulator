package items

data class Process(
    val pid: Int,
    val processName: String,
    val processColor: Long,
    val arrivalTime: Int,
    val burstTime: Int,
    var executedTime: Int = 0
)