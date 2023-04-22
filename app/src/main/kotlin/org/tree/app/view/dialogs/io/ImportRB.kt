package org.tree.app.view.dialogs.io

import TreeController
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import org.tree.binaryTree.KVP
import org.tree.binaryTree.RBNode

@Composable
fun ImportRBDialog(onCloseRequest: () -> Unit, onSuccess: (TreeController<RBNode<KVP<Int, String>>>) -> Unit = {}) {
    var isDialogOpen by remember { mutableStateOf(true) }

    if (isDialogOpen) {
        Dialog(title = "Import Red-Black Tree",
            onCloseRequest = {
                isDialogOpen = false
            }) {
            Column {
                Text("Клоун! Не отрефакторил код.")
                Button(onClick = { isDialogOpen = false }) {
                    Text("Пип-Пип!")
                }
            }
        }
    } else {
        onCloseRequest()
    }
}

