package ui.state

import androidx.compose.runtime.*
import schedulingalgorithm.FCFS
import schedulingalgorithm.SchedulingAlgorithm

class AlgorithmRunnerState {
    var rrQuantum by mutableStateOf(2)
    var interval by mutableStateOf(100L)

    var schedulingAlgorithm by mutableStateOf<SchedulingAlgorithm>(FCFS())
    var algorithmRunningState by mutableStateOf<AlgorithmRunningState>(AlgorithmRunningState.Stopped)

    var time by mutableStateOf(0)
}

@Composable
fun rememberAlgorithmRunnerState() = remember { AlgorithmRunnerState() }