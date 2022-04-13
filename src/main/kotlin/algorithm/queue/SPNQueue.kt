package algorithm.queue

import model.Process
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList

class SPNQueue(override val size: Int = 0) : Queue<Process> by LinkedList() {

    override fun poll(): Process? {
        val process = getShortestProcess()

        remove(process)
        return process
    }

    override fun element(): Process {
        if(isEmpty()) throw NoSuchElementException()
        return getShortestProcess() ?: throw NoSuchElementException()
    }

    override fun peek(): Process? {
        return getShortestProcess()
    }

    private fun getShortestProcess() = this.minByOrNull { it.workload }
}