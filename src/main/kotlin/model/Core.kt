package model

/**
 * @author Seungmin Yang
 * 코어의 속성을 정의한다.
 *
 * @property name 코어명
 * @property number 코어 번호
 * @property processingPowerPerSecond 초당 처리 가능한 Workload
 * @property powerConsumption 초당 전력 소모량
 * @property idlePowerConsumption 프로세스를 처리하고 있지 않을 때 소모 전력량
 * @property process 코어를 점유하고 있는 프로세스
 */
sealed class Core(
    open val name: String,
    open val number: Int,
    val processingPowerPerSecond: Int,
    val powerConsumption: Double,
    val idlePowerConsumption: Double,

    var process: Process? = null
) {
    /**
     * P-Core: 초당 2의 일을 처리, 전력 소모는 3W, 프로세스의 남은 Workload가 1이여도 2의 일을 처리하는 것과 같다.
     *
     * @property name 코어명
     * @property number 코어 번호
     */
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

    /**
     * E-Core: 초당 1의 일을 처리, 전력 소모는 1W.
     *
     * @property name 코어명
     * @property number 코어 번호
     */
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