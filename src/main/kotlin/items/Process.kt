package items

import androidx.compose.ui.graphics.Color

data class Process(
    val pid: Int,
    val processName: String,
    val processColor: Long,
    val arrivalTime: Int,
    val burstTime: Int
)
