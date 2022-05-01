package ui.state

sealed class AlgorithmRunningState {
    object Stopped : AlgorithmRunningState()
    object Running : AlgorithmRunningState()
    object Paused : AlgorithmRunningState()
}
