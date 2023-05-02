package org.tree.binaryTree

import org.tree.binaryTree.templates.TemplateNode
import org.tree.binaryTree.trees.AVLTree
import org.tree.binaryTree.trees.BinSearchTree
import org.tree.binaryTree.trees.RBTree


/**
 * Node for BinSearchTree
 *
 * @param value the value that the node will contain
 *
 * @see TemplateNode
 * @see BinSearchTree
 */
class Node<T : Comparable<T>>(value: T) : TemplateNode<T, Node<T>>(value)

/**
 * Node for RBTree
 *
 * @param parent the parent node of the current node
 * @param value the value that the node will contain
 *
 * @see TemplateNode
 * @see RBTree
 */
class RBNode<T : Comparable<T>>(var parent: RBNode<T>?, value: T) : TemplateNode<T, RBNode<T>>(value) {
    var color: Color = Color.RED

    enum class Color { RED, BLACK }
}

/**
 * Node for AVLTree
 *
 * @param value the value that the node will contain
 *
 * @see TemplateNode
 * @see AVLTree
 */
class AVLNode<T : Comparable<T>>(value: T) : TemplateNode<T, AVLNode<T>>(value) {
    var height: Int = 1
}
