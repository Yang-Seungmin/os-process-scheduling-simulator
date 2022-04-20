package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import model.ExecuteResult
import util.toPx
import kotlin.math.roundToInt

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    results: List<ExecuteResult>
) {
    val dummyResultCount = rememberSaveable { mutableStateOf(0) }
    val itemHeightPx = itemHeight.toPx()
    val scrollState = rememberLazyListState()

    rememberCoroutineScope().launch {
        scrollState.animateScrollToItem(if(results.size - 1 < 0) 0 else results.size - 1)
    }

    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .customBorder()
            .onGloballyPositioned {
                dummyResultCount.value = (it.size.height / itemHeightPx).roundToInt()
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column {
                ResultHeader()
                LazyColumn(
                    state = scrollState
                ) {
                    items(results.size) { index ->
                        ResultItem(results[index])
                    }

                    if (dummyResultCount.value - results.size > 0) {
                        items(dummyResultCount.value - results.size) {
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
            "Process",//"Process Name",
            "AT",//"Arrival Time (AT)",
            "BT",//"Burst Time (BT)",
            "WT",//"Waiting Time (WT)",
            "TT",//"Turnaround Time (TT)",
            "NTT"//"Normalized Turnaround Time(NTT)"
        ).apply {
            forEach {
                Box(
                    modifier = Modifier.weight(1f / this.size)
                        .background(MaterialTheme.colors.primary)
                        .fillMaxHeight()
                        .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
                ) {
                    Text(
                        modifier = Modifier.fillMaxSize().padding(2.dp),
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
                String.format("%.3f", executeResult.turnaroundTime / executeResult.process.burstTime.toDouble())
            ).apply {
                forEachIndexed { i, s ->
                    Box(
                        modifier = Modifier.weight(1f / this.size)
                            .background(if(i == 0) Color(process.processColor) else MaterialTheme.colors.background)
                            .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
                    ) {
                        Text(
                            modifier = Modifier.fillMaxSize().padding(2.dp),
                            text = s,
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
                        .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
                ) {
                    Text(
                        modifier = Modifier.fillMaxSize().padding(2.dp),
                        text = it,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}