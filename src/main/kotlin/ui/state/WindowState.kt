package ui.state

import androidx.compose.runtime.mutableStateListOf

private class MyApplicationState {
    val windows = mutableStateListOf<WindowState>()

    init {
        windows += WindowState("Initial window")
    }

    fun openNewWindow() {
        windows += WindowState("Window ${windows.size}")
    }

    fun exit() {
        windows.clear()
    }

    private fun WindowState(
        title: String
    ) = WindowState(
        openNewWindow = ::openNewWindow,
        exit = ::exit,
        windows::remove
    )
}

private class WindowState(
    val openNewWindow: () -> Unit,
    val exit: () -> Unit,
    private val close: (WindowState) -> Unit
) {
    fun close() = close(this)
}