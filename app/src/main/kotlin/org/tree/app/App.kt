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
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import newTree
import org.tree.app.view.TreeView
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InsertRow(onClick: (key: Int, value: String) -> Unit) {
    var keyString by remember { mutableStateOf("123") }
    var valueString by remember { mutableStateOf("value") }

    fun execute() {
        onClick(keyString.toInt(), valueString)
    }

    Row {
        Button(
            onClick = {
                onClick(keyString.toInt(), valueString)
            },
            modifier = Modifier.weight(0.3f).defaultMinSize(minWidth = 100.dp)
        ) {
            Text("Insert")
        }
        OutlinedTextField(
            value = keyString,
            onValueChange = { keyString = it },
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White.copy(alpha = 0.3f)),
            modifier = Modifier.weight(0.35f).onKeyEvent {
                if (it.key == Key.Enter) {
                    execute()
                    return@onKeyEvent true
                }
                return@onKeyEvent false
            }
        )
        OutlinedTextField(
            value = valueString,
            onValueChange = { valueString = it },
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White.copy(alpha = 0.3f)),
            modifier = Modifier.weight(0.35f).onKeyEvent {
                if (it.key == Key.Enter) {
                    execute()
                    return@onKeyEvent true
                }
                return@onKeyEvent false
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RemoveRow(onClick: (key: Int) -> Unit) {
    var keyString by remember { mutableStateOf("123") }

    fun execute() {
        onClick(keyString.toInt())
    }

    Row {
        Button(
            onClick = {
                onClick(keyString.toInt())
            },
            modifier = Modifier.weight(0.3f)
        ) {
            Text("Remove")
        }

        OutlinedTextField(
            value = keyString,
            onValueChange = { keyString = it },
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White.copy(alpha = 0.3f)),
            modifier = Modifier.weight(0.7f).onKeyEvent {
                if (it.key == Key.Enter) {
                    execute()
                    return@onKeyEvent true
                }
                return@onKeyEvent false
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FindRow(onClick: (key: Int) -> (Unit)) {
    var keyString by remember { mutableStateOf("123") }

    fun execute() {
        onClick(keyString.toInt())
    }

    Row {
        Button(onClick = ::execute, modifier = Modifier.weight(0.3f)) {
            Text("Find")
        }
        OutlinedTextField(
            value = keyString,
            onValueChange = { keyString = it },
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White.copy(alpha = 0.3f)),
            modifier = Modifier.weight(0.7f).onKeyEvent {
                if (it.key == Key.Enter) {
                    execute()
                    return@onKeyEvent true
                }
                return@onKeyEvent false
            }
        )
    }
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
        val treeOffsetX = remember { mutableStateOf(100) }
        val treeOffsetY = remember { mutableStateOf(100) }

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
            Column(Modifier.width(widthOfPanel.dp)) {
                InsertRow(onClick = { key, value ->
                    treeController = treeController.insert(KVP(key, value))
                })
                RemoveRow(onClick = { key ->
                    treeController = treeController.remove(KVP(key))
                })
                FindRow(onClick = { key ->
                    val a = treeController.find(KVP(key))
                    if (a != null) {
                        treeOffsetX.value = 100 - a.x.value
                        treeOffsetY.value = 100 - a.y.value
                    }
                })
            }
            Spacer(modifier = Modifier.width(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
                Box(Modifier.background(color = Color.Gray, shape = RoundedCornerShape(5.dp)).size(5.dp, 100.dp)
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
                ExportRBDialog(
                    onCloseRequest = { dialogType = DialogType.EMPTY },
                    treeController as TreeController<RBNode<KVP<Int, String>>>
                )
            }
        }
    }
}
