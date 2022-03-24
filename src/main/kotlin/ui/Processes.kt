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
import items.Process
import processColors

@Composable
fun ProcessesScreen(
    processes: List<Process>,
    onProcessAdd: (Process) -> Unit,
    onProcessDelete: (Process) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .border(width = 1.dp, color = MaterialTheme.colors.onBackground)
            .height(200.dp)
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
                        ProcessItem(processes[index], processColors[index % processColors.size], onProcessDelete)
                    }

                    if(10 - processes.size > 0) {
                        items(10 - processes.size) {
                            ProcessNullItem()
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
            ProcessAddScreen(processes.size, onProcessAdd)
        }
    }

}

@Composable
fun ProcessesHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf(
            "Process Name",
            "Arrival Time (AT)",
            "Burst Time (BT)"
        ).apply {
            forEach {
                Box(
                    modifier = Modifier.weight(1f / this.size)
                        .height(IntrinsicSize.Min)
                        .background(MaterialTheme.colors.primary)
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
fun ProcessItem(
    process: Process,
    backgroundColor: Color,
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
            process.burstTime.toString()
        ).apply {
            forEach {
                Box(
                    modifier = Modifier.weight(1f / this.size)
                        .background(backgroundColor)
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

@Composable
fun ProcessNullItem(
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

@Composable
fun ProcessAddScreen(
    processesCount: Int,
    onProcessAdd: (Process) -> Unit
) {
    val processName = rememberSaveable { mutableStateOf("") }
    val arrivalTime = rememberSaveable { mutableStateOf(0) }
    val burstTime = rememberSaveable { mutableStateOf(1) }

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
                .padding(4.dp),
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
                .padding(4.dp),
            value = arrivalTime.value.toString(),
            onValueChange = { arrivalTime.value = it.toIntOrNull() ?: 0 },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )

        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = "Burst Time"
        )
        BasicTextField(
            modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.onBackground,
                    shape = MaterialTheme.shapes.medium)
                .padding(4.dp),
            value = burstTime.value.toString(),
            onValueChange = { burstTime.value = it.toIntOrNull() ?: 0 },
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
                        arrivalTime = arrivalTime.value,
                        burstTime = burstTime.value
                    )
                )
            },
            enabled = processName.value.isNotBlank() && arrivalTime.value >= 0 && burstTime.value >= 1
        )
    }
}