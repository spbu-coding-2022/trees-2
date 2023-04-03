package org.tree.binaryTree

class RBTree<T : Comparable<T>> : TemplateBalanceBSTree<T, RBNode<T>>(){
    private fun findParentForNewNode(curNode: RBNode<T>?, obj: T): RBNode<T>? {
        if (curNode == null) { // impossible, but check for null is needed
            return null
        }
        if (obj > curNode.elem) {
            if (curNode.right == null) {
                return curNode
            }
            return findParentForNewNode(curNode.right, obj)
        } else if (obj < curNode.elem) {
            if (curNode.left == null) {
                return curNode
            }
            return findParentForNewNode(curNode.left, obj)
        } else {  // the element already exist
            return null
        }
    }
    override fun insert(curNode: RBNode<T>?, obj: T): RBNode<T>? {
        TODO("Not yet implemented")
    }

    override fun balance(curNode: RBNode<T>?, operationType: BalanceCase.OpType, recursive: BalanceCase.Recursive) {
        TODO("Not yet implemented")
    }
}