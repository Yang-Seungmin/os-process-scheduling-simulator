package algorithm

import items.Processor

abstract class SchedulingAlgorithm {
    protected val _processors = mutableListOf<Processor>().apply {
        add(Processor.PCore)
        add(Processor.PCore)
        add(Processor.ECore)
        add(Processor.ECore)
    }
    val processors : List<Processor> get() = _processors

    fun setProcessors(processors: Collection<Processor>) {
        _processors.clear()
        _processors.addAll(processors)
    }


}