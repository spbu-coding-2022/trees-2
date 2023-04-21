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

    fun importAVLTree(fileName: String): NodeView<AVLNode<KVP<Int, String>>>? {  // when we have treeView, fun will be rewritten
        val json = try {
            File(dirPath, fileName).readText()
        } catch (_: FileNotFoundException) {
            return null
        }

        val jsonTree = Json.decodeFromString<JsonAVLTree>(json)
        return parseAVLNodes(jsonTree)
    }

    private class NodeAndKeys(
        val nv: NodeView<AVLNode<KVP<Int, String>>>,
        val lkey: Int?,
        val rkey: Int?
    )

    private fun parseAVLNodes(nodeAndKeysRecords: JsonAVLTree): NodeView<AVLNode<KVP<Int, String>>>? {
        val key2nk = mutableMapOf<Int, NodeAndKeys>()
        for (nkRecord in nodeAndKeysRecords.AVLTree) {
            try {
                val key = nkRecord?.key ?: throw IOException("Invalid nodes label in the database")
                val value = nkRecord.value
                val nv = NodeView(AVLNode(KVP(key, value)))

                nv.x = nkRecord.x
                nv.y = nkRecord.y
                nv.node.height = nkRecord.height

                val lkey = nkRecord.lkey
                val rkey = nkRecord.rkey
                key2nk[key] = NodeAndKeys(nv, lkey, rkey)
            } catch (ex: Uncoercible) {
                throw IOException("Invalid nodes label in the database", ex)
            }
        }
        val nks = key2nk.values.toTypedArray()
        if (nks.isEmpty()) { // if nodeAndKeysRecords was empty
            return null
        }

        for (nk in nks) {
            nk.lkey?.let {
                nk.nv.l = key2nk[it]?.nv
                key2nk.remove(it)
            }
            nk.nv.l?.let {
                nk.nv.node.left = it.node
            }

            nk.rkey?.let {
                nk.nv.r = key2nk[it]?.nv
                key2nk.remove(it)
            }
            nk.nv.r?.let {
                nk.nv.node.right = it.node
            }
        }
        if (key2nk.values.size != 1) {
            throw IOException("Found ${key2nk.values.size} nodes without parents in database, expected only 1 node")
        }
        val root = key2nk.values.first().nv
        return root
    }

    fun cleanDataBase(fileName: String) {
        File(dirPath, "$fileName.json").delete()
    }
}
