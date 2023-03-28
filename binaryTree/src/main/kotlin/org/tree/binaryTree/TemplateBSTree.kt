package org.tree.binaryTree

abstract class TemplateBSTree<T : Comparable<T>, NODE_T : TemplateNode<T, NODE_T>> {
    var root: NODE_T? = null

    // Insert
    protected open fun insertNode(curNode: NODE_T?, newNode: NODE_T): NODE_T? {
        if (curNode == null) {
            if (root === curNode) {
                root = newNode
                return newNode
            } else {
                throw IllegalArgumentException("Received a non-root null node")
            }
        } else {
            if (newNode.elem < curNode.elem) {
                if (curNode.left == null) {
                    curNode.left = newNode
                } else {
                    insertNode(curNode.left, newNode)
                }
                return curNode.left
            } else if (newNode.elem > curNode.elem) {
                if (curNode.right == null) {
                    curNode.right = newNode
                } else {
                    insertNode(curNode.right, newNode)
                }
                return curNode.right
            } else {
                return null // STTK: 10%
            }
        }
    }

    protected abstract fun insert(curNode: NODE_T?, obj: T): NODE_T?

    fun insert(obj: T) {
        insert(root, obj)
    }

    //Find
    protected fun find(curNode: NODE_T?, obj: T): NODE_T? {
        if (curNode == null) {
            return null
        }

        if (curNode.elem == obj) {
            return curNode
        } else if (obj < curNode.elem) {
            return find(curNode.left, obj)
        } else {
            return find(curNode.right, obj)
        }
    }

    fun find(obj: T): NODE_T? {
        return find(root, obj)
    }

    //Remove
    protected open fun deleteNode(curNode: NODE_T, parentNode: NODE_T?): Int {
        val res = curNode.countNullChild()
        when (res) {
            0 -> {
                val nxt =
                    findNext(curNode) ?: throw IllegalArgumentException("Got null as next than right child isn't null")
                val buf = nxt.elem
                remove(nxt.elem)
                curNode.elem = buf
            }

            1 -> {
                replaceNode(curNode, parentNode, curNode.getNonNullChild())
            }

            else -> {
                replaceNode(curNode, parentNode, null)
            }
        }
        return res
    }

    protected open fun remove(curNode: NODE_T?, parentNode: NODE_T?, obj: T): Int? {
        if (curNode == null) {
            return null
        }

        if (curNode.elem == obj) {
            return deleteNode(curNode, parentNode)
        } else if (obj < curNode.elem) {
            return remove(curNode.left, curNode, obj)
        } else {
            return remove(curNode.right, curNode, obj)
        }
    }

    fun remove(obj: T) {
        remove(root, null, obj)
    }

    //Additional
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

    protected fun replaceNode(replacedNode: NODE_T, parentNode: NODE_T?, newNode: NODE_T?) {
        if (parentNode == null) {
            if (root === replacedNode) {
                root = newNode
            } else {
                throw IllegalArgumentException("Received a non-root node with a null parent")
            }
        } else {
            if (parentNode.right == replacedNode) {
                parentNode.right = newNode
            } else if (parentNode.left == replacedNode) {
                parentNode.left = newNode
            } else {
                throw IllegalArgumentException("Received a node with a wrong parent")
            }
        }
    }
}
