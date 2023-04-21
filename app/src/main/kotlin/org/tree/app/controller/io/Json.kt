package org.tree.app.controller.io

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.neo4j.driver.exceptions.value.Uncoercible
import org.tree.app.view.NodeView
import org.tree.binaryTree.AVLNode
import org.tree.binaryTree.KVP
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Files

@Serializable
private data class JsonAVLNode(
    val key: Int,
    val value: String?,
    val x: Double,
    val y: Double,
    val height: Int,
    val left: JsonAVLNode?,
    val right: JsonAVLNode?
)

@Serializable
private data class JsonAVLTree(
    val root: JsonAVLNode?
)
class Json {
    val dirPath = "json-saved"

    private fun NodeView<AVLNode<KVP<Int, String>>>.toJsonNode(): JsonAVLNode = JsonAVLNode(
        key = this.node.elem.key,
        value = this.node.elem.v,
        x = this.x,
        y = this.y,
        height = this.node.height,
        left = l?.toJsonNode(),
        right = r?.toJsonNode()
    )

    private fun JsonAVLNode.deserialize(): NodeView<AVLNode<KVP<Int, String>>> {
        val nv = NodeView(AVLNode(KVP(key, value)))
        nv.node.height = height
        nv.x = x
        nv.y = y
        nv.l = left?.deserialize()
        nv.r = right?.deserialize()

        return nv
    }

    fun exportTree(root: NodeView<AVLNode<KVP<Int, String>>>, file: File) {
        try {
            Files.createDirectories(file.toPath().parent)
        } catch (ex: SecurityException) {
            throw IOException("Directory ${file.toPath().parent} cannot be created: no access", ex)
        }
        val jsonTree = JsonAVLTree(root.toJsonNode())

        file.run {
            createNewFile()
            writeText(Json.encodeToString(jsonTree))
        }
    }

    fun importTree(file: File): NodeView<AVLNode<KVP<Int, String>>>? {
        val json = try {
            file.readText()
        } catch (_: FileNotFoundException) {
            return null
        }

        val jsonTree = Json.decodeFromString<JsonAVLTree>(json)
        return jsonTree.root?.deserialize()
    }

    fun cleanDataBase(fileName: String) {
        File(dirPath, "$fileName.json").delete()
    }
}
