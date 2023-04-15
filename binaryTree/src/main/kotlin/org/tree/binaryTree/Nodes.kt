package org.tree.binaryTree

import org.tree.binaryTree.templates.TemplateNode

class Node<T : Comparable<T>>(v: T) : TemplateNode<T, Node<T>>(v)

class RBNode<T : Comparable<T>>(p: RBNode<T>?, v: T) : TemplateNode<T, RBNode<T>>(v) {
    var parent: RBNode<T>? = p
    var col: Colour = Colour.RED

    enum class Colour { RED, BLACK }
}

class AVLNode<T : Comparable<T>>(v: T) : TemplateNode<T, AVLNode<T>>(v) {
    var height: Int = 1
}
