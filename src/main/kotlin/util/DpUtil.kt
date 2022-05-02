package util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPx(): Float {
    with(LocalDensity.current) {
        return this@toPx.toPx()
    }
}

@Composable
fun Float.toDp(): Dp {
    with(LocalDensity.current) {
        return this@toDp.toDp()
    }
}