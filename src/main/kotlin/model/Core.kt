package model

sealed class Core(
    open val name: String,
    open val number: Int,
    val processingPowerPerSecond: Int,
    val powerConsumption: Double,
    val idlePowerConsumption: Double,

    var process: Process? = null
) {
    data class PCore(
        override val name: String,
        override val number: Int
    ) : Core(
        name = name,
        number = number,
        processingPowerPerSecond = 2,
        powerConsumption = 3.0,
        idlePowerConsumption = 0.1
    )

    data class ECore(
        override val name: String,
        override val number: Int
    ) : Core(
        name = name,
        number = number,
        processingPowerPerSecond = 1,
        powerConsumption = 1.0,
        idlePowerConsumption = 0.1
    )
}