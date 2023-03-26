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
}
