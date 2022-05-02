package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.Process
import ui.state.ReadyQueueState
import java.util.*

@Composable
fun ReadyQueue(
    readyQueueState: ReadyQueueState
) {
    Column {
        Text(
            modifier = Modifier.padding(8.dp),
            text = "Ready Queue",
            style = MaterialTheme.typography.subtitle1
        )

        ReadyQueueList(
            readyQueues = readyQueueState.readyQueue.value
        )
    }
}

@Composable
private fun ReadyQueueList(
    readyQueues: List<Queue<Process>>
) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        readyQueues.forEach { readyQueue ->
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
                    .height(20.dp)
            ) {
                val readyQueueList = readyQueue.toList()

                items(readyQueue.size) { i ->
                    Box(
                        modifier = Modifier.height(20.dp)
                            .defaultMinSize(minWidth = 20.dp)
                            .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
                            .background(Color(readyQueueList[i].processColor))
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = readyQueueList[i].processName)
                    }
                }
            }
        }
    }
}