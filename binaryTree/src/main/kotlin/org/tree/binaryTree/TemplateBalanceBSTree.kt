package org.tree.binaryTree


abstract class TemplateBalanceBSTree<T : Comparable<T>, NODE_T : TemplateNode<T, NODE_T>> :
    TemplateBSTree<T, NODE_T>() {

    //Balance
    protected class BalanceCase {
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

    // curNode - this is parent of changed node
    protected abstract fun balance(curNode: NODE_T, operationType: BalanceCase.OpType, recursive: BalanceCase.Recursive)

    override fun insertNode(curNode: NODE_T?, newNode: NODE_T): NODE_T? {
        val insNode = super.insertNode(curNode, newNode)
        if (curNode != null) { // STTK: maybe if cur_node = root, and root = null
            if ((curNode.left === insNode) or (curNode.right === insNode)) {
                balance(curNode, BalanceCase.OpType.INSERT, BalanceCase.Recursive.END)
            } else {
                balance(curNode, BalanceCase.OpType.INSERT, BalanceCase.Recursive.RECURSIVE_CALL)
            }
        }
        return insNode
    }

    override fun remove(curNode: NODE_T?, parentNode: NODE_T?, obj: T): Int? {
        if (curNode == null) {
            return null
        }

        val targetNode: NODE_T?
        val isRec: BalanceCase.Recursive
        val res = if (curNode.elem == obj) {
            isRec = BalanceCase.Recursive.END
            targetNode = parentNode
            deleteNode(curNode, parentNode)
        } else if (obj < curNode.elem) {
            isRec = BalanceCase.Recursive.RECURSIVE_CALL
            targetNode = curNode
            remove(curNode.left, curNode, obj)
        } else {
            isRec = BalanceCase.Recursive.RECURSIVE_CALL
            targetNode = curNode
            remove(curNode.right, curNode, obj)
        }

        if (res != null) {
            if (targetNode != null) {
                balance(targetNode, getBalanceRemoveType(res), isRec)
            }
        }
        return res
    }
}
