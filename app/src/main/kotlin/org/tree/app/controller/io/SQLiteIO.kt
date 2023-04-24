package org.tree.app.controller.io

import NodeExtension
import TreeController
import androidx.compose.runtime.mutableStateOf
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
import org.tree.binaryTree.KVP
import org.tree.binaryTree.Node
import org.tree.binaryTree.trees.BinSearchTree
import java.io.File
import java.io.IOException
import java.nio.file.Files

object Nodes : IntIdTable() {
    val key = integer("key")
    val parentKey = integer("parentKey").nullable()
    val value = text("value")
    val x = integer("x")
    val y = integer("y")
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
    private lateinit var treeController : TreeController<Node<KVP<Int, String>>>
    fun importTree(file: File): TreeController<Node<KVP<Int, String>>> {
        Database.connect("jdbc:sqlite:${file.path}", "org.sqlite.JDBC")
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                val setOfNodes = InstanceOfNode.all().toMutableSet()
                val amountOfNodes = setOfNodes.count()
                if (amountOfNodes > 0) {
                    treeController = TreeController(BinSearchTree())
                    parseRootForImport(setOfNodes)
                }
            }
        } catch (ex: ExposedSQLException) {
            throw IOException("File is not a database", ex)
        }
        return treeController
    }

    fun exportTree(treeController_: TreeController<Node<KVP<Int, String>>>, file: File) {
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
            treeController = treeController_
            val root = treeController.tree.root
            if (root != null) {
                parseNodesForExport(root, null)
            }
        }
    }


    private fun parseRootForImport(
        setOfNodes: MutableSet<InstanceOfNode>
    ) {
        try {
            val node = setOfNodes.elementAt(0)
            setOfNodes.remove(node)
            val parsedKey = node.key
            val parsedValue = node.value
            val parsedX = node.x
            val parsedY = node.y
            val bst = treeController.tree
            amountOfNodesToHandle = setOfNodes.count()
            bst.insert(KVP(parsedKey, parsedValue))
            val root = bst.root
            if (root != null) {
                addCoordinatesToNode(root, parsedX, parsedY)
                parseNodesForImport(setOfNodes, root)
            }
        } catch (ex: NumberFormatException) {
            throw IOException(
                "Node keys must be integers, value must be string and coordinates must be doubles",
                ex
            )
        }
    }

    private fun parseNodesForImport(
        setOfNodes: MutableSet<InstanceOfNode>,
        curNode: Node<KVP<Int, String>>
    ) {
        if (amountOfNodesToHandle <= 0) {
            return
        }
        val nodes = setOfNodes.elementAt(0)
        val parsedKey = nodes.key
        val parsedParentKey: Int = nodes.parentKey ?: throw IOException("Incorrect binary tree")
        val parsedValue = nodes.value
        val parsedX = nodes.x
        val parsedY = nodes.y
        if (parsedParentKey == curNode.elem.key) {
            val newNode = Node(KVP(parsedKey, parsedValue))
            addCoordinatesToNode(newNode, parsedX, parsedY)
            setOfNodes.remove(nodes)
            amountOfNodesToHandle--
            if (parsedKey < parsedParentKey) {
                curNode.left = newNode
                val leftChild = curNode.left
                if (leftChild != null) {
                    parseNodesForImport(setOfNodes, leftChild)
                }
                parseNodesForImport(setOfNodes, curNode)
            } else if (parsedKey > parsedParentKey) {
                curNode.right = newNode
                val rightChild = curNode.right
                if (rightChild != null) {
                    parseNodesForImport(setOfNodes, rightChild)
                }
            } else {
                throw IOException("Incorrect binary tree")
            }
        }
    }

    private fun parseNodesForExport(
        curNode: Node<KVP<Int, String>>,
        parNode: Node<KVP<Int, String>>?
    ) {
        InstanceOfNode.new {
            key = curNode.elem.key
            parentKey = parNode?.elem?.key
            value = curNode.elem.v.toString()
            x = treeController.nodes[curNode]?.x?.value ?: 0
            y = treeController.nodes[curNode]?.y?.value ?: 0
        }
        val leftChild = curNode.left
        if (leftChild != null) {
            parseNodesForExport(leftChild, curNode)
        }
        val rightChild = curNode.right
        if (rightChild != null) {
            parseNodesForExport(rightChild, curNode)
        }
    }

    private fun addCoordinatesToNode(
        node: Node<KVP<Int, String>>,
        x: Int, y: Int
    ) {
        treeController.nodes[node] = NodeExtension(mutableStateOf(x), mutableStateOf(y))
    }
}
