package org.tree.binaryTree.templates

/**
 * This class is template class for creating your own nodes.
 * @param T the type of element stored in the node
 * @param NODE_T the type of your node
 *
 * @property element the element stored in the node.
 * @property left the left child node of this node, or null if this node does not have a left child node.
 * @property right the right child node of this node, or null if this node does not have a right child node.
 */
abstract class TemplateNode<T : Comparable<T>, NODE_T : TemplateNode<T, NODE_T>>(var element: T) {
    var left: NODE_T? = null
    var right: NODE_T? = null

    /**
     * @property INORDER first the left child, then the parent and the right child
     * @property PREORDER first the parent, then the left child and the right child
     * @property POSTORDER first the left child, then the right child and the parent
     */
    enum class Traversal {
        INORDER,
        PREORDER,
        POSTORDER
    }

    private fun traverse(res: MutableList<T>, traversalOrder: Traversal) {
        if (traversalOrder == Traversal.PREORDER) {
            res.add(element)
        }
        left?.traverse(res, traversalOrder)
        if (traversalOrder == Traversal.INORDER) {
            res.add(element)
        }
        right?.traverse(res, traversalOrder)
        if (traversalOrder == Traversal.POSTORDER) {
            res.add(element)
        }
    }

    /**
     * @return a list of nodes' elements in the specified order
     * @param order specified traversal order
     */
    fun traverse(order: Traversal): MutableList<T> {
        val res: MutableList<T> = mutableListOf()
        traverse(res, order)
        return res
    }
}
