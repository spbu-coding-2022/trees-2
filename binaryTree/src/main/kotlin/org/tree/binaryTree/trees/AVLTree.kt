package org.tree.binaryTree.trees

import org.tree.binaryTree.AVLNode
import org.tree.binaryTree.templates.TemplateBalanceBSTree
import kotlin.math.max

class AVLTree<T : Comparable<T>> : TemplateBalanceBSTree<T, AVLNode<T>>() {
    private fun heightOrZero(avlNode: AVLNode<T>?): Int {
        return avlNode?.height ?: 0
    }

    private fun balanceFactor(avlNode: AVLNode<T>?): Int {
        fixHeight(avlNode)
        fixHeight(avlNode?.left)
        fixHeight(avlNode?.right)
        return if (avlNode != null) {
            heightOrZero(avlNode.right) - heightOrZero(avlNode.left)
        } else {
            0
        }
    }

    private fun fixHeight(avlNode: AVLNode<T>?) {
        if (avlNode != null) {
            val hl = heightOrZero(avlNode.left)
            val hr = heightOrZero(avlNode.right)
            avlNode.height = max(hl, hr) + 1
        }
    }

    override fun rotateRight(curNode: AVLNode<T>, parentNode: AVLNode<T>?) {
        val replacementNode = curNode.left
        super.rotateRight(curNode, parentNode)
        fixHeight(curNode)
        fixHeight(replacementNode)
    }

    override fun rotateLeft(curNode: AVLNode<T>, parentNode: AVLNode<T>?) {
        val replacementNode = curNode.right
        super.rotateLeft(curNode, parentNode)
        fixHeight(replacementNode)
        fixHeight(curNode)
    }

    override fun insert(curNode: AVLNode<T>?, obj: T): AVLNode<T>? {
        return super.insertNode(curNode, AVLNode(obj))
    }

    private fun balanceNode(curNode: AVLNode<T>, parentNode: AVLNode<T>?) {
        if (balanceFactor(curNode) == 2) {
            if (balanceFactor(curNode.right) < 0) {
                curNode.right?.let { rotateRight(it, curNode) }
            }
            rotateLeft(curNode, parentNode)
            return
        }

        if (balanceFactor(curNode) == -2) {
            if (balanceFactor(curNode.left) > 0) {
                curNode.left?.let { rotateLeft(it, curNode) }
            }
            rotateRight(curNode, parentNode)
            return
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
