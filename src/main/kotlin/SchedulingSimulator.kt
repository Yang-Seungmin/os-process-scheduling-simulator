// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
@file:JvmName("Scheduling Simulator")

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.launch
import manager.CoreManager
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import schedulingalgorithm.RR
import schedulingalgorithm.SchedulingAlgorithmRunner
import ui.MainScreen

fun main() = application {

    val schedulingAlgorithmRunner = SchedulingAlgorithmRunner()
    val coreManager = CoreManager()

    val coroutineScope = rememberCoroutineScope()

    Window(
        title = "OS Process Scheduling Simulator",
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            size = DpSize(1280.dp, 720.dp)
        )
    ) {
        MainScreen(
            schedulingAlgorithmRunner = schedulingAlgorithmRunner,
            coreManager = coreManager
        )
    }
}


