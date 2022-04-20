package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import model.Process
import processColors

var processColorCount = 0

@Composable
fun ProcessesScreen(
    modifier: Modifier = Modifier,
    processes: List<Process>,
    enabled: Boolean,
    dummyProcessCount: Int = 20,
    onProcessAdd: (Process) -> Unit,
    onProcessDelete: (Process) -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .customBorder()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .fillMaxHeight()
        ) {
            Column {
                ProcessesHeader()
                LazyColumn {
                    items(processes.size) { index ->
                        ProcessItem(processes[index], onProcessDelete)
                    }

                    if(dummyProcessCount - processes.size > 0) {
                        items(dummyProcessCount - processes.size) {
                            DummyProcessItem()
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            ProcessAddScreen(processes.size, enabled, onProcessAdd)
        }
    }

}

@Composable
fun ProcessesHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf(
            "Process Name",
            "Arrival Time (AT)",
            "Workload"
        ).apply {
            forEach {
                Box(
                    modifier = Modifier.weight(1f / this.size)
                        .height(IntrinsicSize.Min)
                        .background(MaterialTheme.colors.primary)
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
fun ProcessItem(
    process: Process,
    onItemClick: (Process) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(IntrinsicSize.Min)
            .fillMaxSize()
            .clickable {
                onItemClick(process)
            }
    ) {
        listOf(
            process.processName,
            process.arrivalTime.toString(),
            process.workload.toString()
        ).apply {
            forEach {
                Box(
                    modifier = Modifier.weight(1f / this.size)
                        .background(Color(process.processColor))
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

@Composable
fun DummyProcessItem(
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(IntrinsicSize.Min)
            .fillMaxSize()
    ) {
        listOf("", "", "").apply {
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

@Composable
fun ProcessAddScreen(
    processesCount: Int,
    enabled: Boolean,
    onProcessAdd: (Process) -> Unit
) {
    val processName = rememberSaveable { mutableStateOf("") }
    val arrivalTime = rememberSaveable { mutableStateOf("0") }
    val workload = rememberSaveable { mutableStateOf("1") }

    Column {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp).padding(top = 8.dp),
            text = "Process Name"
        )
        BasicTextField(
            modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.onBackground,
                    shape = MaterialTheme.shapes.medium)
                .padding(2.dp),
            value = processName.value,
            onValueChange = { processName.value = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Ascii
            ),
            singleLine = true
        )

        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = "Arrival Time"
        )
        BasicTextField(
            modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.onBackground,
                    shape = MaterialTheme.shapes.medium)
                .padding(2.dp),
            value = arrivalTime.value,
            onValueChange = { arrivalTime.value = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )

        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = "Workload"
        )
        BasicTextField(
            modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.onBackground,
                    shape = MaterialTheme.shapes.medium)
                .padding(2.dp),
            value = workload.value,
            onValueChange = { workload.value = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )

        Button(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            content = { Text("Add") },
            onClick = {
                onProcessAdd(
                    Process(
                        pid = processesCount + 1,
                        processName = processName.value,
                        arrivalTime = arrivalTime.value.toIntOrNull() ?: 0,
                        workload = workload.value.toIntOrNull() ?: 1,
                        processColor = processColors[processColorCount++ % processColors.size]
                    )
                )
            },
            enabled =
            processName.value.isNotBlank()
                    && (arrivalTime.value.toIntOrNull() ?: -1) >= 0
                    && (workload.value.toIntOrNull() ?: -1) >= 1
                    && enabled
        )
    }
}