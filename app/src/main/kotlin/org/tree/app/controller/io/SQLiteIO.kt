package org.tree.app.controller.io

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
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
    private var amountOfNodesToHandle = 0
    fun importTree(file: File): NodeView<Node<KVP<Int, String>>>? {
        Database.connect("jdbc:sqlite:${file.path}", "org.sqlite.JDBC")
        var root: NodeView<Node<KVP<Int, String>>>? = null
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                val amountOfNodes = InstanceOfNode.all().count()
                if (amountOfNodes > 0) {
                    val setOfNodes = InstanceOfNode.all().toMutableSet()
                    root = parseRootForImport(setOfNodes)
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


    private fun parseRootForImport(
        setOfNodeView: MutableSet<InstanceOfNode>
    ): NodeView<Node<KVP<Int, String>>> {
        try {
            val nodeView = setOfNodeView.elementAt(0)
            setOfNodeView.remove(nodeView)
            val parsedKey = nodeView.key
            val parsedValue = nodeView.value
            val parsedX: Double = nodeView.x.toDouble()
            val parsedY: Double = nodeView.y.toDouble()
            val newNodeView = NodeView(Node(KVP(parsedKey, parsedValue)))
            addCoordinatesForNodeView(newNodeView, parsedX, parsedY)
            newNodeView.node = Node(KVP(parsedKey, parsedValue))
            amountOfNodesToHandle = setOfNodeView.count()
            parseNodesForImport(setOfNodeView, newNodeView)
            return newNodeView
        } catch (ex: NumberFormatException) {
            throw IOException(
                "Node keys must be integers, value must be string and coordinates must be doubles",
                ex
            )
        }
    }

    private fun parseNodesForImport(
        setOfNodeView: MutableSet<InstanceOfNode>,
        curNode: NodeView<Node<KVP<Int, String>>>
    ) {
        if (amountOfNodesToHandle <= 0) {
            return
        }
        val nodeView = setOfNodeView.elementAt(0)
        val parsedKey = nodeView.key
        val parsedParentKey: Int = nodeView.parentKey ?: throw IOException("Incorrect binary tree")
        val parsedValue = nodeView.value
        val parsedX: Double = nodeView.x.toDouble()
        val parsedY: Double = nodeView.y.toDouble()
        if (parsedParentKey == curNode.node.elem.key) {
            val newNode = Node(KVP(parsedKey, parsedValue))
            val newNodeView = NodeView(newNode)
            addCoordinatesForNodeView(newNodeView, parsedX, parsedY)
            setOfNodeView.remove(nodeView)
            amountOfNodesToHandle--
            if (parsedKey < parsedParentKey) {
                curNode.node.left = newNode
                curNode.l = newNodeView
                val leftChild = curNode.l
                if (leftChild != null) {
                    parseNodesForImport(setOfNodeView, leftChild)
                }
                parseNodesForImport(setOfNodeView, curNode)
            } else if (parsedKey > parsedParentKey) {
                curNode.node.right = newNode
                curNode.r = newNodeView
                val rightChild = curNode.r
                if (rightChild != null) {
                    parseNodesForImport(setOfNodeView, rightChild)
                }
            } else {
                throw IOException("Incorrect binary tree")
            }
        }
    }

    private fun parseNodesForExport(
        curNode: NodeView<Node<KVP<Int, String>>>,
        parNode: NodeView<Node<KVP<Int, String>>>?
    ) {
        InstanceOfNode.new {
            key = curNode.node.elem.key
            parentKey = parNode?.node?.elem?.key
            value = curNode.node.elem.v.toString()
            x = curNode.x.toFloat()
            y = curNode.y.toFloat()
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

    private fun addCoordinatesForNodeView(nodeView: NodeView<Node<KVP<Int, String>>>, x: Double, y: Double) {
        nodeView.x = x
        nodeView.y = y
    }
}
