package ui.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import model.ExecuteResult

class ResultState {
    val resultTable = mutableStateListOf<ExecuteResult>()
    val scrollState = LazyListState()
    var dummyProcessCount by mutableStateOf(0)

    suspend fun scrollToLast() {
        scrollState.animateScrollToItem(if (resultTable.size >= dummyProcessCount) resultTable.size else 0)
    }
}

@Composable
fun rememberResultState() = remember { ResultState() }