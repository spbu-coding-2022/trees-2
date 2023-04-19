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

@Serializable
private data class JsonAVLNode(
    val key: Int,
    val value: String?,
    val x: Double,
    val y: Double,
    val height: Int,
    val lkey: Int?,
    val rkey: Int?
)

@Serializable
private data class JsonAVLTree(
    val AVLTree: Array<JsonAVLNode?>
)
class Json {
    val dirPath = "json-saved"

    fun exportAVLTree(root: NodeView<AVLNode<KVP<Int, String>>>) {
        val sb = StringBuilder()
        sb.append("{\"AVLTree\":[")
        traverseExportAVLNode(sb, root)
        sb.append("]}")
        File(dirPath).mkdirs()
        File(dirPath, "output.json").run {
            createNewFile()
            writeText(sb.toString())
        }
    }

    private fun traverseExportAVLNode(
        sb: StringBuilder,
        nodeView: NodeView<AVLNode<KVP<Int, String>>>,
    ) {

        with(nodeView) {
            val json = JsonAVLNode(
                key = node.elem.key,
                value = node.elem.v,
                x = x,
                y = y,
                height = node.height,
                lkey = l?.node?.elem?.key,
                rkey = r?.node?.elem?.key
            )

            sb.append(Json.encodeToString(json))

            l?.let {
                sb.append(",")
                traverseExportAVLNode(sb, it)
            }
            r?.let {
                sb.append(",")
                traverseExportAVLNode(sb, it)
            }
        }

    }
}
