package model

import kotlinx.serialization.Transient

@kotlinx.serialization.Serializable
data class Process(
    val pid: Int,
    val processName: String,
    val processColor: Long,
    val arrivalTime: Int,
    val workload: Int,
    @Transient var doneWorkload: Int = 0,
    @Transient var burstTime: Int = 0
)

val Process.remainWorkload get() = workload - doneWorkload