package org.tree.binaryTree

class AVLTree<T : Comparable<T>> : TemplateBalanceBSTree<T, AVLNode<T>>() {
    override fun insert(curNode: AVLNode<T>?, obj: T): AVLNode<T>? {
        return super.insertNode(curNode, AVLNode(obj))
    }

    override fun balance(curNode: AVLNode<T>?, operationType: BalanceCase.OpType, recursive: BalanceCase.Recursive) {
        TODO("Not yet implemented")
    }
}