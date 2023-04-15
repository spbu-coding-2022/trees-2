package org.tree.binaryTree.trees

import org.tree.binaryTree.Node
import org.tree.binaryTree.templates.TemplateBSTree

class BinSearchTree<T : Comparable<T>> : TemplateBSTree<T, Node<T>>() {
    override fun insert(curNode: Node<T>?, obj: T): Node<T>? {
        return insertNode(curNode, Node(obj))
    }
}
