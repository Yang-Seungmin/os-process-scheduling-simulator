package util

private operator fun String.times(i: Int): String {
    return with(StringBuilder()) {
        repeat(i) { append(this@times) }
        toString()
    }
}