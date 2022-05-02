// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
@file:JvmName("Scheduling Simulator")

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import manager.CoreManager
import manager.ProcessManager
import schedulingalgorithm.SchedulingAlgorithmRunner
import ui.MainScreen
import ui.state.rememberCoreState
import ui.state.rememberGanttChartState
import ui.state.rememberProcessState
import ui.state.rememberReadyQueueState
import ui.state.rememberResultState
import ui.state.rememberAlgorithmRunnerState

fun main() = application {
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

    if (randomProcessGeneratorWindowOpened.value) {
        RandomProcessGenerator(processManager) {
            randomProcessGeneratorWindowOpened.value = false
        }
    }

    Window(
        title = "OS Process Scheduling Simulator",
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            size = DpSize(1280.dp, 720.dp)
        )
    ) {
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
    }
}


