package org.tree.app.controller.io

import NodeExtension
import TreeController
import androidx.compose.runtime.mutableStateOf
import org.neo4j.driver.*
import org.neo4j.driver.exceptions.AuthenticationException
import org.neo4j.driver.exceptions.ClientException
import org.neo4j.driver.exceptions.ServiceUnavailableException
import org.neo4j.driver.exceptions.SessionExpiredException
import org.neo4j.driver.exceptions.value.Uncoercible
import org.tree.binaryTree.KVP
import org.tree.binaryTree.RBNode
import org.tree.binaryTree.trees.RBTree
import java.io.Closeable
import java.io.IOException

class Neo4jIO() : Closeable {
    private var driver: Driver? = null

    private companion object {
        // Labels
        const val RBNODE = "RBNode"
        const val TREE = "Tree"
        const val NEW_NODE = "NewNode"

        // Links
        const val ROOT = "ROOT"
        const val LCHILD = "LEFT_CHILD"
        const val RCHILD = "RIGHT_CHILD"
    }

    fun exportRBTree(
        treeController: TreeController<RBNode<KVP<Int, String>>>,
        treeName: String = "Tree"
    ) {   // when we have treeView, fun will be rewritten
        val session = driver?.session() ?: throw IOException("Driver is not open")
        val root = treeController.tree.root
        handleTransactionException {
            session.executeWrite { tx ->
                deleteTree(tx, treeName)
                exportRBNode(tx, root, treeController.nodes)
                tx.run(
                    "MATCH (p: $RBNODE) " +
                            "MATCH (l: $NEW_NODE {key: p.lkey}) " +
                            "CREATE (p)-[: $LCHILD]->(l) " +
                            "REMOVE p.lkey, l: $NEW_NODE"
                ) // connect parent and left child
                tx.run(
                    "MATCH (p: $RBNODE) " +
                            "MATCH (r: $NEW_NODE {key: p.rkey}) " +
                            "CREATE (p)-[: $RCHILD]->(r) " +
                            "REMOVE p.rkey, r: $NEW_NODE"
                )// connect parent and right child

                tx.run(
                    "CREATE (t: $TREE {name: \$treeName})", mutableMapOf(
                        "treeName" to treeName
                    ) as Map<String, Any>?
                ) // create tree label

                tx.run(
                    "MATCH (r: $NEW_NODE) " +
                            "MATCH (t: $TREE {name: \$treeName}) " +
                            "CREATE (t)-[:$ROOT]->(r) " +
                            "REMOVE r:$NEW_NODE",
                    mutableMapOf(
                        "treeName" to treeName
                    ) as Map<String, Any>?
                )// connect tree and root

            }
        }
        session.close()
    }


    fun importRBTree(treeName: String = "Tree"): TreeController<RBNode<KVP<Int, String>>> {  // when we have treeView, fun will be rewritten
        val session = driver?.session() ?: throw IOException("Driver is not open")
        val res: TreeController<RBNode<KVP<Int, String>>> =
            handleTransactionException {
                session.executeRead { tx ->
                    val tree = RBTree<KVP<Int, String>>()
                    val treeController = TreeController(tree)
                    importRBNodes(tx, treeController, treeName)
                    treeController
                }
            }
        session.close()
        return res
    }

    fun removeTree(treeName: String = "Tree") {
        val session = driver?.session() ?: throw IOException("Driver is not open")
        handleTransactionException {
            session.executeWrite { tx ->
                deleteTree(tx, treeName)
            }
        }
        session.close()
    }

    fun getTreesNames(): MutableList<String> {
        val session = driver?.session() ?: throw IOException("Driver is not open")
        val res: MutableList<String> = handleTransactionException {
            session.executeRead { tx ->
                val nameRecords = tx.run("MATCH (t: $TREE) RETURN t.name AS name")
                parseNames(nameRecords)
            }
        }
        session.close()
        return res
    }

    private fun deleteTree(tx: TransactionContext, treeName: String) {
        tx.run(
            "MATCH (t: $TREE {name: \$treeName})" +
                    "OPTIONAL MATCH (t)-[*]->(n:$RBNODE) " +
                    "DETACH DELETE t, n",
            mutableMapOf(
                "treeName" to treeName
            ) as Map<String, Any>?
        ) // delete tree and all its nodes
    }

    private fun exportRBNode(
        tx: TransactionContext,
        curNode: RBNode<KVP<Int, String>>?,
        nodes: MutableMap<RBNode<KVP<Int, String>>, NodeExtension>
    ) {
        if (curNode != null) {
            val lkey = curNode.left?.elem?.key
            val rkey = curNode.right?.elem?.key
            val ext = nodes[curNode]
            if (ext == null) {
                throw IOException("Can't find coordinates for node with key ${curNode.elem.key}")
            } else {
                tx.run(
                    "CREATE (:$RBNODE:$NEW_NODE {key : \$key, " +
                            "value: \$value, " +
                            "x: \$x, y: \$y, " +
                            "isBlack: \$isBlack, " +
                            "lkey: \$lkey, " +
                            "rkey: \$rkey" +
                            "})",
                    mutableMapOf(
                        "key" to curNode.elem.key,
                        "value" to (curNode.elem.v ?: ""),
                        "x" to ext.x.value, "y" to ext.y.value,
                        "isBlack" to (curNode.col == RBNode.Colour.BLACK),
                        "lkey" to lkey,
                        "rkey" to rkey
                    ) as Map<String, Any>?
                )
                exportRBNode(tx, curNode.left, nodes)
                exportRBNode(tx, curNode.right, nodes)
            }
        }
    }

    private fun importRBNodes(
        tx: TransactionContext,
        treeController: TreeController<RBNode<KVP<Int, String>>>,
        treeName: String
    ) {
        val nodeAndKeysRecords = tx.run(
            "MATCH (:$TREE {name: \$treeName})-[*]->(p: $RBNODE)" +
                    "OPTIONAL MATCH (p)-[: $LCHILD]->(l: $RBNODE) " +
                    "OPTIONAL MATCH (p)-[: $RCHILD]->(r: $RBNODE) " +
                    "RETURN p.x AS x, p.y AS y, p.isBlack AS isBlack, p.key AS key, p.value AS value, " +
                    "   l.key AS lKey, r.key AS rKey",
            mutableMapOf(
                "treeName" to treeName
            ) as Map<String, Any>?
        ) // for all nodes get their properties + keys of their children
        return parseRBNodes(nodeAndKeysRecords, treeController)
    }

    private class NodeAndKeys(
        val nd: RBNode<KVP<Int, String>>,
        val lkey: Int?,
        val rkey: Int?
    )

    private fun parseRBNodes(nodeAndKeysRecords: Result, treeController: TreeController<RBNode<KVP<Int, String>>>) {
        val key2nk = mutableMapOf<Int, NodeAndKeys>()
        for (nkRecord in nodeAndKeysRecords) {
            try {
                val key = nkRecord["key"].asInt()
                val value = nkRecord["value"].asString()
                val node = RBNode(null, KVP(key, value))

                val x = nkRecord["x"].asInt()
                val y = nkRecord["y"].asInt()

                val isBlack = nkRecord["isBlack"].asBoolean()
                node.col = if (isBlack) {
                    RBNode.Colour.BLACK
                } else {
                    RBNode.Colour.RED
                }

                treeController.nodes[node] =
                    NodeExtension(mutableStateOf(x), mutableStateOf(y), treeController.getNodeCol(node))

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

                key2nk[key] = NodeAndKeys(node, lkey, rkey)
            } catch (ex: Uncoercible) {
                throw IOException("Invalid nodes label in the database", ex)
            }
        }
        val nks = key2nk.values.toTypedArray()

        if (key2nk.isEmpty()) { // tree was empty
            treeController.tree.root = null
        } else {
            for (nk in nks) {
                nk.lkey?.let {
                    nk.nd.left = key2nk[it]?.nd
                    nk.nd.left?.parent = nk.nd
                    key2nk.remove(it)
                }

                nk.rkey?.let {
                    nk.nd.right = key2nk[it]?.nd
                    nk.nd.right?.parent = nk.nd
                    key2nk.remove(it)
                }
            }
            if (key2nk.values.size != 1) {
                throw IOException("Found ${key2nk.values.size} nodes without parents in database, expected only 1 node")
            }
            treeController.tree.root = key2nk.values.first().nd
        }
    }

    private fun parseNames(nameRecords: Result): MutableList<String> {
        val res = mutableListOf<String>()
        for (nmRecord in nameRecords) {
            try {
                val name = nmRecord["name"].asString()
                res.add(name)
            } catch (ex: Uncoercible) {
                throw IOException("Invalid tree label in the database", ex)
            }
        }
        return res
    }

    fun open(uri: String, username: String, password: String) {
        try {
            driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password))
        } catch (ex: IllegalArgumentException) {
            throw HandledIOException("Wrong URI", ex)
        } catch (ex: SessionExpiredException) {
            throw HandledIOException("Session failed, try restarting the app", ex)
        }
        sendTestQuery()
    }

    override fun close() {
        driver?.close()
    }

    private fun sendTestQuery() {
        getTreesNames()
    }

    private fun <T> handleTransactionException(transaction: () -> T): T {
        try {
            return transaction()
        } catch (ex: AuthenticationException) {
            throw HandledIOException("Wrong username or password", ex)
        } catch (ex: ClientException) {
            println(ex.message)
            throw HandledIOException("Use the bolt:// URI scheme or some other expected labels", ex)
        } catch (ex: ServiceUnavailableException) {
            throw HandledIOException("Check your network connection", ex)
        }
    }
}


