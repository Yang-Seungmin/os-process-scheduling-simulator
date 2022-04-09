package ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.AwtWindow
import java.awt.FileDialog
import java.awt.Frame

@Composable
fun ProcessesJsonFileLoadDialog(
    parent: Frame? = null,
    onCloseRequest: (directory: String?, file: String?) -> Unit
) = AwtWindow(
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