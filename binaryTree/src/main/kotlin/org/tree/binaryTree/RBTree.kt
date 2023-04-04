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
        val parentForObj = findParentForNewNode(curNode, obj)
        val newNode = RBNode(parentForObj, obj)
        if (parentForObj == null) { // in case of root insert | node already exist (nothing will be changed)
            newNode.col = RBNode.Colour.BLACK
        }
        return insertNode(parentForObj, newNode)
    }

    override fun balance(curNode: RBNode<T>?, operationType: BalanceCase.OpType, recursive: BalanceCase.Recursive) {
        TODO("Not yet implemented")
    }
    override fun replaceNode(replacedNode: RBNode<T>, parentNode: RBNode<T>?, newNode: RBNode<T>?) {
        newNode?.parent = parentNode
        super.replaceNode(replacedNode, parentNode, newNode)
    }

    override fun rotateRight(curNode: RBNode<T>, parentNode: RBNode<T>?) {
        curNode.parent = curNode.left
        curNode.left?.parent = parentNode
        curNode.left?.right?.parent = curNode
        if (curNode === root) {
            curNode.left?.col = RBNode.Colour.BLACK
        }
        super.rotateRight(curNode, parentNode)
    }

    override fun rotateLeft(curNode: RBNode<T>, parentNode: RBNode<T>?) {
        curNode.parent = curNode.right
        curNode.right?.parent = parentNode
        curNode.right?.left?.parent = curNode
        if (curNode === root) {
            curNode.right?.col = RBNode.Colour.BLACK
        }
        super.rotateLeft(curNode, parentNode)
    }
}