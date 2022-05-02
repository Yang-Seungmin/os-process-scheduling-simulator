package ui.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import model.Process

class ProcessState {
    val processes = mutableStateListOf<Process>()
    val processesScrollState = LazyListState()
    var editModeIndex by mutableStateOf(-1)
    var dummyProcessCount by mutableStateOf(0)

    suspend fun scrollToLast() {
        processesScrollState.animateScrollToItem(if (processes.size > 0) processes.size - 1 else 0)
    }
}

@Composable
fun rememberProcessState() =
    remember { ProcessState() }