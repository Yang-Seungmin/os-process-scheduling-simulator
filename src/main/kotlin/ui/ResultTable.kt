package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import model.ExecuteResult
import model.normalizedTurnAroundTime
import model.waitingTime
import ui.state.ResultState

val headerItems = listOf(
    "Process",//"Process Name",
    "AT",//"Arrival Time (AT)",
    "BT",//"Burst Time (BT)",
    "WT",//"Waiting Time (WT)",
    "TT",//"Turnaround Time (TT)",
    "NTT"//"Normalized Turnaround Time(NTT)"
)
val headerItemCount = headerItems.size

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    resultState: ResultState,
) {
    LaunchedEffect(resultState.resultTable.size) {
        resultState.scrollToLast()
    }

    Column {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Result (${resultState.resultTable.size})",
                style = MaterialTheme.typography.subtitle1
            )

            Text(
                modifier = Modifier.padding(8.dp),
                text = "Average NTT : ${
                    String.format(
                        "%.3f",
                        resultState.resultTable.map { it.normalizedTurnAroundTime }.average()
                    )
                }",
                style = MaterialTheme.typography.body1
            )
        }

        BoxWithConstraints(
            modifier = modifier
                .padding(horizontal = 8.dp)
                .customBorder()
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val dummyResultCount = (maxHeight / itemHeight).toInt()

            LazyColumn(
                state = resultState.scrollState
            ) {
                stickyHeader {
                    ResultHeader(maxWidth)
                }

                items(resultState.resultTable.size) { index ->
                    ResultItem(maxWidth, resultState.resultTable[index])
                }

                if (dummyResultCount - resultState.resultTable.size > 0) {
                    items(dummyResultCount - resultState.resultTable.size) {
                        DummyResultItem(maxWidth)
                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                adapter = rememberScrollbarAdapter(resultState.scrollState)
            )
        }
    }

}

@Composable
fun ResultHeader(
    width: Dp
) {
    Row(
        modifier = Modifier.width(width).height(itemHeight)
    ) {
        headerItems.forEach {
            Box(
                modifier = Modifier.width(width / headerItemCount).height(itemHeight)
                    .background(MaterialTheme.colors.primary)
                    .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
            ) {
                Text(
                    modifier = Modifier.width(width / headerItemCount).height(itemHeight).padding(2.dp),
                    text = it,
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ResultItem(
    width: Dp,
    executeResult: ExecuteResult
) {
    Row(
        modifier = Modifier.width(width).height(itemHeight)
    ) {
        with(executeResult) {
            listOf(
                process.processName,
                process.arrivalTime.toString(),
                process.burstTime.toString(),
                waitingTime.toString(),
                turnaroundTime.toString(),
                String.format("%.3f", normalizedTurnAroundTime)
            ).forEachIndexed { i, s ->
                Box(
                    modifier = Modifier.width(width / headerItemCount)
                        .background(if (i == 0) Color(process.processColor) else MaterialTheme.colors.background)
                        .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
                ) {
                    Text(
                        modifier = Modifier.width(width / headerItemCount).height(itemHeight).padding(2.dp),
                        text = s,
                        textAlign = TextAlign.Center
                    )
                }
            }

        }

    }
}

@Composable
fun DummyResultItem(
    width: Dp
) {
    Row(
        modifier = Modifier.width(width).height(itemHeight)
    ) {
        headerItems.forEach {
            Box(
                modifier = Modifier.width(width / headerItemCount).height(itemHeight)
                    .background(MaterialTheme.colors.background)
                    .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
            )
        }
    }
}