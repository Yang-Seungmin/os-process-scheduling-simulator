// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

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
            size = DpSize(960.dp, 920.dp)
        )
    ) {
        MainScreen()
    }
}