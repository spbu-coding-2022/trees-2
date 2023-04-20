package org.tree.binaryTree.templates


abstract class TemplateBalanceBSTree<T : Comparable<T>, NODE_T : TemplateNode<T, NODE_T>> :
    TemplateBSTree<T, NODE_T>() {

    //Balance
    protected class BalanceCase {
        // LEFT - left child was changed
        // RIGHT - right child was changed
        // ROOT - root was changed
        enum class ChangedChild { LEFT, RIGHT, ROOT }

        // RECURSIVE_CALL - the function was called recursively for traverse
        // END - the last, significant call
        enum class Recursive { RECURSIVE_CALL, END }

        // INSERT - with the insert method
        // REMOVE_X - with the remove method when current node had X null children
        enum class OpType { INSERT, REMOVE_0, REMOVE_1, REMOVE_2 }
    }

    protected fun getBalanceRemoveType(a: Int): BalanceCase.OpType {
        return when (a) {
            2 -> BalanceCase.OpType.REMOVE_2
            1 -> BalanceCase.OpType.REMOVE_1
            else -> BalanceCase.OpType.REMOVE_0
        }
    }

    protected fun getDirectionChangedChild(curNode: NODE_T?, obj: T): BalanceCase.ChangedChild {
        return if (curNode == null) {
            BalanceCase.ChangedChild.ROOT
        } else if (obj < curNode.elem) {
            BalanceCase.ChangedChild.LEFT
        } else {
            BalanceCase.ChangedChild.RIGHT
        }
    }

    // curNode - this is parent of changed node
    // if curNode == null -> changed node = root
    protected abstract fun balance(
        curNode: NODE_T?,
        changedChild: BalanceCase.ChangedChild,
        operationType: BalanceCase.OpType,
        recursive: BalanceCase.Recursive
    )

    override fun insertNode(curNode: NODE_T?, newNode: NODE_T): NODE_T? {
        val parNode = super.insertNode(curNode, newNode)
        if (curNode != null) { // STTK: maybe if cur_node = root, and root = null
            if (curNode === parNode) {
                balance(
                    curNode,
                    getDirectionChangedChild(curNode, newNode.elem),
                    BalanceCase.OpType.INSERT,
                    BalanceCase.Recursive.END
                )
            } else {
                balance(
                    curNode,
                    getDirectionChangedChild(curNode, newNode.elem),
                    BalanceCase.OpType.INSERT,
                    BalanceCase.Recursive.RECURSIVE_CALL
                )
                if (curNode === root) {
                    balance(
                        null,
                        BalanceCase.ChangedChild.ROOT,
                        BalanceCase.OpType.INSERT,
                        BalanceCase.Recursive.RECURSIVE_CALL
                    )
                }
            }
        }
        return parNode
    }

    override fun remove(curNode: NODE_T?, parentNode: NODE_T?, obj: T): Int? {
        if (curNode == null) {
            return null
        }

        val targetNode: NODE_T?
        val isRec: BalanceCase.Recursive
        val res =
            if (obj < curNode.elem) {
                isRec = BalanceCase.Recursive.RECURSIVE_CALL
                targetNode = parentNode
                remove(curNode.left, curNode, obj)
            } else if (obj > curNode.elem) {
                isRec = BalanceCase.Recursive.RECURSIVE_CALL
                targetNode = parentNode
                remove(curNode.right, curNode, obj)
            } else {
                isRec = BalanceCase.Recursive.END
                targetNode = parentNode
                deleteNode(curNode, parentNode)
            }

        if (res != null) {
            balance(targetNode, getDirectionChangedChild(targetNode, obj), getBalanceRemoveType(res), isRec)
        }
        return res
    }

    //Rotates
    protected open fun rotateRight(curNode: NODE_T, parentNode: NODE_T?) {
        val replacementNode = curNode.left ?: throw IllegalArgumentException("Received a node with a null left child")
        curNode.left = replacementNode.right
        replacementNode.right = curNode

        replaceNode(curNode, parentNode, replacementNode)
    }

    protected open fun rotateLeft(curNode: NODE_T, parentNode: NODE_T?) {
        val replacementNode = curNode.right ?: throw IllegalArgumentException("Received a node with a null right child")
        curNode.right = replacementNode.left
        replacementNode.left = curNode

        replaceNode(curNode, parentNode, replacementNode)
    }
}
