package items

import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.mapSaver

sealed class Processor(
    val name: String,
    val processingPowerPerSecond: Int,
    val powerConsumption: Double,
    val idlePowerConsumption: Double
) {
    object PCore : Processor(
        name = "P-Core",
        processingPowerPerSecond = 2,
        powerConsumption = 3.0,
        idlePowerConsumption = 0.1
    )

    object ECore : Processor(
        name = "E-Core",
        processingPowerPerSecond = 1,
        powerConsumption = 1.0,
        idlePowerConsumption = 0.1
    )
}