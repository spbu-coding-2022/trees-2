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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.tree.binaryTree.KVP
import org.tree.binaryTree.templates.TemplateNode

class NodeView<NODE_T : TemplateNode<KVP<Int, String>, NODE_T>>(nd: NODE_T) { // legacy code
    var node: NODE_T = nd
    var x: Double = 0.0
    var y: Double = 0.0
    var l: NodeView<NODE_T>? = null
    var r: NodeView<NODE_T>? = null

    init {
        node.left?.let {
            l = NodeView(it)
        }
        node.right?.let {
            r = NodeView(it)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Node(
    x: MutableState<Int>,
    y: MutableState<Int>,
    key: Int, value: String,
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
            modifier = Modifier.offset((x.value - size / 2).dp, (y.value - size / 2).dp),
            delayMillis = 600,
            tooltipPlacement = TooltipPlacement.CursorPoint(
                alignment = Alignment.BottomEnd,
                offset = DpOffset(5.dp, 5.dp)
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .size(size.dp).clip(CircleShape).background(color = color)
                    .pointerInput(x, y) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            x.value += dragAmount.x.toInt()
                            y.value += dragAmount.y.toInt()
                        }
                    }
            ) {
                Text(key.toString())
            }
        }
    }

}
