/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package org.tree.app

import TreeController
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import newTree
import org.tree.app.view.*
import org.tree.app.view.dialogs.io.ExportRBDialog
import org.tree.app.view.dialogs.io.ImportRBDialog
import org.tree.binaryTree.AVLNode
import org.tree.binaryTree.KVP
import org.tree.binaryTree.Node
import org.tree.binaryTree.RBNode
import org.tree.binaryTree.trees.AVLTree
import org.tree.binaryTree.trees.BinSearchTree
import org.tree.binaryTree.trees.RBTree

enum class DialogType {
    EMPTY,
    IMPORT_RB,
    IMPORT_AVL,
    IMPORT_BST,
    EXPORT_RB,
    EXPORT_AVL,
    EXPORT_BST
}

fun main() = application {
    val icon = painterResource("icon.png")
    Window(
        onCloseRequest = ::exitApplication,
        title = "Trees",
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        icon = icon
    ) {
        var treeController by remember {
            mutableStateOf<TreeController<*>>(
                newTree(BinSearchTree())
            )
        }
        var widthOfPanel by remember { mutableStateOf(400) }
        var dialogType by remember { mutableStateOf(DialogType.EMPTY) }
        var logString by remember { mutableStateOf("Log string") }
        var logColor by remember { mutableStateOf(Color.DarkGray) }
        val treeOffsetX = remember { mutableStateOf(100) }
        val treeOffsetY = remember { mutableStateOf(100) }

        fun convertKey(keyString: String): Int? {
            return try {
                keyString.toInt()
            } catch (ex: NumberFormatException) {
                logString = "Can't convert \"$keyString\" to int."
                logColor = Color.Red
                null
            }
        }

        MenuBar {
            Menu("File", mnemonic = 'F') {
                Menu("New tree", mnemonic = 'N') {
                    Item("Bin Search Tree", onClick = { treeController = newTree(BinSearchTree()) })
                    Item("Red black Tree", onClick = { treeController = newTree(RBTree()) })
                    Item("AVL Tree") { treeController = newTree(AVLTree()) }
                }
                Menu("Open", mnemonic = 'O') {
                    Item("Bin Search Tree", onClick = { dialogType = DialogType.IMPORT_BST })
                    Item("Red black Tree", onClick = { dialogType = DialogType.IMPORT_RB })
                    Item("AVL Tree", onClick = { dialogType = DialogType.IMPORT_AVL })
                }
                Menu("Save", mnemonic = 'S') {
                    Item(
                        "Bin Search Tree",
                        onClick = { dialogType = DialogType.EXPORT_BST },
                        enabled = (treeController.nodeType() is Node<*>?)
                    )
                    Item(
                        "Red black Tree",
                        onClick = { dialogType = DialogType.EXPORT_RB },
                        enabled = (treeController.nodeType() is RBNode<*>?)
                    )
                    Item(
                        "AVL Tree",
                        onClick = { dialogType = DialogType.EXPORT_AVL },
                        enabled = (treeController.nodeType() is AVLNode<*>?)
                    )
                }
            }
        }

        Row(modifier = Modifier.background(Color.LightGray)) {
            Column(modifier = Modifier.width(widthOfPanel.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    InsertRow(onClick = { keyString, value ->
                        val key = convertKey(keyString) ?: return@InsertRow
                        val rememd = treeController
                        treeController = treeController.insert(KVP(key, value))
                        if (rememd == treeController) {
                            logString = "Node with key = $key already in tree. Nothing is done"
                            logColor = Color.Yellow
                        } else {
                            logString = "Node with key = $key and value = \"$value\" inserted"
                            logColor = Color.Green
                        }
                    })
                    Spacer(modifier = Modifier.size(5.dp))
                    RemoveRow(onClick = { keyString ->
                        val key = convertKey(keyString) ?: return@RemoveRow
                        val rememd = treeController
                        treeController = treeController.remove(KVP(key))
                        if (rememd == treeController) {
                            logString = "There isn't node with key = $key in tree. Nothing is done"
                            logColor = Color.Yellow
                        } else {
                            logString = "Node with key = $key removed"
                            logColor = Color.Green
                        }
                    })
                    Spacer(modifier = Modifier.size(5.dp))
                    FindRow(onClick = { keyString ->
                        val key = convertKey(keyString) ?: return@FindRow
                        val node = treeController.find(KVP(key))
                        if (node != null) {
                            logString = "Found node with key = $key and value = \"${node.elem.v}\""
                            logColor = Color.Green
                            val coord = treeController.nodes[node]
                            if (coord != null) {
                                treeOffsetX.value = 100 - coord.x.value
                                treeOffsetY.value = 100 - coord.y.value
                            }
                        } else {
                            logString = "No node with key = $key found"
                            logColor = Color.Yellow
                        }
                    })
                    Spacer(modifier = Modifier.size(5.dp))
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                treeController = TreeController(treeController.tree)
                                logString = "Tree coordinates reset"
                                logColor = Color.Green
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xffff5b79),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Reset coordinates")
                        }
                        Button(
                            onClick = {
                                val treeRoot = treeController.tree.root
                                if (treeRoot != null) {
                                    val coord = treeController.nodes[treeController.find(treeRoot.elem)]
                                    if (coord != null) {
                                        logString = "Moved to root"
                                        logColor = Color.Green
                                        treeOffsetX.value = 100 - coord.x.value
                                        treeOffsetY.value = 100 - coord.y.value
                                    }
                                } else {
                                    logString = "Current tree is empty"
                                    logColor = Color.Yellow
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Gray,
                                contentColor = Color.White
                            )
                        ) {
                            Text("To root")
                        }
                    }
                }
                Spacer(Modifier.height(5.dp))
                Row(horizontalArrangement = Arrangement.Center) {
                    Spacer(Modifier.width(5.dp))
                    Box(
                        Modifier.background(color = Color.Gray.copy(alpha = 0.5f), shape = RoundedCornerShape(5.dp))
                            .height(3.dp).fillMaxWidth()
                    )
                    Spacer(Modifier.width(5.dp))
                }
                Spacer(Modifier.height(5.dp))
                Logger(logString, logColor)
            }

            Spacer(modifier = Modifier.width(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
                Box(
                    Modifier.background(color = Color.Gray, shape = RoundedCornerShape(5.dp)).size(5.dp, 100.dp)
                        .draggable(
                            orientation = androidx.compose.foundation.gestures.Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                widthOfPanel += delta.toInt()
                                treeOffsetX.value -= delta.toInt() / 2
                            }
                        )
                )
            }
            Spacer(modifier = Modifier.width(3.dp))
            Box(Modifier.scale(1.0F)) {
                TreeView(treeController, treeOffsetX, treeOffsetY)
            }
        }

        when (dialogType) {
            DialogType.EMPTY -> {
            }

            DialogType.IMPORT_AVL -> {
                TODO("Implement")
                dialogType = DialogType.EMPTY
            }

            DialogType.EXPORT_AVL -> {
                TODO("Implement")
                dialogType = DialogType.EMPTY
            }

            DialogType.IMPORT_BST -> {
                TODO("Implement")
                dialogType = DialogType.EMPTY
            }

            DialogType.EXPORT_BST -> {
                TODO("Implement")
                dialogType = DialogType.EMPTY
            }

            DialogType.IMPORT_RB -> {
                ImportRBDialog(
                    onCloseRequest = { dialogType = DialogType.EMPTY },
                    onSuccess = { treeController = it })
            }

            DialogType.EXPORT_RB -> {
                @Suppress("UNCHECKED_CAST")
                ExportRBDialog(
                    onCloseRequest = { dialogType = DialogType.EMPTY },
                    treeController as TreeController<RBNode<KVP<Int, String>>>
                )
            }
        }
    }
}
