package org.tree.binaryTree.templates

/**
 * This class is template class for creating your own binary search trees.
 * @param T the type of element stored in the tree's nodes
 * @param NODE_T the type of nodes in the tree
 *
 * @property root the root node of the tree
 */
abstract class TemplateBSTree<T : Comparable<T>, NODE_T : TemplateNode<T, NODE_T>> {
    var root: NODE_T? = null

    // Insert
    /**
     * Insert [newNode] into the subtree of the [curNode].
     *
     * @return the parent of the inserted node,
     * null if node with the same element already in tree or if inserted node is root
     */
    protected open fun insertNode(curNode: NODE_T?, newNode: NODE_T): NODE_T? {
        if (curNode == null) {
            if (root === curNode) {
                root = newNode
                return null
            } else {
                throw IllegalArgumentException("Received a non-root null node")
            }
        } else {
            if (newNode.element < curNode.element) {
                if (curNode.left == null) {
                    curNode.left = newNode
                    return curNode
                } else {
                    return insertNode(curNode.left, newNode)
                }
            } else if (newNode.element > curNode.element) {
                if (curNode.right == null) {
                    curNode.right = newNode
                    return curNode
                } else {
                    return insertNode(curNode.right, newNode)
                }
            } else {
                return null
            }
        }
    }

    /**
     * Insert [element] into the subtree of the [curNode].
     *
     * @return the parent of the inserted node,
     * null if node with the same element already in tree or if inserted node is root
     *
     * @see insertNode
     */
    protected abstract fun insert(curNode: NODE_T?, element: T): NODE_T?

    /**
     * Insert [element] into tree.
     *
     * @return true if the element has been inserted, false if the element is already contained in the tree.
     */
    fun insert(element: T): Boolean {
        val rootInsert = root == null
        // insert returns null if the same element is found or when the root is inserted
        return ((insert(root, element) != null) or rootInsert)
    }

    // Find
    /**
     * Find node with the given [element] into subtree of the [curNode].
     *
     * @return the found node or null if the node was not found
     */
    protected fun find(curNode: NODE_T?, element: T): NODE_T? {
        if (curNode == null) {
            return null
        }

        if (element < curNode.element) {
            return find(curNode.left, element)
        } else if (element > curNode.element) {
            return find(curNode.right, element)
        } else {
            return curNode
        }
    }

    /**
     * Find node with the given [element] into tree.
     *
     * @return the found node or null if the node was not found
     */
    fun find(element: T): NODE_T? {
        return find(root, element)
    }

    // Remove
    /**
     * Delete [curNode] with [parentNode] as parent from tree.
     *
     * @return the count of null children of deleted node
     */
    protected fun deleteNode(curNode: NODE_T, parentNode: NODE_T?): Int {
        var res = 0
        if (curNode.left == null)
            res += 1
        if (curNode.right == null)
            res += 1

        when (res) {
            0 -> {
                val nxt =
                    findNext(curNode) ?: throw IllegalArgumentException("Got null as next than right child isn't null")
                val buf = nxt.element
                remove(nxt.element)
                curNode.element = buf
            }

            1 -> {
                replaceNode(curNode, parentNode, curNode.left ?: curNode.right)
            }

            else -> {
                replaceNode(curNode, parentNode, null)
            }
        }
        return res
    }

    /**
     * Remove [element] from subtree of the [curNode] with [parentNode] as parent.
     *
     * @return the count of null children of deleted node or null if the node was not found
     */
    protected open fun remove(curNode: NODE_T?, parentNode: NODE_T?, element: T): Int? {
        if (curNode == null) {
            return null
        }

        if (element < curNode.element) {
            return remove(curNode.left, curNode, element)
        } else if (element > curNode.element) {
            return remove(curNode.right, curNode, element)
        } else {
            return deleteNode(curNode, parentNode)
        }
    }

    /**
     * Remove [element] from tree.
     *
     * @return true if the element has been successfully removed; false if it was not present in the tree.
     */
    fun remove(element: T): Boolean {
        return remove(root, null, element) != null
    }

    // Additional
    /**
     * Find next node after [curNode]
     *
     * @return node with minimal element that greater than element of current node;
     * null if element of current node is the greatest
     */
    protected fun findNext(curNode: NODE_T): NODE_T? {
        var res = curNode.right
        if (res == null) {
            return null
        } else {
            var nextNode = res.left
            while (nextNode != null) {
                res = nextNode
                nextNode = res.left
            }
            return res
        }
    }

    /**
     * Replace [replacedNode] with [parentNode] as parent by [newNode].
     */
    protected open fun replaceNode(replacedNode: NODE_T, parentNode: NODE_T?, newNode: NODE_T?) {
        if (parentNode == null) {
            if (root === replacedNode) {
                root = newNode
            } else {
                throw IllegalArgumentException("Received a non-root node with a null parent")
            }
        } else {
            if (parentNode.right === replacedNode) {
                parentNode.right = newNode
            } else if (parentNode.left === replacedNode) {
                parentNode.left = newNode
            } else {
                throw IllegalArgumentException("Received a node with a wrong parent")
            }
        }
    }

    /**
     * @param order specified traversal order
     *
     * @return a list of tree's elements in the specified order
     */
    fun traversal(order: TemplateNode.Traversal): MutableList<T> {
        return root?.traverse(order) ?: mutableListOf()
    }
}
