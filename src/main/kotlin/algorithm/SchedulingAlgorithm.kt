package algorithm

abstract class SchedulingAlgorithm(
    val algorithmName: String,
    val requireReadyQueuePerCore: Boolean
)