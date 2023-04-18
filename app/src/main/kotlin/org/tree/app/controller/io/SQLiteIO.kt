package org.tree.app.controller.io

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.tree.app.view.NodeView
import org.tree.binaryTree.KVP
import org.tree.binaryTree.Node
import java.io.File
import java.io.IOException
import java.nio.file.Files

object Nodes : Table("Nodes") {
    val key = integer("key")
    val parentKey = integer("parentKey").nullable()
    val value = text("value")
    val x = float("x")
    val y = float("y")
}

class SQLiteIO {
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
            SchemaUtils.create(Nodes)
            parseNode(rootOfBinTree, null)
        }
    }

    private fun parseNode(
        currentNode: NodeView<Node<KVP<Int, String>>>,
        parentNode: NodeView<Node<KVP<Int, String>>>?
    ) {
        Nodes.insert {
            it[key] = currentNode.node.elem.key
            it[parentKey] = parentNode?.node?.elem?.key
            it[value] = currentNode.node.elem.v.toString()
            it[x] = currentNode.x.toFloat()
            it[y] = currentNode.y.toFloat()
        }
        val leftChild = currentNode.l
        if (leftChild != null) {
            parseNode(leftChild, currentNode)
        }
        val rightChild = currentNode.r
        if (rightChild != null) {
            parseNode(rightChild, currentNode)
        }
    }
}
