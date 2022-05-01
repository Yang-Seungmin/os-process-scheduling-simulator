package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import model.Process
import processColors
import util.toPx

var processColorCount = 0
val itemHeight = 20.dp
val columnSize = 3
val columnItems = listOf(
    "Process Name",
    "Arrival Time (AT)",
    "Workload"
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProcessesScreen(
    modifier: Modifier = Modifier,
    processes: List<Process>,
    enabled: Boolean,
    onProcessAdd: (String, Int, Int) -> Unit,
    onProcessUpdate: (Process, Process) -> Unit,
    onProcessDuplicate: (Process) -> Unit,
    onProcessDelete: (Process) -> Unit,
    scrollState: LazyListState
) {
    var editModeIndex by remember { mutableStateOf(-1) }

    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .customBorder()
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .fillMaxHeight()
        ) {
            val dummyProcessCount = (maxHeight / itemHeight).toInt()

            LazyColumn(
                state = scrollState
            ) {
                stickyHeader {
                    ProcessesHeader(maxWidth)
                }
                items(processes.size) { index ->
                    ProcessItem(
                        width = maxWidth,
                        index = index,
                        process = processes[index],
                        editable = enabled,
                        onUpdate = onProcessUpdate,
                        onDuplicate = onProcessDuplicate,
                        onDelete = onProcessDelete,
                        isEditMode = index == editModeIndex,
                        onEditModeChange = {
                            editModeIndex = it
                        }
                    )
                }

                if (dummyProcessCount - processes.size > 0) {
                    items(dummyProcessCount - processes.size) {
                        DummyProcessItem(maxWidth)
                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                adapter = rememberScrollbarAdapter(scrollState)
            )
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
fun ProcessesHeader(
    width: Dp
) {
    Row(modifier = Modifier.width(width)) {
        columnItems.forEach {
            Box(
                modifier = Modifier.width(width / columnSize)
                    .height(itemHeight)
                    .background(MaterialTheme.colors.primary)
                    .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
            ) {
                Text(
                    modifier = Modifier.width(width).height(itemHeight).padding(2.dp),
                    text = it,
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ProcessItem(
    width: Dp,
    index: Int,
    process: Process,
    editable: Boolean,
    isEditMode: Boolean,
    onEditModeChange: (Int) -> Unit,
    onUpdate: (Process, Process) -> Unit,
    onDuplicate: (Process) -> Unit,
    onDelete: (Process) -> Unit
) {
    val edit = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.width(width)
            .height(itemHeight)
    ) {
        val texts = remember {
            mutableStateListOf(
                process.processName,
                process.arrivalTime.toString(),
                process.workload.toString()
            )
        }
        texts.forEachIndexed { i, s ->
            Box(
                modifier = Modifier.width(width / columnSize).height(itemHeight)
                    .background(
                        if (i == 0) {
                            Color(process.processColor)
                        } else {
                            if (edit.value) {
                                MaterialTheme.colors.surface
                            } else {
                                MaterialTheme.colors.background
                            }
                        }
                    )
                    .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
            ) {
                ContextMenuDataProvider(
                    items = {
                        listOf(
                            ContextMenuItem("Duplicate") {
                                onDuplicate(process.copy())
                            },
                            ContextMenuItem("Delete") {
                                onDelete(process)
                            }
                        )
                    }
                ) {
                    if (isEditMode) {
                        BasicTextField(
                            modifier = Modifier.width(width / columnSize).height(itemHeight)
                                .onKeyEvent {
                                    if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                                        if (editable && edit.value) {
                                            texts[1] = (texts[1].toIntOrNull() ?: process.arrivalTime).toString()
                                            texts[2] = (texts[2].toIntOrNull() ?: process.workload).toString()
                                            onUpdate(
                                                process,
                                                process.copy(
                                                    processName = texts[0],
                                                    arrivalTime = texts[1].toInt(),
                                                    workload = texts[2].toInt()
                                                )
                                            )

                                            edit.value = false
                                        }
                                        true
                                    } else false
                                },
                            value = s,
                            onValueChange = {
                                if (editable) {
                                    texts[i] = it
                                    edit.value = true
                                }
                            },
                            singleLine = true,
                            textStyle = TextStyle.Default.copy(textAlign = TextAlign.Center)
                        )
                    } else {
                        Text(
                            modifier = Modifier.width(width / columnSize).height(itemHeight)
                                .mouseClickable {
                                    onEditModeChange(index)
                                },
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
fun DummyProcessItem(
    width: Dp
) {
    Row(
        modifier = Modifier.width(width).height(itemHeight)
    ) {
        columnItems.forEach {
            Box(
                modifier = Modifier.width(width / columnSize).height(itemHeight)
                    .background(MaterialTheme.colors.background)
                    .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
            )
        }
    }
}

@Composable
fun ProcessAddScreen(
    processesCount: Int,
    enabled: Boolean,
    onProcessAdd: (String, Int, Int) -> Unit
) {
    val processName = rememberSaveable { mutableStateOf("") }
    val arrivalTime = rememberSaveable { mutableStateOf("0") }
    val workload = rememberSaveable { mutableStateOf("1") }

    Column(
        modifier = Modifier.customBorder().fillMaxHeight()
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp).padding(top = 8.dp),
            text = "Process Name"
        )
        BasicTextField(
            modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.onBackground,
                    shape = MaterialTheme.shapes.medium
                )
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
                    shape = MaterialTheme.shapes.medium
                )
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
                    shape = MaterialTheme.shapes.medium
                )
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
                    processName.value,
                    arrivalTime.value.toIntOrNull() ?: 0,
                    workload.value.toIntOrNull() ?: 1
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