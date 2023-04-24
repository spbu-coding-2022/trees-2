package org.tree.app.view.dialogs.io

import TreeController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import org.tree.app.controller.io.Neo4jIO
import org.tree.binaryTree.KVP
import org.tree.binaryTree.RBNode

@Composable
fun ImportRBDialog(
    onCloseRequest: () -> Unit,
    onSuccess: (TreeController<RBNode<KVP<Int, String>>>) -> Unit = {}
) {
    var throwException by remember { mutableStateOf(false) }
    var exceptionContent by remember { mutableStateOf("Nothing...") }

    AlertDialog(exceptionContent, throwException, { throwException = false })
    Neo4jIODialog("Import RBTree", onCloseRequest = onCloseRequest) { enabled, closeRequest, db, treeName ->
        Button(
            enabled = enabled,
            onClick = {
                val treeController =
                    handleIOException(onCatch = {
                        exceptionContent = it.toString()
                        throwException = true
                    }) { db.importRBTree(treeName) }
                if (treeController != null) {
                    db.close()
                    onSuccess(treeController)
                    closeRequest()
                }
            }
        ) {
            Text("Import")
        }
    }
}

@Composable
fun ExportRBDialog(
    onCloseRequest: () -> Unit,
    treeController: TreeController<RBNode<KVP<Int, String>>>
) {

    var throwException by remember { mutableStateOf(false) }
    var exceptionContent by remember { mutableStateOf("Nothing...") }

    AlertDialog(exceptionContent, throwException, { throwException = false })
    Neo4jIODialog("Export RBTree", onCloseRequest = onCloseRequest) { enabled, closeRequest, db, treeName ->
        Button(
            enabled = enabled,
            onClick = {
                handleIOException(onCatch = {
                    exceptionContent = it.toString()
                    throwException = true
                }) { db.exportRBTree(treeController, treeName) }
                if (!throwException) {
                    db.close()
                    closeRequest()
                }
            }
        ) {
            Text("Export")
        }
    }
}

@Composable
fun Neo4jIODialog(
    title: String,
    onCloseRequest: () -> Unit,
    button: @Composable() ((enabled: Boolean, closeRequest: () -> Unit, db: Neo4jIO, treeName: String) -> Unit
    )
) {
    var isDialogOpen by remember { mutableStateOf(true) }
    var isDBEnable by remember { mutableStateOf(false) }
    var expandMenu by remember { mutableStateOf(false) }
    var treeName by remember { mutableStateOf("Tree") }
    var throwException by remember { mutableStateOf(false) }
    var exceptionContent by remember { mutableStateOf("Nothing...") }

    AlertDialog(exceptionContent, throwException, { throwException = false })

    var db = Neo4jIO()

    if (isDialogOpen) {
        Dialog(state = rememberDialogState(width = 500.dp, height = 500.dp),
            title = title,
            onCloseRequest = {
                isDialogOpen = false
            }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Neo4jConnect(onSuccess = {
                    db = it
                    isDBEnable = true
                },
                    onFail = {
                        db = it
                        isDBEnable = false
                    })

                Box {
                    Row {
                        OutlinedTextField(
                            enabled = isDBEnable,
                            value = treeName,
                            onValueChange = { treeName = it },
                        )
                        IconButton(
                            enabled = isDBEnable,
                            onClick = { expandMenu = true }
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Show tree names in db")
                        }
                    }

                    DropdownMenu(
                        expanded = expandMenu,
                        onDismissRequest = { expandMenu = false }
                    ) {
                        if (isDBEnable) {
                            val names = handleIOException(
                                onCatch = {
                                    exceptionContent = it.toString()
                                    throwException = true
                                }
                            ) { db.getTreesNames() }
                            if (names != null) {
                                for (name in names) {
                                    Text(name, modifier = Modifier.padding(10.dp).clickable(onClick = {
                                        treeName = name
                                        expandMenu = false
                                    }))
                                }
                            }
                        }
                    }
                }

                Row {
                    Spacer(modifier = Modifier.weight(0.1f))
                    Button(
                        onClick = {
                            isDialogOpen = false
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    button(isDBEnable, { isDialogOpen = false }, db, treeName)
                    Spacer(modifier = Modifier.weight(0.1f))

                }
            }
        }
    } else {
        onCloseRequest()
    }
}

