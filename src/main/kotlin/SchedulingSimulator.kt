// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
@file:JvmName("Scheduling Simulator")

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ui.MainScreen

fun main() = application {
    Window(
        title = "OS Process Scheduling Simulator",
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            size = DpSize(1280.dp, 720.dp)
        )
    ) {
        MainScreen()
    }
}

//코어 워크로드 - 지금 작업량(남은 작업량) 보다 가장 작은 작업량을 가진 프로세스보다 클때