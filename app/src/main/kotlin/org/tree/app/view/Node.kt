package org.tree.app.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Node(
    x: Int,
    y: Int,
    key: Int, value: String,
    onDrag: (PointerInputChange, Offset) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
    size: Int = 10,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        TooltipArea(
            tooltip = {
                Surface(
                    modifier = Modifier.shadow(4.dp),
                    color = Color(255, 255, 210),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "value: $value",
                        modifier = Modifier.padding(10.dp)
                    )
                }
            },
            modifier = Modifier.offset((x - size / 2).dp, (y - size / 2).dp),
            delayMillis = 600,
            tooltipPlacement = TooltipPlacement.CursorPoint(
                alignment = Alignment.BottomEnd,
                offset = DpOffset(5.dp, 5.dp)
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .requiredSize(size.dp).clip(CircleShape).background(color = color)
                    .pointerInput(onDrag) {
                        detectDragGestures { change, dragAmount ->
                            onDrag(change, dragAmount)
                        }
                    }
            ) {
                AutoSizeText(fontSize = 16.sp, text = key.toString(), maxLines = 1, softWrap = false)
            }
        }
    }

}
