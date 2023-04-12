package org.tree.binaryTree.trees

import org.tree.binaryTree.AVLNode
import org.tree.binaryTree.templates.TemplateBalanceBSTree
import kotlin.math.max

class AVLTree<T : Comparable<T>> : TemplateBalanceBSTree<T, AVLNode<T>>() {
    private fun height(avlNode: AVLNode<T>?): Int {
        return avlNode?.height ?: 0
    }

    private fun bfactor(avlNode: AVLNode<T>?): Int {
        return if (avlNode != null) {
            height(avlNode.right) - height(avlNode.left)
        } else {
            0
        }
    }

    private fun fixheight(avlNode: AVLNode<T>?) {
        if (avlNode != null) {
            val hl = height(avlNode.left)
            val hr = height(avlNode.right)
            avlNode.height = max(hl, hr) + 1
        }
    }

    override fun rotateRight(curNode: AVLNode<T>, parentNode: AVLNode<T>?) {
        val replacementNode = curNode.left
        super.rotateRight(curNode, parentNode)
        fixheight(curNode)
        fixheight(replacementNode)
    }

    override fun rotateLeft(curNode: AVLNode<T>, parentNode: AVLNode<T>?) {
        val replacementNode = curNode.right
        super.rotateLeft(curNode, parentNode)
        fixheight(replacementNode)
        fixheight(curNode)
    }

    override fun insert(curNode: AVLNode<T>?, obj: T): AVLNode<T>? {
        return super.insertNode(curNode, AVLNode(obj))
    }

    private fun balanceNode(curNode: AVLNode<T>, parentNode: AVLNode<T>?) {
        fixheight(curNode)
        if (bfactor(curNode) == 2) {
            if (bfactor(curNode.right) < 0) {
                curNode.right?.let { rotateRight(it, curNode) }
            }
            rotateLeft(curNode, parentNode)
        }

        if (bfactor(curNode) == -2) {
            if (bfactor(curNode.left) > 0) {
                curNode.left?.let { rotateLeft(it, curNode) }
            }
            rotateRight(curNode, parentNode)
        }
    }

    override fun balance(
        curNode: AVLNode<T>?,
        changedChild: BalanceCase.ChangedChild,
        operationType: BalanceCase.OpType,
        recursive: BalanceCase.Recursive
    ) {
        when (operationType) {
            BalanceCase.OpType.REMOVE_0 -> {}
            else -> {
                if (curNode == null) {
                    root?.let { balanceNode(it, curNode) }
                    return
                }

                curNode.right?.let { balanceNode(it, curNode) }
                curNode.left?.let { balanceNode(it, curNode) }
            }
        }
    }
}
