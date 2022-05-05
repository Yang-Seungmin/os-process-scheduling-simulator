package model

import kotlinx.serialization.Transient

/**
 * 프로세스의 정보를 나타내는 데이터 클래스
 *
 * @property pid 프로세스 ID
 * @property processName 프로세스 이름
 * @property processColor 프로세스 배경 색, 프로세스 및 Result Table에서 배경 색을 입혀 구분하는 데 사용
 * @property arrivalTime 프로세스의 도착 시간
 * @property workload 프로세스의 일의 양
 * @property doneWorkload 처리한 일의 양
 * @property burstTime workload를 처리하는 데 걸리는 시간
 */
@kotlinx.serialization.Serializable
data class Process(
    val pid: Int,
    val processName: String,
    val processColor: Long,
    val arrivalTime: Int,
    val workload: Int,
    @Transient var doneWorkload: Int = 0,
    @Transient var burstTime: Int = 0
)

val Process.remainWorkload get() = workload - doneWorkload
fun Process.getResponseRatio(time: Int): Int {
    return 1 + (time - arrivalTime) / workload
}
