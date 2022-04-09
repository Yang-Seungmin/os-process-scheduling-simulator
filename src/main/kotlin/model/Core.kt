package model

sealed class Core(
    open val name: String,
    val processingPowerPerSecond: Int,
    val powerConsumption: Double,
    val idlePowerConsumption: Double,

    var process: Process? = null
) {
    class PCore(
        name: String
    ) : Core(
        name = name,
        processingPowerPerSecond = 2,
        powerConsumption = 3.0,
        idlePowerConsumption = 0.1
    )

    class ECore(
        name: String
    ) : Core(
        name = name,
        processingPowerPerSecond = 1,
        powerConsumption = 1.0,
        idlePowerConsumption = 0.1
    )
}