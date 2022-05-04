package ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import model.Process
import java.util.*

class ReadyQueueState {
    val readyQueue = mutableStateOf<List<Queue<Process>>>(
        mutableListOf<Queue<Process>>().apply { add(LinkedList()) }
    )
}

@Composable
fun rememberReadyQueueState() = remember { ReadyQueueState() }