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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import newTree
import org.tree.app.controller.io.AppDataController
import org.tree.app.controller.io.SavedTree
import org.tree.app.controller.io.SavedType
import org.tree.app.controller.io.handleIOException
import org.tree.app.view.*
import org.tree.app.view.dialogs.AlertDialog
import org.tree.app.view.dialogs.ExitDialog
import org.tree.app.view.dialogs.io.*
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
    EXPORT_RB
}

val appDataController = AppDataController()
fun main() = application {
    val icon = painterResource("icon.png")
    var showExitDialog by remember { mutableStateOf(false) }
    val windowState = rememberWindowState(width = 800.dp, height = 600.dp)
    Window(
        onCloseRequest = { showExitDialog = true },
        title = "Trees",
        state = windowState,
        icon = icon,
    ) {
        var throwException by remember { mutableStateOf(false) }
        var exceptionContent by remember { mutableStateOf("Nothing...") }

        var widthOfPanel by remember { mutableStateOf(400) }
        var dialogType by remember { mutableStateOf(DialogType.EMPTY) }
        var logString by remember { mutableStateOf("Log string") }
        var logColor by remember { mutableStateOf(Color.DarkGray) }
        val treeOffsetX = remember { mutableStateOf(0) }
        val treeOffsetY = remember { mutableStateOf(0) }
        var treeController by remember {
            mutableStateOf(
                handleIOException(onCatch = { ex ->
                    exceptionContent = "Can't open the last tree because: $ex"
                    throwException = true
                }) {
                    appDataController.loadLastTree()
                } ?: newTree(BinSearchTree())
            )
        }

        fun convertKey(keyString: String): Int? {
            if (keyString.isEmpty()) {
                return null
            }
            return try {
                keyString.toInt()
            } catch (ex: NumberFormatException) {
                logString = "Can't convert \"$keyString\" to int."
                logColor = Color.Red
                null
            }
        }

        fun toTreeRoot() {
            val treeRoot = treeController.tree.root
            if (treeRoot != null) {
                val coordinates = treeController.nodes[treeController.find(treeRoot.element)]
                if (coordinates != null) {
                    logString = "Moved to root."
                    logColor = Color.Green
                    treeOffsetX.value = -coordinates.x.value
                    treeOffsetY.value = -coordinates.y.value
                }
            } else {
                logString = "Current tree is empty."
                logColor = Color.Yellow
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
                    Item("Bin Search Tree", onClick = {
                        val tc = handleIOException(onCatch = { ex ->
                            exceptionContent = "Failed to import tree from file: ${ex.message}"
                            throwException = true
                        }) {
                            importBST()
                        }
                        if (tc != null) {
                            treeController = tc
                        }
                    })
                    Item("Red black Tree", onClick = { dialogType = DialogType.IMPORT_RB })
                    Item("AVL Tree", onClick = {
                        val tc = handleIOException(onCatch = { ex ->
                            exceptionContent = "Failed to import tree from file: ${ex.message}"
                            throwException = true
                        }) {
                            importAVLT()
                        }
                        if (tc != null) {
                            treeController = tc
                        }
                    })
                }
                Menu("Save", mnemonic = 'S') {
                    Item(
                        "Bin Search Tree",
                        onClick = {
                            @Suppress("UNCHECKED_CAST")
                            exportBST(
                                treeController as TreeController<Node<KVP<Int, String>>>
                            )
                        },
                        enabled = (treeController.nodeType() is Node<*>?)
                    )
                    Item(
                        "Red black Tree",
                        onClick = { dialogType = DialogType.EXPORT_RB },
                        enabled = (treeController.nodeType() is RBNode<*>?)
                    )
                    Item(
                        "AVL Tree",
                        onClick = {
                            @Suppress("UNCHECKED_CAST")
                            exportAVLT(
                                treeController as TreeController<AVLNode<KVP<Int, String>>>
                            )
                        },
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
                        val treeBeforeInsert = treeController
                        treeController = treeController.insert(KVP(key, value))
                        if (treeBeforeInsert == treeController) {
                            logString = "The node with key = $key is already in the tree. Nothing has been done."
                            logColor = Color.Yellow
                        } else {
                            logString = "The node with key = $key and value = \"$value\" has been inserted."
                            logColor = Color.Green
                        }
                    })
                    Spacer(modifier = Modifier.size(5.dp))
                    RemoveRow(onClick = { keyString ->
                        val key = convertKey(keyString) ?: return@RemoveRow
                        val treeBeforeInsert = treeController
                        treeController = treeController.remove(KVP(key))
                        if (treeBeforeInsert == treeController) {
                            logString = "There is no node with key = $key in the tree. Nothing has been done."
                            logColor = Color.Yellow
                        } else {
                            logString = "The node with key = $key has been removed."
                            logColor = Color.Green
                        }
                    })
                    Spacer(modifier = Modifier.size(5.dp))
                    FindRow(onClick = { keyString ->
                        val key = convertKey(keyString) ?: return@FindRow
                        val node = treeController.find(KVP(key))
                        if (node != null) {
                            logString = "Node with key = $key and value = \"${node.element.v}\" found."
                            logColor = Color.Green
                            val coordinates = treeController.nodes[node]
                            if (coordinates != null) {
                                treeOffsetX.value = -coordinates.x.value
                                treeOffsetY.value = -coordinates.y.value
                            }
                        } else {
                            logString = "Node with key = $key not found."
                            logColor = Color.Yellow
                        }
                    })
                    Spacer(modifier = Modifier.size(5.dp))
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                treeController = TreeController(treeController.tree)
                                logString = "Tree coordinates reset."
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
                                toTreeRoot()
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
                                val newWidth = widthOfPanel + delta.toInt()
                                if ((windowState.size.width > (newWidth + 10).dp) && (newWidth > 0)) {
                                    widthOfPanel = newWidth
                                }
                            }
                        )
                )
            }
            Spacer(modifier = Modifier.width(3.dp))

            TreeView(treeController, treeOffsetX, treeOffsetY)

        }

        when (dialogType) {
            DialogType.EMPTY -> {
            }

            DialogType.IMPORT_RB -> {
                ImportRBDialog(
                    onCloseRequest = { dialogType = DialogType.EMPTY },
                    onSuccess = { newTreeController, treeName ->
                        treeController = newTreeController
                        appDataController.data.lastTree = SavedTree(SavedType.Neo4j, treeName)
                        appDataController.saveData()
                    })
            }

            DialogType.EXPORT_RB -> {
                @Suppress("UNCHECKED_CAST")
                ExportRBDialog(
                    onCloseRequest = { dialogType = DialogType.EMPTY },
                    onSuccess = { treeName ->
                        appDataController.data.lastTree = SavedTree(SavedType.Neo4j, treeName)
                        appDataController.saveData()
                    },
                    treeController as TreeController<RBNode<KVP<Int, String>>>
                )
            }
        }

        AlertDialog(exceptionContent, throwException, onCloseRequest = { throwException = false })
        ExitDialog(
            showExitDialog,
            onCloseRequest = { showExitDialog = false },
            onExitRequest = ::exitApplication
        ) {
            if (treeController.nodeType() is Node<*>?) {
                Button(onClick = {
                    @Suppress("UNCHECKED_CAST")
                    exportBST(treeController as TreeController<Node<KVP<Int, String>>>)
                }) {
                    Text("Save as Bin Search Tree")
                }
            }
            if (treeController.nodeType() is AVLNode<*>?) {
                Button(onClick = {
                    @Suppress("UNCHECKED_CAST")
                    exportAVLT(treeController as TreeController<AVLNode<KVP<Int, String>>>)
                }) {
                    Text("Save as AVL Tree")
                }
            }
            if (treeController.nodeType() is RBNode<*>?) {
                Button(onClick = {
                    dialogType = DialogType.EXPORT_RB
                }) {
                    Text("Save as RB Tree")
                }
            }
        }
    }
}
