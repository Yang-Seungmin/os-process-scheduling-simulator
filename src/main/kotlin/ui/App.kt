package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import items.Process
import items.Processor

@Composable
@Preview
fun MainScreen() {
    val processes = rememberSaveable { mutableStateListOf<Process>() }
    val processors = rememberSaveable {
        mutableStateListOf<Processor?>(Processor.PCore, Processor.PCore, Processor.ECore, Processor.ECore)
    }
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "OS Process Scheduling Simulator",
                    style = MaterialTheme.typography.h4
                )

                Row(
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    // 프로세스
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "Processes (${processes.size})",
                            style = MaterialTheme.typography.subtitle1
                        )

                        ProcessesScreen(
                            processes = processes,
                            onProcessAdd = {
                                processes.add(it)
                            }, onProcessDelete = {
                                processes.remove(it)
                            }
                        )
                    }

                    //프로세서
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "Processes",
                            style = MaterialTheme.typography.subtitle1
                        )

                        ProcessorsScreen(
                            processors = processors,
                            onProcessorChange = { i, processor ->
                                processors[i] = processor
                            }
                        )
                    }
                }
            }
        }
    }
}