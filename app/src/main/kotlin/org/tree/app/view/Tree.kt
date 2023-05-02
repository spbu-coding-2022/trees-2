package org.tree.app.view

import TreeController
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import org.tree.binaryTree.KVP
import org.tree.binaryTree.templates.TemplateNode

@Composable
fun <NODE_T : TemplateNode<KVP<Int, String>, NODE_T>> TreeView(
    treeController: TreeController<NODE_T>,
    offsetX: MutableState<Int>,
    offsetY: MutableState<Int>,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.background(Color.White, shape = RoundedCornerShape(16.dp)).clipToBounds().fillMaxSize()
            .pointerInput(
                offsetX, offsetY
            ) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX.value += dragAmount.x.toInt()
                    offsetY.value += dragAmount.y.toInt()
                }
            })
    {
        Box(modifier = Modifier.size(treeController.nodeSize.dp)) {
            for (n in treeController.nodes) {
                val x by n.value.x
                val y by n.value.y

                with(n.key) {
                    val l = treeController.nodes[left]
                    if (l != null) {
                        Line(x + offsetX.value, y + offsetY.value, l.x.value + offsetX.value, l.y.value + offsetY.value)
                    }

                    val r = treeController.nodes[right]
                    if (r != null) {
                        Line(x + offsetX.value, y + offsetY.value, r.x.value + offsetX.value, r.y.value + offsetY.value)
                    }
                }
            }

            for (n in treeController.nodes) {
                var x by n.value.x
                var y by n.value.y
                val col = n.value.color
                with(n.key.element) {
                    key(key) {
                        Node(
                            x + offsetX.value,
                            y + offsetY.value,
                            key,
                            v ?: "",
                            size = treeController.nodeSize,
                            color = col,
                            onDrag = { change, dragAmount ->
                                change.consume()
                                x += dragAmount.x.toInt()
                                y += dragAmount.y.toInt()
                            })
                    }
                }
            }
        }
    }


}

