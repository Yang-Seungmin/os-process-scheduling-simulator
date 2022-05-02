package ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import model.Core

class CoreState {
    val cores = mutableStateListOf<Core?>().apply {
        add(Core.PCore("Core 0 [P-Core]", 0))
        add(Core.PCore("Core 1 [P-Core]", 1))
        add(Core.ECore("Core 2 [E-Core]", 2))
        add(Core.ECore("Core 3 [E-Core]", 3))
    }
    val totalPowerConsumptionPerCore = mutableStateMapOf<Core, Double>()
    val utilizationPerCore = mutableStateMapOf<Core, Double>()
}

@Composable
fun rememberCoreState() = remember { CoreState() }