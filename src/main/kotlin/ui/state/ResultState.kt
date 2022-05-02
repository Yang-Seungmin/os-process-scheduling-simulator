package ui.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import model.ExecuteResult

class ResultState {
    val resultTable = mutableStateListOf<ExecuteResult>()
    val scrollState = LazyListState()

    suspend fun scrollToLast() {
        scrollState.animateScrollToItem(if (resultTable.size > 0) resultTable.size - 1 else 0)
    }
}

@Composable
fun rememberResultState() = remember { ResultState() }