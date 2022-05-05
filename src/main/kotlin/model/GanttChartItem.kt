package model

/**
 * 간트 차트를 그릴 때 사용하는 데이터 클래스
 *
 * @property process 프로세스
 * @property core 프로세스가 점유하고 있는 코어
 * @property time 프로세스가 점유하기 시작한 시간/끝난 시간 (start inclusive and end inclusive)
 */
data class GanttChartItem(
    val process: Process,
    val core: Core,
    val time: IntRange
)

fun IntRange.range(): Int {
    return this.last - this.first
}