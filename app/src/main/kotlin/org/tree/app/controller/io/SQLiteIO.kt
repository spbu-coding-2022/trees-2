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
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import org.tree.binaryTree.KVP
import org.tree.binaryTree.Node
import org.tree.binaryTree.trees.BinSearchTree
import java.io.File
import java.nio.file.Files
import java.sql.SQLException

object Nodes : IntIdTable() {
    val key = integer("key")
    val parentKey = integer("parentKey").nullable()
    val value = text("value", eagerLoading = true)
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
    fun exportTree(treeController: TreeController<Node<KVP<Int, String>>>, file: File) {
        try {
            Files.createDirectories(file.toPath().parent)
        } catch (ex: SecurityException) {
            throw HandledIOException("Directory ${file.toPath().parent} cannot be created: no access", ex)
        }
        Database.connect("jdbc:sqlite:${file.path}", "org.sqlite.JDBC")
        transaction {
            try {
                SchemaUtils.drop(Nodes)
                SchemaUtils.create(Nodes)
            } catch (ex: ExposedSQLException) {
                throw HandledIOException("File is not a database", ex)
            }
            val root = treeController.tree.root
            if (root != null) {
                parseNodesForExport(root, null, treeController)
            }
        }
    }

    fun importTree(file: File): TreeController<Node<KVP<Int, String>>> {
        val treeController = TreeController(BinSearchTree())
        val setOfNodes = getSetOfNodesFromDB(file)
        val amountOfNodes = setOfNodes.count()
        if (amountOfNodes > 0) {
            parseRootForImport(setOfNodes, treeController)
        }
        return treeController
    }

    private fun parseNodesForExport(
        curNode: Node<KVP<Int, String>>,
        parNode: Node<KVP<Int, String>>?,
        treeController: TreeController<Node<KVP<Int, String>>>
    ) {
        InstanceOfNode.new {
            key = curNode.element.key
            parentKey = parNode?.element?.key
            value = curNode.element.v.toString()
            x = treeController.nodes[curNode]?.x?.value ?: 0
            y = treeController.nodes[curNode]?.y?.value ?: 0
        }
        val leftChild = curNode.left
        if (leftChild != null) {
            parseNodesForExport(leftChild, curNode, treeController)
        }
        val rightChild = curNode.right
        if (rightChild != null) {
            parseNodesForExport(rightChild, curNode, treeController)
        }
    }

    private fun getSetOfNodesFromDB(file: File): MutableSet<InstanceOfNode> {
        Database.connect("jdbc:sqlite:${file.path}", "org.sqlite.JDBC")
        return transaction {
            try {
                if (Nodes.exists()) {
                    InstanceOfNode.all().toMutableSet()
                } else {
                    throw HandledIOException("Database without a Nodes table")
                }
            } catch (ex: SQLException) {
                throw HandledIOException("File is not a SQLite database", ex)
            }
        }
    }

    private fun parseRootForImport(
        setOfNodes: MutableSet<InstanceOfNode>,
        treeController: TreeController<Node<KVP<Int, String>>>
    ) {
        try {
            val node = setOfNodes.elementAt(0)
            setOfNodes.remove(node)
            val parsedKey = node.key
            val parsedValue = node.value
            val parsedX = node.x
            val parsedY = node.y
            val bst = treeController.tree
            bst.insert(KVP(parsedKey, parsedValue))
            val root = bst.root
            if (root != null) {
                addCoordinatesToNode(root, parsedX, parsedY, treeController)
                parseNodesForImport(setOfNodes, root, treeController)
                if (setOfNodes.isNotEmpty()) {
                    throw HandledIOException("Incorrect binary tree: there are at least two left/right children of some node")
                }
            }
        } catch (ex: NumberFormatException) {
            throw HandledIOException(
                "Node keys must be integers, value must be string and coordinates must be doubles",
                ex
            )
        }
    }

    private fun parseNodesForImport(
        setOfNodes: MutableSet<InstanceOfNode>,
        curNode: Node<KVP<Int, String>>,
        treeController: TreeController<Node<KVP<Int, String>>>
    ) {
        if (setOfNodes.size == 0) {
            return
        }
        val nodes = setOfNodes.elementAt(0)
        val parsedKey = nodes.key
        val parsedParentKey: Int =
            nodes.parentKey ?: throw HandledIOException("Incorrect binary tree: there are at least 2 roots")
        val parsedValue = nodes.value
        val parsedX = nodes.x
        val parsedY = nodes.y
        if (parsedKey == parsedParentKey) throw HandledIOException("Child with key = ${curNode.element.key} is parent for himself")
        if (parsedParentKey == curNode.element.key) {
            val newNode = Node(KVP(parsedKey, parsedValue))
            addCoordinatesToNode(newNode, parsedX, parsedY, treeController)
            setOfNodes.remove(nodes)
            if (parsedKey < parsedParentKey) {
                if (curNode.left != null) throw HandledIOException("Incorrect binary tree: there are at least two left children of node with key = ${curNode.element.key}")
                curNode.left = newNode
                val leftChild = curNode.left
                if (leftChild != null) {
                    parseNodesForImport(setOfNodes, leftChild, treeController)
                }
                parseNodesForImport(setOfNodes, curNode, treeController)
            } else { // When parsedKey is greater than parsedParentKey
                if (curNode.right != null) throw HandledIOException("Incorrect binary tree: there are at least two right children of node with key = ${curNode.element.key}")
                curNode.right = newNode
                val rightChild = curNode.right
                if (rightChild != null) {
                    parseNodesForImport(setOfNodes, rightChild, treeController)
                }
            }
        }
    }

    private fun addCoordinatesToNode(
        node: Node<KVP<Int, String>>,
        x: Int, y: Int, treeController: TreeController<Node<KVP<Int, String>>>
    ) {
        treeController.nodes[node] =
            NodeExtension(mutableStateOf(x), mutableStateOf(y), treeController.getNodeCol(node))
    }
}
