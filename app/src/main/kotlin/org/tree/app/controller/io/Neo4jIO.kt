package org.tree.app.controller.io

import org.neo4j.driver.*
import org.neo4j.driver.exceptions.SessionExpiredException
import org.neo4j.driver.exceptions.value.Uncoercible
import org.tree.app.view.NodeView
import org.tree.binaryTree.KVP
import org.tree.binaryTree.RBNode
import java.io.Closeable
import java.io.IOException

class Neo4jIO() : Closeable {
    private var driver: Driver? = null

    fun exportRBTree(root: NodeView<RBNode<KVP<String, String>>>) {   // when we have treeView, fun will be rewritten
        val session = driver?.session() ?: throw IOException("Driver is not open")
        session.executeWrite { tx ->
            cleanDataBase(tx)
            tx.run(genExportRBNodes(root))
            tx.run(
                "MATCH (p: RBNode) " +
                        "MATCH (l: RBNode {key: p.lkey}) " +
                        "CREATE (p)-[:LEFT_CHILD]->(l) " +
                        "REMOVE p.lkey"
            ) // connect parent and left child
            tx.run(
                "MATCH (p: RBNode) " +
                        "MATCH (r: RBNode {key: p.rkey}) " +
                        "CREATE (p)-[:RIGHT_CHILD]->(r) " +
                        "REMOVE p.rkey"
            )// connect parent and right child
        }
        session.close()
    }


    fun importRBTree(): NodeView<RBNode<KVP<String, String>>> {  // when we have treeView, fun will be rewritten
        val session = driver?.session() ?: throw IOException("Driver is not open")
        var res: NodeView<RBNode<KVP<String, String>>> = session.executeRead { tx ->
            importRBNode(null, tx)
        }
        session.close()
        return res
    }

    private fun cleanDataBase(tx: TransactionContext) {
        tx.run("MATCH (n: RBNode) DETACH DELETE n")
    }

    private fun genExportRBNodes(root: NodeView<RBNode<KVP<String, String>>>): String {
        val sb = StringBuilder()
        traverseExportRBNode(sb, root)
        return sb.toString()
    }

    private fun traverseExportRBNode(
        sb: StringBuilder,
        nodeView: NodeView<RBNode<KVP<String, String>>>,
    ) {
        with(nodeView) {
            sb.append(
                "CREATE (:RBNode {key : \"${node.elem.key}\", " +
                        "value: \"${node.elem.v ?: ""}\", " +
                        "x: $x, y: $y, " +
                        "isBlack: ${node.col == RBNode.Colour.BLACK}, " +
                        "lkey: \"${l?.node?.elem?.key ?: ""}\", " +
                        "rkey: \"${r?.node?.elem?.key ?: ""}\"}) "
            )
            l?.let {
                traverseExportRBNode(sb, it)
            }
            r?.let {
                traverseExportRBNode(sb, it)
            }
        }
    }

    private fun importRBNode(key: String?, tx: TransactionContext): NodeView<RBNode<KVP<String, String>>> {
        val matchStr = if (key == null) {
            "MATCH (p: ROOT) "
        } else {
            "MATCH (p: RBNode {key: \"${key}\"})"
        }
        val ret = tx.run(
            matchStr +
                    "OPTIONAL MATCH (p)-[:LEFT_CHILD]->(l: RBNode) " +
                    "OPTIONAL MATCH (p)-[:RIGHT_CHILD]->(r: RBNode) " +
                    "RETURN p.x AS x, p.y AS y, p.isBlack AS isBlack, p.key AS key, p.value AS value, " +
                    "   l.key AS lKey, r.key AS rKey"
        )
        val rec = ret.next()
        val res = parseRBNode(rec)
        if (!(rec["lKey"].isNull)) {
            val lKey: String
            try {
                lKey = rec["lKey"].asString()
            } catch (ex: Uncoercible) {
                throw IOException("Invalid nodes label in the database", ex)
            }
            res.l = importRBNode(lKey, tx)
            res.l?.let {
                res.node.left = it.node
                it.node.parent = res.node
            }
        }
        if (!(rec["rKey"].isNull)) {
            val rKey: String
            try {
                rKey = rec["rKey"].asString()
            } catch (ex: Uncoercible) {
                throw IOException("Invalid nodes label in the database", ex)
            }
            res.r = importRBNode(rKey, tx)
            res.r?.let {
                res.node.right = it.node
                it.node.parent = res.node
            }
        }
        return res
    }

    private fun parseRBNode(rec: Record): NodeView<RBNode<KVP<String, String>>> {
        val res: NodeView<RBNode<KVP<String, String>>>

        try {
            val key = rec["key"].asString()
            val value = rec["value"].asString()
            res = NodeView(RBNode(null, KVP(key, value)))

            res.x = rec["x"].asDouble()
            res.y = rec["y"].asDouble()

            val isBlack = rec["isBlack"].asBoolean()
            res.node.col = if (isBlack) {
                RBNode.Colour.BLACK
            } else {
                RBNode.Colour.RED
            }
        } catch (ex: Uncoercible) {
            throw IOException("Invalid nodes label in the database", ex)
        }
        return res
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
