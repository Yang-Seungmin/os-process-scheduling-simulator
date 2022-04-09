package model

import kotlinx.serialization.Transient

@kotlinx.serialization.Serializable
data class Process(
    val pid: Int,
    val processName: String,
    val processColor: Long,
    val arrivalTime: Int,
    val burstTime: Int,
    @Transient var executedTime: Int = 0
)