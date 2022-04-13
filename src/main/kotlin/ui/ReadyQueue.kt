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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import model.Process
import model.Core
import java.util.*
import kotlin.collections.ArrayList

@Composable
fun PerCoreReadyQueue(
    cores: List<Core?>,
    readyQueues: List<List<Process>>
) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        cores.forEachIndexed { i, processor ->
            ReadyQueueBar(
                coreNumber = i,
                core = processor,
                processes = readyQueues[i].toList()
            )
        }

        Box {
            Text(
                modifier = Modifier.padding(start = 150.dp),
                text = "First",
                style = MaterialTheme.typography.caption
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Last",
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun ReadyQueueList(
    readyQueues: List<Queue<Process>>
) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        readyQueues.forEachIndexed { i, readyQueue ->
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
                    .height(24.dp)
            ) {
                val readyQueueList = readyQueue.toList()

                items(readyQueue.size) { i ->
                    Box(
                        modifier = Modifier.height(24.dp)
                            .defaultMinSize(minWidth = 24.dp)
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

        Box {
            Text(
                modifier = Modifier,
                text = "First",
                style = MaterialTheme.typography.caption
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Last",
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun SingleReadyQueue(
    readyQueue: List<Process>
) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        SingleReadyQueueBar(
            processes = readyQueue
        )

        Box {
            Text(
                modifier = Modifier.padding(start = 0.dp),
                text = "First",
                style = MaterialTheme.typography.caption
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Last",
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun ReadyQueueBar(
    coreNumber: Int,
    core: Core?,
    processes: List<Process>
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
            .height(24.dp)
    ) {
        items(1) {
            Box(
                modifier = Modifier.height(24.dp)
                    .defaultMinSize(minWidth = 150.dp)
                    .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Core $coreNumber [${core?.name ?: "OFF"}]",
                    color = if (core == null) MaterialTheme.colors.error else MaterialTheme.colors.onBackground
                )
            }
        }
        items(processes.size) { i ->
            Box(
                modifier = Modifier.height(24.dp)
                    .defaultMinSize(minWidth = 24.dp)
                    .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
                    .background(Color(processes[i].processColor))
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = processes[i].processName)
            }
        }
    }
}

@Composable
fun SingleReadyQueueBar(
    processes: List<Process>
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
            .height(24.dp)
    ) {
        items(processes.size) { i ->
            Box(
                modifier = Modifier.height(24.dp)
                    .defaultMinSize(minWidth = 24.dp)
                    .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
                    .background(Color(processes[i].processColor))
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = processes[i].processName)
            }
        }
    }
}