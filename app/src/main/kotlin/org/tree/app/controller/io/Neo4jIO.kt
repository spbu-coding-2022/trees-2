package org.tree.app.controller.io

import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.TransactionContext
import org.neo4j.driver.exceptions.SessionExpiredException
import org.tree.app.view.NodeView
import org.tree.binaryTree.KVP
import org.tree.binaryTree.RBNode
import java.io.Closeable
import java.io.IOException

class Neo4jIO() : Closeable {
    private var driver: Driver? = null

    private fun exportRBNode(
        nodeView: NodeView<RBNode<KVP<String, String>>>,
        tx: TransactionContext,
        isRoot: Boolean = false
    ) {
        val rootCase = if (isRoot) {
            ":ROOT"
        } else {
            ""
        }
        with(nodeView) {
            tx.run(
                "MERGE (n: RBNode" + rootCase + "{key : \"${node.elem.key}\", " +
                        "value: \"${node.elem.v ?: ""}\", " +
                        "x: $x, y: $y, " +
                        "isBlack: ${node.col == RBNode.Colour.BLACK}})"
            )
            l?.let {
                exportRBNode(it, tx)
                tx.run(
                    "MATCH" +
                            "  (p:RBNode {key: \"${node.elem.key}\"}), " +
                            "  (c:RBNode {key: \"${it.node.elem.key}\"}) " +
                            "MERGE (p)-[:LEFT_CHILD]->(c)"
                )
            }
            r?.let {
                exportRBNode(it, tx)
                tx.run(
                    "MATCH" +
                            "  (p:RBNode {key: \"${node.elem.key}\"}), " +
                            "  (c:RBNode {key: \"${it.node.elem.key}\"}) " +
                            "MERGE (p)-[:RIGHT_CHILD]->(c)"
                )
            }
        }
    }

    fun open(uri: String, username: String, password: String) {
        try {
            driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password))
        } catch (ex: IllegalArgumentException) {
            throw IOException("Wrong URI", ex)
        } catch (ex: SessionExpiredException) {
            throw IOException("Session failed, try restarting the app", ex)
        }
    }

    override fun close() {
        driver?.close()
    }
}
