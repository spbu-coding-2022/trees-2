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
class Json {
    private lateinit var treeController : TreeController<AVLNode<KVP<Int, String>>>

    private fun AVLNode<KVP<Int, String>>.serialize(): JsonAVLNode{
    return JsonAVLNode(
        key = this.elem.key,
        value = this.elem.v,
        x = treeController.nodes[this]?.x?.value ?: 0,
        y = treeController.nodes[this]?.y?.value ?: 0,
        height = this.height,
        left = this.left?.serialize(),
        right = this.right?.serialize()
    )
    }

    private fun JsonAVLNode.deserialize(): AVLNode<KVP<Int, String>> {
        val nv = AVLNode(KVP(key, value))
        nv.height = height
        addCoordinatesToNode(nv, x, y)
        nv.left = left?.deserialize()
        nv.right = right?.deserialize()
        return nv
    }

    private fun addCoordinatesToNode(
        node: AVLNode<KVP<Int, String>>,
        x: Int, y: Int
    ) {
        treeController.nodes[node] = NodeExtension(mutableStateOf(x), mutableStateOf(y))
    }

    fun exportTree(treeController_: TreeController<AVLNode<KVP<Int, String>>>, file: File) {
        try {
            Files.createDirectories(file.toPath().parent)
        } catch (ex: SecurityException) {
            throw HandledIOException("Directory ${file.toPath().parent} cannot be created: no access", ex)
        }

        treeController = treeController_
        val jsonTree = JsonAVLTree(treeController.tree.root?.serialize())

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

        treeController = TreeController(AVLTree())
        val jsonTree = Json.decodeFromString<JsonAVLTree>(json)
        treeController.tree.root = jsonTree.root?.deserialize()
        return treeController

    }

    fun cleanDataBase(file: File) {
        file.delete()
    }
}
