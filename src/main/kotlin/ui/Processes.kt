package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import manager.ProcessManager
import model.Process
import ui.state.ProcessState

val itemHeight = 20.dp
val processColumnItems = listOf(
    "Process Name",
    "Arrival Time (AT)",
    "Workload"
)
val columnSize = processColumnItems.size

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProcessesScreen(
    modifier: Modifier = Modifier,
    processManager: ProcessManager,
    enabled: Boolean,
) {
    val processes = processManager.processState.processes
    val scrollState = processManager.processState.processesScrollState

    var width by remember { mutableStateOf(0.dp) }

    var scroll by remember { mutableStateOf(0) }

    LaunchedEffect(scroll) {
        processManager.processState.scrollToLast()
    }

    Column(
        modifier = modifier
    ) {
        Row {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Processes (${processes.size})",
                style = MaterialTheme.typography.subtitle1
            )

            Box(modifier = Modifier.padding(8.dp))

            Text(
                modifier = Modifier
                    .clickable(enabled) {
                        processManager.clearProcess()
                    }
                    .padding(8.dp),
                text = "Clear processes",
                color = if (enabled) MaterialTheme.colors.primary else MaterialTheme.colors.surface
            )

            Text(
                modifier = Modifier
                    .clickable(enabled) {
                        importFromJsonFileDialog(ComposeWindow())?.let {
                            processManager.importProcessesFromFile(it)
                            scroll = (scroll + 1) % 2
                        }

                    }
                    .padding(8.dp),
                text = "Import from..",
                color = if (enabled) MaterialTheme.colors.primary else MaterialTheme.colors.surface
            )

            Text(
                modifier = Modifier
                    .clickable(enabled) {
                        exportFromJsonFileDialog(ComposeWindow())?.let {
                            processManager.exportProcessesToFile(it)
                        }

                    }
                    .padding(8.dp),
                text = "Export to..",
                color = if (enabled) MaterialTheme.colors.primary else MaterialTheme.colors.surface
            )
        }

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
                LaunchedEffect(maxHeight) {
                    processManager.processState.dummyProcessCount = (maxHeight / itemHeight).toInt()
                }

                LaunchedEffect(maxWidth) {
                    width = maxWidth
                }

                LazyColumn(
                    state = scrollState
                ) {
                    stickyHeader {
                        ProcessesHeader()
                    }

                    itemsIndexed(
                        items = processes,
                        key = { _, p -> p.pid }) { index, p ->
                        ProcessItem(
                            index = index,
                            width = width,
                            process = p,
                            editable = enabled,
                            onUpdate = { p1, p2 ->
                                processManager.modifyProcess(p1, p2)
                            },
                            onDuplicate = { process ->
                                processManager.duplicateProcess(process)
                            },
                            onDelete = { process ->
                                processManager.removeProcess(process)
                            },
                            isEditMode = index == processManager.processState.editModeIndex,
                            onEditModeChange = {
                                processManager.processState.editModeIndex = it
                            }
                        )
                    }

                    if (processManager.processState.dummyProcessCount - processes.size > 0) {
                        items(processManager.processState.dummyProcessCount - processes.size) {
                            DummyProcessItem()
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
                ProcessAddScreen(processManager.processState, enabled) { name, at, wl ->
                    processManager.addProcess(name, at, wl)
                    scroll = (scroll + 1) % 2
                }
            }
        }
    }
}

@Composable
fun ProcessesHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        processColumnItems.forEach {
            Box(
                modifier = Modifier.weight(1f)
                    .height(itemHeight)
                    .background(MaterialTheme.colors.primary)
                    .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth().height(itemHeight).padding(2.dp),
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
    index: Int,
    width: Dp,
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
                                    if (editable)
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
fun DummyProcessItem() {
    Row(
        modifier = Modifier.fillMaxWidth().height(itemHeight)
    ) {
        processColumnItems.forEach {
            Box(
                modifier = Modifier.weight(1f).height(itemHeight)
                    .background(MaterialTheme.colors.background)
                    .border(width = 0.5.dp, color = MaterialTheme.colors.surface)
            )
        }
    }
}

@Composable
fun ProcessAddScreen(
    processState: ProcessState,
    enabled: Boolean,
    onProcessAdd: (String, Int, Int) -> Unit
) {
    var processName by remember { mutableStateOf("P${processState.processes.size}") }
    var arrivalTime by remember { mutableStateOf("0") }
    var workload by remember { mutableStateOf("1") }

    Column(
        modifier = Modifier.customBorder().fillMaxHeight()
    ) {
        InputWithTitle(
            title = "Process Name",
            value = processName,
            onValueChange = { processName = it }
        )

        InputWithTitle(
            title = "Arrival Time",
            value = arrivalTime,
            onValueChange = { arrivalTime = it }
        )

        InputWithTitle(
            title = "Workload",
            value = workload,
            onValueChange = { workload = it }
        )

        Button(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            content = { Text("Add") },
            onClick = {
                onProcessAdd(
                    processName,
                    arrivalTime.toIntOrNull() ?: 0,
                    workload.toIntOrNull() ?: 1
                )
                processName = "P${processState.processes.size}"
            },
            enabled =
            processName.isNotBlank()
                    && (arrivalTime.toIntOrNull() ?: -1) >= 0
                    && (workload.toIntOrNull() ?: -1) >= 1
                    && enabled
        )
    }
}

@Composable
private fun InputWithTitle(
    title: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Text(
        modifier = Modifier.padding(horizontal = 8.dp).padding(top = 8.dp),
        text = title
    )
    BasicTextField(
        modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onBackground,
                shape = MaterialTheme.shapes.medium
            )
            .padding(2.dp),
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Ascii
        ),
        singleLine = true
    )
}