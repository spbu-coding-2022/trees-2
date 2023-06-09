package org.tree.binaryTree.trees

import org.tree.binaryTree.AVLNode
import org.tree.binaryTree.templates.TemplateBalanceBSTree
import kotlin.math.max

class AVLTree<T : Comparable<T>> : TemplateBalanceBSTree<T, AVLNode<T>>() {
    private fun heightOrZero(avlNode: AVLNode<T>?): Int {
        return avlNode?.height ?: 0
    }

    private fun balanceFactor(avlNode: AVLNode<T>): Int = //avl balance factor - difference in the heights of the right and left subtrees
        avlNode.run {
            fixHeight(this)
            fixHeight(left)
            fixHeight(right)
            heightOrZero(right) - heightOrZero(left)
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

    override fun insert(curNode: AVLNode<T>?, element: T): AVLNode<T>? {
        return super.insertNode(curNode, AVLNode(element))
    }

    private fun balanceNode(curNode: AVLNode<T>, parentNode: AVLNode<T>?) {
        if (balanceFactor(curNode) == 2) {
            curNode.right?.let {
                if (balanceFactor(it) < 0) {
                    rotateRight(it, curNode)
                }
            }
            rotateLeft(curNode, parentNode)
            return
        }

        if (balanceFactor(curNode) == -2) {
            curNode.left?.let {
                if (balanceFactor(it) > 0) {
                    rotateLeft(it, curNode)
                }
            }
            rotateRight(curNode, parentNode)
        }
    }

    override fun balance(
        curNode: AVLNode<T>?,
        changedChild: BalanceCase.ChangedChild,
        operationType: BalanceCase.OperationType,
        recursive: BalanceCase.Recursive
    ) {
        if (operationType == BalanceCase.OperationType.REMOVE_0) return
        if (curNode == null) {
            root?.let { balanceNode(it, curNode) }
            return
        }
        curNode.right?.let { balanceNode(it, curNode) }
        curNode.left?.let { balanceNode(it, curNode) }
    }
}
