package org.tree.binaryTree

class AVLTree<T : Comparable<T>> : TemplateBalanceBSTree<T, AVLNode<T>>() {
    private fun height(avlNode: AVLNode<T>?): Int {
        return avlNode?.height ?: 0
    }

    private fun bfactor(avlNode: AVLNode<T>?): Int {
        return if (avlNode != null) {
            height(avlNode.right)-height(avlNode.left)
        } else {
            0
        }
    }

    private fun fixheight(avlNode: AVLNode<T>?) {
        if (avlNode != null) {
            val hl = height(avlNode.left)
            val hr = height(avlNode.right)
            avlNode.height = (if (hl > hr) hl else hr) + 1
        }
    }

    override fun insert(curNode: AVLNode<T>?, obj: T): AVLNode<T>? {
        return super.insertNode(curNode, AVLNode(obj))
    }

    override fun balance(curNode: AVLNode<T>?, operationType: BalanceCase.OpType, recursive: BalanceCase.Recursive) {
        TODO("Not yet implemented")
    }
}