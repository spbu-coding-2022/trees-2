package org.tree.binaryTree


abstract class TemplateBalanceBSTree<T : Comparable<T>, NODE_T : TemplateNode<T, NODE_T>> :
    TemplateBSTree<T, NODE_T>() {

    //Balance
    protected class BalanceCase {
        enum class Recursive { RECURSIVE_CALL, END }
        enum class OpType { INSERT, REMOVE_0, REMOVE_1, REMOVE_2 }
    }

    protected fun getBalanceRemoveType(a: Int): BalanceCase.OpType {
        return when (a) {
            2 -> BalanceCase.OpType.REMOVE_2
            1 -> BalanceCase.OpType.REMOVE_1
            else -> BalanceCase.OpType.REMOVE_0
        }
    }

    protected abstract fun balance(curNode: NODE_T, operationType: BalanceCase.OpType, recursive: BalanceCase.Recursive)


}
