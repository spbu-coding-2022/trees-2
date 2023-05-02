package org.tree.app.view.dialogs.io

import TreeController
import androidx.compose.ui.awt.ComposeWindow
import org.tree.app.appDataController
import org.tree.app.controller.io.JsonIO
import org.tree.app.controller.io.SQLiteIO
import org.tree.app.controller.io.SavedTree
import org.tree.app.controller.io.SavedType
import org.tree.binaryTree.AVLNode
import org.tree.binaryTree.KVP
import org.tree.binaryTree.Node
import java.awt.FileDialog
import java.io.File

fun importAVLT(): TreeController<AVLNode<KVP<Int, String>>>? {
    val fileString = selectFile("json", Mode.IMPORT) ?: return null
    val file = File(fileString)
    val db = JsonIO()
    val treeController = db.importTree(file)
    appDataController.data.lastTree = SavedTree(SavedType.Json, file.path)
    appDataController.saveData()
    return treeController
}

fun exportAVLT(tc: TreeController<AVLNode<KVP<Int, String>>>) {
    val fileString = selectFile("json", Mode.EXPORT) ?: return
    val file = File(fileString)
    val db = JsonIO()
    db.exportTree(tc, file)
    appDataController.data.lastTree = SavedTree(SavedType.Json, file.path)
    appDataController.saveData()
}

fun importBST(): TreeController<Node<KVP<Int, String>>>? {
    val fileString = selectFile("sqlite", Mode.IMPORT) ?: return null
    val file = File(fileString)
    val db = SQLiteIO()
    val treeController = db.importTree(file)
    appDataController.data.lastTree = SavedTree(SavedType.SQLite, file.path)
    appDataController.saveData()
    return treeController
}

fun exportBST(tc: TreeController<Node<KVP<Int, String>>>) {
    val fileString = selectFile("sqlite", Mode.EXPORT) ?: return
    val file = File(fileString)
    val db = SQLiteIO()
    db.exportTree(tc, file)
    appDataController.data.lastTree = SavedTree(SavedType.SQLite, file.path)
    appDataController.saveData()
}

enum class Mode {
    IMPORT,
    EXPORT
}

fun selectFile(fileExtension: String, mode: Mode): String? {
    val fd = if (mode == Mode.IMPORT) {
        FileDialog(ComposeWindow(), "Choose .$fileExtension file to import", FileDialog.LOAD)
    } else {
        FileDialog(ComposeWindow(), "Choose .$fileExtension file to export", FileDialog.SAVE)
    }
    fd.directory = "C:\\"
    fd.file = "*.$fileExtension"
    fd.isVisible = true
    val fileString = fd.directory + fd.file
    if (fileString != "nullnull") {
        return fd.directory + fd.file
    }
    return null
}
