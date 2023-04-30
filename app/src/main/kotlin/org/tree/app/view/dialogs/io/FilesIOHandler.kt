package org.tree.app.view.dialogs.io

import TreeController
import androidx.compose.ui.awt.ComposeWindow
import org.tree.app.appDataController
import org.tree.app.controller.io.*
import org.tree.binaryTree.AVLNode
import org.tree.binaryTree.KVP
import org.tree.binaryTree.Node
import java.awt.FileDialog
import java.io.File

fun importAVLT(
    window: ComposeWindow,
): TreeController<AVLNode<KVP<Int, String>>>? {
    val fileString = selectFile(window, "json", "import") ?: return null
    val file = File(fileString)
    val db = Json()
    val treeController = db.importTree(file)
    appDataController.data.lastTree = SavedTree(SavedType.Json, file.path)
    appDataController.saveData()
    return treeController
}

fun exportAVLT(window: ComposeWindow, tc: TreeController<AVLNode<KVP<Int, String>>>) {
    val fileString = selectFile(window, "json", "export") ?: return
    val file = File(fileString)
    val db = Json()
    db.exportTree(tc, file)
    appDataController.data.lastTree = SavedTree(SavedType.Json, file.path)
    appDataController.saveData()
}

fun importBST(
    window: ComposeWindow,
): TreeController<Node<KVP<Int, String>>>? {
    val fileString = selectFile(window, "sqlite", "import") ?: return null
    val file = File(fileString)
    val db = SQLiteIO()
    val treeController = try {
        db.importTree(file)
    } catch (ex: HandledIOException) {
        null
    }
    if (treeController != null) {
        appDataController.data.lastTree = SavedTree(SavedType.SQLite, file.path)
        appDataController.saveData()
    }
    return treeController
}

fun exportBST(window: ComposeWindow, tc: TreeController<Node<KVP<Int, String>>>) {
    val fileString = selectFile(window, "sqlite", "export") ?: return
    val file = File(fileString)
    val db = SQLiteIO()
    db.exportTree(tc, file)
    appDataController.data.lastTree = SavedTree(SavedType.SQLite, file.path)
    appDataController.saveData()
}

fun selectFile(window: ComposeWindow, fileFormant: String, mode: String): String? {
    val fd = if (mode == "import") {
        FileDialog(window, "Choose .sqlite file to import", FileDialog.LOAD)
    } else {
        FileDialog(window, "Choose .sqlite file to export", FileDialog.SAVE)
    }
    fd.directory = "C:\\"
    fd.file = "*.$fileFormant"
    fd.isVisible = true
    val fileString = fd.directory + fd.file
    if (fileString != "nullnull") {
        return fd.directory + fd.file
    }
    return null
}

