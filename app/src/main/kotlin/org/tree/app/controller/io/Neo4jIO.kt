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
        val res: NodeView<RBNode<KVP<String, String>>> = session.executeRead { tx ->
            importRBNode(tx)
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

    private fun importRBNode(tx: TransactionContext): NodeView<RBNode<KVP<String, String>>> {

        val ret = tx.run(
            "MATCH (p: RBNode)" +
                    "OPTIONAL MATCH (p)-[:LEFT_CHILD]->(l: RBNode) " +
                    "OPTIONAL MATCH (p)-[:RIGHT_CHILD]->(r: RBNode) " +
                    "RETURN p.x AS x, p.y AS y, p.isBlack AS isBlack, p.key AS key, p.value AS value, " +
                    "   l.key AS lKey, r.key AS rKey"
        )

        return parseRBNode(ret)
    }

    private class NodeAndKeys(
        val nv: NodeView<RBNode<KVP<String, String>>>,
        val lkey: String?,
        val rkey: String?
    )

    private fun parseRBNode(ret: Result): NodeView<RBNode<KVP<String, String>>> {
        val st = mutableMapOf<String, NodeAndKeys>()
        for (rec in ret) {
            try {
                val key = rec["key"].asString()
                val value = rec["value"].asString()
                val nv = NodeView(RBNode(null, KVP(key, value)))

                nv.x = rec["x"].asDouble()
                nv.y = rec["y"].asDouble()

                val isBlack = rec["isBlack"].asBoolean()
                nv.node.col = if (isBlack) {
                    RBNode.Colour.BLACK
                } else {
                    RBNode.Colour.RED
                }

                val lkey = if (rec["lKey"].isNull) {
                    null
                } else {
                    rec["lKey"].asString()
                }
                val rkey = if (rec["rKey"].isNull) {
                    null
                } else {
                    rec["rKey"].asString()
                }

                st[key] = NodeAndKeys(nv, lkey, rkey)
            } catch (ex: Uncoercible) {
                throw IOException("Invalid nodes label in the database", ex)
            }
        }
        val a = st.values.toTypedArray()
        for (nk in a) {
            nk.lkey?.let {
                nk.nv.l = st[it]?.nv
                st.remove(it)
            }
            nk.nv.l?.let {
                nk.nv.node.left = it.node
                it.node.parent = nk.nv.node
            }

            nk.rkey?.let {
                nk.nv.r = st[it]?.nv
                st.remove(it)
            }
            nk.nv.r?.let {
                nk.nv.node.right = it.node
                it.node.parent = nk.nv.node
            }
        }
        if (st.values.size != 1) {
            throw IOException("Found ${st.values.size} nodes without parents in database, expected 1")
        }
        val root = st.values.first().nv
        println(root.node.elem.key)
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
