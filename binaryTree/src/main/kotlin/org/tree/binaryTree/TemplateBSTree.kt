package org.tree.binaryTree

abstract class TemplateBSTree<T : Comparable<T>, NODE_T : TemplateNode<T, NODE_T>> {
    var root: NODE_T? = null
}
