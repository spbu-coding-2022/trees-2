package org.tree.app.controller.io

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import TreeController
import org.tree.binaryTree.AVLNode
import org.tree.binaryTree.KVP
import java.io.File
import NodeExtension
import androidx.compose.runtime.mutableStateOf
import org.tree.binaryTree.trees.AVLTree
import java.io.FileNotFoundException
import java.nio.file.Files

@Serializable
private data class JsonAVLNode(
    val key: Int,
    val value: String?,
    val x: Int,
    val y: Int,
    val height: Int,
    val left: JsonAVLNode?,
    val right: JsonAVLNode?
)

@Serializable
private data class JsonAVLTree(
    val root: JsonAVLNode?
)

class JsonIO {

    private fun AVLNode<KVP<Int, String>>.serialize(treeController: TreeController<AVLNode<KVP<Int, String>>>): JsonAVLNode {
        return JsonAVLNode(
            key = this.element.key,
            value = this.element.v,
            x = treeController.nodes[this]?.x?.value ?: 0,
            y = treeController.nodes[this]?.y?.value ?: 0,
            height = this.height,
            left = this.left?.serialize(treeController),
            right = this.right?.serialize(treeController)
        )
    }

    private fun JsonAVLNode.deserialize(treeController: TreeController<AVLNode<KVP<Int, String>>>): AVLNode<KVP<Int, String>> {
        val nv = AVLNode(KVP(key, value))
        nv.height = height
        treeController.nodes[nv] = NodeExtension(mutableStateOf(x), mutableStateOf(y), treeController.getNodeCol(nv))
        nv.left = left?.deserialize(treeController)
        nv.right = right?.deserialize(treeController)
        return nv
    }

    fun exportTree(treeController: TreeController<AVLNode<KVP<Int, String>>>, file: File) {
        try {
            Files.createDirectories(file.toPath().parent)
        } catch (ex: SecurityException) {
            throw HandledIOException("Directory ${file.toPath().parent} cannot be created: no access", ex)
        }

        val jsonTree = JsonAVLTree(treeController.tree.root?.serialize(treeController))

        file.run {
            createNewFile()
            writeText(Json.encodeToString(jsonTree))
        }
    }

    fun importTree(file: File): TreeController<AVLNode<KVP<Int, String>>> {
        val json = try {
            file.readText()
        } catch (ex: FileNotFoundException) {
            throw HandledIOException("File ${file.toPath().fileName} not found: no access", ex)
        }

        val treeController = TreeController(AVLTree())
        val jsonTree = Json.decodeFromString<JsonAVLTree>(json)
        treeController.tree.root = jsonTree.root?.deserialize(treeController)
        return treeController

    }
}
