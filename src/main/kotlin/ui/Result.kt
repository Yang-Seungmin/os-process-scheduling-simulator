package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import model.ExecuteResult

@Composable
fun ResultScreen(
    results: List<ExecuteResult>
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .border(width = 1.dp, color = MaterialTheme.colors.onBackground)
            .height(200.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column {
                ResultHeader()
                LazyColumn {
                    items(results.size) { index ->
                        ResultItem(results[index])
                    }

                    if (5 - results.size > 0) {
                        items(5 - results.size) {
                            DummyResultItem()
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun ResultHeader() {
    Row(modifier = Modifier.fillMaxWidth()
        .height(IntrinsicSize.Min)) {
        listOf(
            "Process Name",
            "Arrival Time (AT)",
            "Burst Time (BT)",
            "Waiting Time (WT)",
            "Turnaround Time (TT)",
            "Normalized Turnaround Time(NTT)"
        ).apply {
            forEach {
                Box(
                    modifier = Modifier.weight(1f / this.size)
                        .background(MaterialTheme.colors.primary)
                        .fillMaxHeight()
                        .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
                ) {
                    Text(
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        text = it,
                        color = MaterialTheme.colors.onPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ResultItem(
    executeResult: ExecuteResult
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(IntrinsicSize.Min)
            .fillMaxSize()
    ) {
        with(executeResult) {
            listOf(
                process.processName,
                process.arrivalTime.toString(),
                process.burstTime.toString(),
                (turnaroundTime - process.burstTime).toString(),
                turnaroundTime.toString(),
                String.format("%.3f", executeResult.turnaroundTime / executeResult.process.doneWorkload.toDouble())
            ).apply {
                forEach {
                    Box(
                        modifier = Modifier.weight(1f / this.size)
                            .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
                    ) {
                        Text(
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            text = it,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun DummyResultItem() {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(IntrinsicSize.Min)
            .fillMaxSize()
    ) {
        listOf("", "", "", "", "", "").apply {
            forEach {
                Box(
                    modifier = Modifier.weight(1f / this.size)
                        .background(MaterialTheme.colors.background)
                        .border(width = 0.5.dp, color = MaterialTheme.colors.onBackground)
                ) {
                    Text(
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        text = it,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}