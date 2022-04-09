package items

sealed class Core(
    val name: String,
    val processingPowerPerSecond: Int,
    val powerConsumption: Double,
    val idlePowerConsumption: Double,

    var process: items.Process? = null
) {
    class PCore : Core(
        name = "P-Core",
        processingPowerPerSecond = 2,
        powerConsumption = 3.0,
        idlePowerConsumption = 0.1
    )

    class ECore : Core(
        name = "E-Core",
        processingPowerPerSecond = 1,
        powerConsumption = 1.0,
        idlePowerConsumption = 0.1
    )
}