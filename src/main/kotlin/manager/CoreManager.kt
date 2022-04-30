package manager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import model.Core

class CoreManager {
    private val _cores = mutableListOf<Core?>(
        Core.PCore("Core 0 [P-Core]"),
        Core.PCore("Core 1 [P-Core]"),
        Core.ECore("Core 2 [E-Core]"),
        Core.ECore("Core 3 [E-Core]")
    )
    val cores : List<Core?> get() = _cores

    @get:Composable
    val coreState: SnapshotStateList<Core?>
        get() = cores.toMutableStateList()

    fun setPCore(index: Int) : Core? {
        _cores[index] = Core.PCore("Core $index [P-Core]")
        return cores[index]
    }

    fun setECore(index: Int) : Core? {
        _cores[index] = Core.ECore("Core $index [E-Core]")
        return cores[index]
    }

    fun setCoreOff(index: Int) : Core? {
        _cores[index] = null
        return cores[index]
    }
}