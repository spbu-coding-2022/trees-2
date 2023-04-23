package org.tree.app.controller.io

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.tree.app.view.NodeView
import org.tree.binaryTree.KVP
import org.tree.binaryTree.Node
import java.io.File
import java.io.IOException
import java.nio.file.Files

object Nodes : IntIdTable() {
    val key = integer("key")
    val parentKey = integer("parentKey").nullable()
    val value = text("value")
    val x = float("x")
    val y = float("y")
}

class InstanceOfNode(id: EntityID<Int>) : IntEntity(id) { // A separate row with data in SQLite
    companion object : IntEntityClass<InstanceOfNode>(Nodes)

    var key by Nodes.key
    var parentKey by Nodes.parentKey
    var value by Nodes.value
    var x by Nodes.x
    var y by Nodes.y
}

class SQLiteIO {
    fun importTree(file: File): NodeView<Node<KVP<Int, String>>>? {
        Database.connect("jdbc:sqlite:${file.path}", "org.sqlite.JDBC")
        var root: NodeView<Node<KVP<Int, String>>>? = null
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                val amountOfNodes = Nodes.selectAll().count()
                if (amountOfNodes > 0) {
                    root = parseRootForImport()
                    root?.let { exportTree(it, file) }
                }
            }
        } catch (ex: ExposedSQLException) {
            throw IOException("File is not a database", ex)
        }
        return root
    }

    fun exportTree(rootOfBinTree: NodeView<Node<KVP<Int, String>>>, file: File) {
        try {
            Files.createDirectories(file.toPath().parent)
        } catch (ex: SecurityException) {
            throw IOException("Directory ${file.toPath().parent} cannot be created: no access", ex)
        }

        Database.connect("jdbc:sqlite:${file.path}", "org.sqlite.JDBC")

        transaction {
            addLogger(StdOutSqlLogger)
            try {
                SchemaUtils.drop(Nodes)
                SchemaUtils.create(Nodes)
            } catch (ex: ExposedSQLException) {
                throw IOException("File is not a database", ex)
            }
            parseNodesForExport(rootOfBinTree, null)
        }
    }

    private fun parseNodesForExport(
        curNode: NodeView<Node<KVP<Int, String>>>,
        parNode: NodeView<Node<KVP<Int, String>>>?
    ) {
        Nodes.insert {
            it[key] = curNode.node.elem.key
            it[parentKey] = parNode?.node?.elem?.key
            it[value] = curNode.node.elem.v.toString()
            it[x] = curNode.x.toFloat()
            it[y] = curNode.y.toFloat()
        }
        val leftChild = curNode.l
        if (leftChild != null) {
            parseNodesForExport(leftChild, curNode)
        }
        val rightChild = curNode.r
        if (rightChild != null) {
            parseNodesForExport(rightChild, curNode)
        }
    }

    private fun parseRootForImport(): NodeView<Node<KVP<Int, String>>>? {
        try {
            for (node in Nodes.selectAll()) {
                val parsedParentKey = node[Nodes.parentKey]
                if (parsedParentKey != null) {
                    continue
                }
                val parsedKey = node[Nodes.key]
                val parsedValue = node[Nodes.value]
                val parsedX: Double = node[Nodes.x].toDouble()
                val parsedY: Double = node[Nodes.y].toDouble()
                val nodeView = NodeView(Node(KVP(parsedKey, parsedValue)))
                nodeView.x = parsedX
                nodeView.y = parsedY
                Nodes.deleteWhere { parentKey eq null }
                parseNodesForImport(nodeView)
                return nodeView
            }
        } catch (ex: NumberFormatException) {
            throw IOException(
                "Node keys must be integers, value must be string and coordinates must be doubles",
                ex
            )
        }
        return null
    }

    private fun parseNodesForImport(
        curNode: NodeView<Node<KVP<Int, String>>>
    ): NodeView<Node<KVP<Int, String>>> {
        for (node in Nodes.selectAll()) {
            val parsedKey = node[Nodes.key]
            val parsedParentKey: Int = node[Nodes.parentKey] ?: throw IOException("Incorrect binary tree")
            val parsedValue = node[Nodes.value]
            val parsedX: Double = node[Nodes.x].toDouble()
            val parsedY: Double = node[Nodes.y].toDouble()
            if (parsedParentKey == curNode.node.elem.key){
                Nodes.deleteWhere { key eq parsedKey }
                val newNode: NodeView<Node<KVP<Int, String>>> = NodeView(Node(KVP(parsedKey, parsedValue)))
                newNode.x = parsedX
                newNode.y = parsedY
                if (parsedKey < parsedParentKey) {
                    curNode.l = newNode
                    val leftChild = curNode.l
                    if (leftChild != null) {
                        parseNodesForImport(leftChild)
                    }
                    if (curNode.r == null){
                        parseNodesForImport(curNode)
                        break
                    }
                } else if (parsedKey > parsedParentKey) {
                    curNode.r = newNode
                    val rightChild = curNode.r
                    if (rightChild != null) {
                        parseNodesForImport(rightChild)
                    }
                    if (curNode.l == null){
                        parseNodesForImport(curNode)
                        break
                    }
                } else {
                    throw IOException("Incorrect binary tree")
                }
            }
        }
        return curNode
    }

    private fun addCoordinatesForNodeView(nodeView: NodeView<Node<KVP<Int, String>>>, x: Double, y: Double) {
        nodeView.x = x
        nodeView.y = y
    }
}
