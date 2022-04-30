package util

import kotlin.math.roundToInt

fun hslToRGB(h: Float, s: Float, l: Float, alpha: Float): IntArray {
    var h = h
    if (s < 0.0f || s > 1f) {
        val message = "Color parameter outside of expected range - Saturation"
        throw IllegalArgumentException(message)
    }
    if (l < 0.0f || l > 1f) {
        val message = "Color parameter outside of expected range - Luminance"
        throw IllegalArgumentException(message)
    }
    if (alpha < 0.0f || alpha > 1.0f) {
        val message = "Color parameter outside of expected range - Alpha"
        throw IllegalArgumentException(message)
    }

    //  Formula needs all values between 0 - 1.
    h %= 360.0f
    h /= 360f
    var q = 0f
    q = if (l < 0.5) l * (1 + s) else l + s - s * l
    val p = 2 * l - q
    val r =
        0f.coerceAtLeast(hueToRGB(p, q, h + 1.0f / 3.0f) * 256).toDouble().roundToInt()
    val g = 0f.coerceAtLeast(hueToRGB(p, q, h) * 256).toDouble().roundToInt()
    val b =
        0f.coerceAtLeast(hueToRGB(p, q, h - 1.0f / 3.0f) * 256).toDouble().roundToInt()
    return intArrayOf(r, g, b)
}

private fun hueToRGB(p: Float, q: Float, h: Float): Float {
    var h = h
    if (h < 0) h += 1f
    if (h > 1) h -= 1f
    if (6 * h < 1) {
        return p + (q - p) * 6 * h
    }
    if (2 * h < 1) {
        return q
    }
    return if (3 * h < 2) {
        p + (q - p) * 6 * (2.0f / 3.0f - h)
    } else p
}