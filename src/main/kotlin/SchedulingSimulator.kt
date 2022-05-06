// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
@file:JvmName("SchedulingSimulator")

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import manager.CoreManager
import manager.ProcessManager
import schedulingalgorithm.SchedulingAlgorithmRunner
import ui.MainScreen
import ui.exportFromJsonFileDialog
import ui.importFromJsonFileDialog
import ui.state.rememberCoreState
import ui.state.rememberGanttChartState
import ui.state.rememberProcessState
import ui.state.rememberReadyQueueState
import ui.state.rememberResultState
import ui.state.rememberAlgorithmRunnerState

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val mainWindowState = rememberWindowState(
        size = DpSize(1280.dp, 720.dp)
    )
    val aboutWindowState = rememberWindowState(
        size = DpSize(400.dp, Dp.Unspecified),

    )
    val coroutineScope = rememberCoroutineScope()

    val processState = rememberProcessState()
    val coreState = rememberCoreState()
    val readyQueueState = rememberReadyQueueState()
    val ganttChartState = rememberGanttChartState()
    val resultState = rememberResultState()
    val algorithmRunnerState = rememberAlgorithmRunnerState()

    val schedulingAlgorithmRunner = SchedulingAlgorithmRunner(
        processes = processState.processes,
        coreState = coreState,
        readyQueueState = readyQueueState,
        totalPowerConsumption = coreState.totalPowerConsumptionPerCore,
        ganttChartRecord = ganttChartState.ganttChartMapState,
        resultTable = resultState.resultTable,
        algorithmRunnerState = algorithmRunnerState
    )
    val coreManager = CoreManager(coreState, ganttChartState)
    val processManager = ProcessManager(processState)

    val randomProcessGeneratorWindowOpened = remember { mutableStateOf(false) }
    val aboutWindowOpened = remember { mutableStateOf(false) }

    if (randomProcessGeneratorWindowOpened.value) {
        RandomProcessGenerator(processManager) {
            randomProcessGeneratorWindowOpened.value = false
        }
    }

    if (aboutWindowOpened.value) {
        AboutScreen(windowState = aboutWindowState) {
            aboutWindowOpened.value = false
        }
    }

    Window(
        title = "Process Scheduling Simulator",
        onCloseRequest = ::exitApplication,
        state = mainWindowState
    ) {
        val onExport: () -> Unit = {
            exportFromJsonFileDialog(ComposeWindow())?.let { file ->
                processManager.exportProcessesToFile(file)
            }
        }
        val onImport: () -> Unit = {
            importFromJsonFileDialog(ComposeWindow())?.let { file ->
                processManager.importProcessesFromFile(file)
            }
        }

        LaunchedEffect(aboutWindowOpened.value) {
            aboutWindowState.position = WindowPosition(
                x = mainWindowState.position.x + mainWindowState.size.width / 2 - aboutWindowState.size.width / 2,
                y = mainWindowState.position.y + mainWindowState.size.height / 6
            )
        }

        MainScreen(
            coroutineScope = coroutineScope,
            schedulingAlgorithmRunner = schedulingAlgorithmRunner,
            coreManager = coreManager,
            processManager = processManager,
            readyQueueState = readyQueueState,
            ganttChartState = ganttChartState,
            resultState = resultState
        ) {
            randomProcessGeneratorWindowOpened.value = true
        }

        MenuBar {
            Menu(
                text = "Process",
                mnemonic = 'P'
            ) {
                Item(
                    text = "Import process list from JSON file",
                    onClick = onImport,
                    shortcut = KeyShortcut(Key.I, ctrl = true)
                )
                Item(
                    text = "Export process list to JSON file",
                    onClick = onExport,
                    shortcut = KeyShortcut(Key.E, ctrl = true)
                )
                Separator()
                Item(
                    text = "Open random process generator",
                    onClick = {randomProcessGeneratorWindowOpened.value = true}
                )
            }

            Menu(
                text = "Help",
                mnemonic = 'H'
            ) {
                Item(
                    text = "About Process Scheduling Simulator",
                    onClick = {
                        aboutWindowOpened.value = true
                    }
                )
            }
        }
    }
}

