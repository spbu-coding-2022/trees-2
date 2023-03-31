package org.tree.binaryTree

abstract class TemplateNode<T : Comparable<T>, NODE_T : TemplateNode<T, NODE_T>>(v: T) {
    var elem: T = v
    var left: NODE_T? = null
    var right: NODE_T? = null

    fun getNonNullChild(): NODE_T? {
        val res = if (left != null) {
            left
        } else if (right != null) {
            right
        } else {
            null
        }
        return res
    }

    fun countNullChildren(): Int {
        var res = 0
        if (left == null) {
            res += 1
        }
        if (right == null) {
            res += 1
        }
        return res
    }

    //Traversal
    enum class Traversal {
        INORDER,
        PREORDER,
        POSTORDER
    }

    private fun traversal(res: MutableList<T>, cs: Traversal) {
        if (cs == Traversal.PREORDER) {
            res.add(elem)
        }
        left?.traversal(res, cs)
        if (cs == Traversal.INORDER) {
            res.add(elem)
        }
        right?.traversal(res, cs)
        if (cs == Traversal.POSTORDER) {
            res.add(elem)
        }
    }

    fun traversal(order: Traversal): MutableList<T> {
        val res: MutableList<T> = mutableListOf()
        traversal(res, order)
        return res
    }
}

class Node<T : Comparable<T>>(v: T) : TemplateNode<T, Node<T>>(v)

class RBNode<T : Comparable<T>>(p: RBNode<T>?, v: T) : TemplateNode<T, RBNode<T>>(v) {
    var parent: RBNode<T>? = p
    var col: Colour = Colour.RED

    enum class Colour { RED, BLACK }
}

class AVLNode<T : Comparable<T>>(v: T) : TemplateNode<T, AVLNode<T>>(v) {
    var height: Int = 0
}
