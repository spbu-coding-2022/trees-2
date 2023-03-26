package org.tree.binaryTree

class BinSearchTree<T : Comparable<T>> : TemplateBSTree<T, Node<T>>() {
    override fun insert(curNode: Node<T>?, obj: T): Node<T>? {
        return insertNode(curNode, Node(obj))
    }
}
