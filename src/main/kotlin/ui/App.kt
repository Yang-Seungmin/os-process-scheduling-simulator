package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import items.Process

@Composable
@Preview
fun MainScreen() {
    val processes = rememberSaveable { mutableStateListOf<Process>() }
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            Column {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "OS Process Scheduling Simulator",
                    style = MaterialTheme.typography.h4
                )

                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Processes (${processes.size})",
                    style = MaterialTheme.typography.subtitle1
                )

                ProcessesScreen(
                    processes = processes,
                    onProcessAdd = {
                        processes.add(it)
                    }, onProcessDelete = {
                        processes.remove(it)
                    })
            }
        }
    }
}