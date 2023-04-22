package org.tree.app.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Line(x0: MutableState<Int>, y0: MutableState<Int>, x1: MutableState<Int>, y1: MutableState<Int>) {
    val x0 = x0.value.dp
    val x1 = x1.value.dp
    val y0 = y0.value.dp
    val y1 = y1.value.dp
    Box(modifier = Modifier.offset(x0, y0)) {
        Box(
            modifier = Modifier.size(x1 - x0, y1 - y0)
                .drawBehind { drawLine(Color.Black, Offset.Zero, Offset((x1 - x0).toPx(), (y1 - y0).toPx()), 1f) }
        ) {

        }
    }
}
