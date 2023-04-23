package org.tree.app.view

import TreeController
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import org.tree.binaryTree.KVP
import org.tree.binaryTree.templates.TemplateNode

@Composable
fun <NODE_T : TemplateNode<KVP<Int, String>, NODE_T>> Tree(
    t: TreeController<NODE_T>,
    offsetX: MutableState<Int>,
    offsetY: MutableState<Int>,
) {
    Box {
        Box(
            Modifier
                .offset { IntOffset(offsetX.value, offsetY.value) }
                .background(Color.Green) //for debug
                .pointerInput(offsetX, offsetY) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX.value += dragAmount.x.toInt()
                        offsetY.value += dragAmount.y.toInt()
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

