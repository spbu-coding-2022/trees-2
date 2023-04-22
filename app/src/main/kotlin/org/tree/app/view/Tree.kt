package org.tree.app.view

import TreeController
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import org.tree.binaryTree.KVP
import org.tree.binaryTree.templates.TemplateNode
import kotlin.math.roundToInt

@Composable
fun <NODE_T : TemplateNode<KVP<Int, String>, NODE_T>> Tree(t: TreeController<NODE_T>) {
    Box {
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Box(
            Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .background(Color.Green) //for debug
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
        ) {
            for (n in t.nodes) {
                val x = n.value.x
                val y = n.value.y

                with(n.key) {
                    val l = t.nodes[left]
                    if (l != null) {
                        Line(x, y, l.x, l.y)
                    }

                    val r = t.nodes[right]
                    if (r != null) {
                        Line(x, y, r.x, r.y)
                    }
                }
            }

            for (n in t.nodes) {
                val x = n.value.x
                val y = n.value.y
                val col = n.value.color
                with(n.key.elem) {
                    key(key) {
                        Node(x, y, key, v ?: "", size = t.nodeSize, color = col)
                    }
                }
            }
        }
    }


}

