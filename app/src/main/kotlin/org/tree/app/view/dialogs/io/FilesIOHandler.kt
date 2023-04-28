package org.tree.app.view.dialogs.io

import TreeController
import androidx.compose.ui.awt.ComposeWindow
import org.tree.app.controller.io.Json
import org.tree.app.controller.io.SQLiteIO
import org.tree.binaryTree.AVLNode
import org.tree.binaryTree.KVP
import org.tree.binaryTree.Node
import java.awt.FileDialog
import java.io.File

fun importAVLT(
    window: ComposeWindow,
): TreeController<AVLNode<KVP<Int, String>>>? {
    val fileString = selectFile(window, "json") ?: return null
    val file = File(fileString)
    val db = Json()
    return db.importTree(file)
}

fun exportAVLT(window: ComposeWindow, tc: TreeController<AVLNode<KVP<Int, String>>>) {
    val fileString = selectFile(window, "json") ?: return
    val file = File(fileString)
    val db = Json()
    db.exportTree(tc, file)
}

fun importBST(
    window: ComposeWindow,
): TreeController<Node<KVP<Int, String>>>? {
    val fileString = selectFile(window, "sqlite") ?: return null
    val file = File(fileString)
    val db = SQLiteIO()
    return db.importTree(file)
}

fun exportBST(window: ComposeWindow, tc: TreeController<Node<KVP<Int, String>>>) {
    val fileString = selectFile(window, "sqlite") ?: return
    val file = File(fileString)
    val db = SQLiteIO()
    db.exportTree(tc, file)
}

fun selectFile(window: ComposeWindow, fileFormant: String): String? {
    val fd = FileDialog(window, "Choose a file", FileDialog.LOAD)
    fd.directory = "C:\\"
    fd.file = "*.$fileFormant"
    fd.isVisible = true
    val fileString = fd.directory + fd.file
    if (fileString != "nullnull") {
        return fd.directory + fd.file
    }
    return null
}

