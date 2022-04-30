package ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.AwtWindow
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
fun ProcessesJsonFileLoadDialog(
    parent: Frame? = null,
    onCloseRequest: (directory: String?, file: String?) -> Unit
) = AwtWindow (
    create = {
        object : FileDialog(parent, "Choose a file", LOAD) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest(directory, file)
                }
            }
        }.apply {
            setFilenameFilter { dir, name -> name.split(".").last() == "json" }
        }
    },
    dispose = FileDialog::dispose
)
@Composable
fun ProcessesJsonFileSaveDialog(
    parent: Frame? = null,
    onCloseRequest: (directory: String?, file: String?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(parent, "Save to", SAVE) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest(directory, file)
                }
            }
        }.apply {
            file = "processes.json"
        }
    },
    dispose = FileDialog::dispose
)

fun importFromJsonFileDialog(window: ComposeWindow): File? {
    val files = FileDialog(window, "Select a processes json file", FileDialog.LOAD).apply {
        // windows
        file = "*.json"
        // linux
        setFilenameFilter { dir, name -> name.split(".").last() == "json" }

        isVisible = true
    }.files

    return if(files.isNotEmpty()) files[0] else null
}

fun exportFromJsonFileDialog(window: ComposeWindow): File? {
    val files = FileDialog(window, "Export to processes json file", FileDialog.SAVE).apply {
        // windows
        file = "processes.json"
        // linux
        setFilenameFilter { dir, name -> name.split(".").last() == "json" }
        isVisible = true
    }.files

    return if(files.isNotEmpty()) files[0] else null
}