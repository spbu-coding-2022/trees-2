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

    fun exportRBTree(
        root: NodeView<RBNode<KVP<Int, String>>>,
        treeName: String = "Tree"
    ) {   // when we have treeView, fun will be rewritten
        val session = driver?.session() ?: throw IOException("Driver is not open")
        session.executeWrite { tx ->
            deleteTree(tx, treeName)
            tx.run(genExportRBNodes(root))
            tx.run(
                "MATCH (p: RBNode) " +
                        "MATCH (l: NewNode {key: p.lkey}) " +
                        "CREATE (p)-[:LEFT_CHILD]->(l) " +
                        "REMOVE p.lkey, l:NewNode"
            ) // connect parent and left child
            tx.run(
                "MATCH (p: RBNode) " +
                        "MATCH (r: NewNode {key: p.rkey}) " +
                        "CREATE (p)-[:RIGHT_CHILD]->(r) " +
                        "REMOVE p.rkey, r:NewNode"
            )// connect parent and right child

            tx.run(
                "MATCH (r: NewNode) " +
                        "CREATE (t: Tree {name: \"$treeName\"})-[:ROOT]->(r) " +
                        "REMOVE r:NewNode"
            )
        }
        session.close()
    }


    fun importRBTree(): NodeView<RBNode<KVP<Int, String>>>? {  // when we have treeView, fun will be rewritten
        val session = driver?.session() ?: throw IOException("Driver is not open")
        val res: NodeView<RBNode<KVP<Int, String>>>? = session.executeRead { tx ->
            importRBNodes(tx)
        }
        session.close()
        return res
    }

    private fun deleteTree(tx: TransactionContext, treeName: String) {
        tx.run(
            "MATCH (t: Tree {name: \"$treeName\"})" +
                    "MATCH (t)-[*]->(n:RBNode) " +
                    "DETACH DELETE t, n"
        )
    }

    private fun genExportRBNodes(root: NodeView<RBNode<KVP<Int, String>>>): String {
        val sb = StringBuilder()
        traverseExportRBNode(sb, root)
        return sb.toString()
    }

    private fun traverseExportRBNode(
        sb: StringBuilder,
        nodeView: NodeView<RBNode<KVP<Int, String>>>,
    ) {
        with(nodeView) {
            val lkey = l?.node?.elem?.key
            val lkeyString = if (lkey != null) {
                ", lkey: ${lkey} "
            } else {
                ""
            }
            val rkey = r?.node?.elem?.key
            val rkeyString = if (rkey != null) {
                ", rkey: ${r?.node?.elem?.key}"
            } else {
                ""
            }
            sb.append(
                "CREATE (:RBNode:NewNode {key : ${node.elem.key}, " +
                        "value: \"${node.elem.v ?: ""}\", " +
                        "x: $x, y: $y, " +
                        "isBlack: ${node.col == RBNode.Colour.BLACK}" +
                        lkeyString + rkeyString +
                        "})"
            ) // save node (lkey and rkey are needed for connection later)
            l?.let {
                traverseExportRBNode(sb, it)
            }
            r?.let {
                traverseExportRBNode(sb, it)
            }
        }
    }

    private fun importRBNodes(tx: TransactionContext): NodeView<RBNode<KVP<Int, String>>>? {
        val nodeAndKeysRecords = tx.run(
            "MATCH (p: RBNode)" +
                    "OPTIONAL MATCH (p)-[:LEFT_CHILD]->(l: RBNode) " +
                    "OPTIONAL MATCH (p)-[:RIGHT_CHILD]->(r: RBNode) " +
                    "RETURN p.x AS x, p.y AS y, p.isBlack AS isBlack, p.key AS key, p.value AS value, " +
                    "   l.key AS lKey, r.key AS rKey"
        ) // for all nodes get their properties + keys of their children
        return parseRBNodes(nodeAndKeysRecords)
    }

    private class NodeAndKeys(
        val nv: NodeView<RBNode<KVP<Int, String>>>,
        val lkey: Int?,
        val rkey: Int?
    )

    private fun parseRBNodes(nodeAndKeysRecords: Result): NodeView<RBNode<KVP<Int, String>>>? {
        val key2nk = mutableMapOf<Int, NodeAndKeys>()
        for (nkRecord in nodeAndKeysRecords) {
            try {
                val key = nkRecord["key"].asInt()
                val value = nkRecord["value"].asString()
                val nv = NodeView(RBNode(null, KVP(key, value)))

                nv.x = nkRecord["x"].asDouble()
                nv.y = nkRecord["y"].asDouble()

                val isBlack = nkRecord["isBlack"].asBoolean()
                nv.node.col = if (isBlack) {
                    RBNode.Colour.BLACK
                } else {
                    RBNode.Colour.RED
                }

                val lkey = if (nkRecord["lKey"].isNull) {
                    null
                } else {
                    nkRecord["lKey"].asInt()
                }
                val rkey = if (nkRecord["rKey"].isNull) {
                    null
                } else {
                    nkRecord["rKey"].asInt()
                }

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
                it.node.parent = nk.nv.node
            }

            nk.rkey?.let {
                nk.nv.r = key2nk[it]?.nv
                key2nk.remove(it)
            }
            nk.nv.r?.let {
                nk.nv.node.right = it.node
                it.node.parent = nk.nv.node
            }
        }
        if (key2nk.values.size != 1) {
            throw IOException("Found ${key2nk.values.size} nodes without parents in database, expected only 1 node")
        }
        val root = key2nk.values.first().nv
        return root
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
