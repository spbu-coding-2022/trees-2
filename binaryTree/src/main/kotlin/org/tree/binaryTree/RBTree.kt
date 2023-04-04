package org.tree.binaryTree

class RBTree<T : Comparable<T>> : TemplateBalanceBSTree<T, RBNode<T>>() {
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
        if (operationType == BalanceCase.OpType.INSERT) {
            if (curNode != null) {
                if (recursive == BalanceCase.Recursive.END) { // curNode is parent Node of inserted Node
                    if (curNode.col == RBNode.Colour.RED) {
                        val grandParent = curNode.parent
                        if (grandParent != null) { // in case when grandparent is null, there is no need to balance a tree
                            val uncle_position: balancePosition
                            val uncle = if (curNode.elem < grandParent.elem) {
                                uncle_position = balancePosition.RIGHT_UNCLE
                                grandParent.right
                            } else {
                                uncle_position = balancePosition.LEFT_UNCLE
                                grandParent.left
                            }
                            if (uncle != null) {
                                if (uncle.col == RBNode.Colour.RED) {
                                    balanceInsertCaseOfRedUncle(curNode, grandParent, uncle)
                                } else {
                                    balanceInsertCaseOfBLackUncle(curNode, grandParent, uncle_position)
                                }
                            } else { // null uncle means that he is black
                                balanceInsertCaseOfBLackUncle(curNode, grandParent, uncle_position)
                            }
                        }
                    }
                }
            }
        }
    }

    enum class balancePosition {
        LEFT_UNCLE, RIGHT_UNCLE
    }

    private fun balanceInsertCaseOfRedUncle(curNode: RBNode<T>, grandParent: RBNode<T>, uncle: RBNode<T>) {
        val grandGrandParent = grandParent.parent
        uncle.col = RBNode.Colour.BLACK
        curNode.col = RBNode.Colour.BLACK
        if (grandGrandParent != null) {
            grandParent.col = RBNode.Colour.RED
            // https://skr.sh/sJ9LBQU2IGg, when y is curNode
            if (grandGrandParent.col == RBNode.Colour.RED) {
                balance(grandGrandParent, BalanceCase.OpType.INSERT, BalanceCase.Recursive.END)
            }
        }
    }

    private fun balanceInsertCaseOfBLackUncle(
        curNode: RBNode<T>,
        grandParent: RBNode<T>,
        position: balancePosition
    ) { // can be null uncle
        if (position == balancePosition.LEFT_UNCLE) {
            val leftSon = curNode.left
            if (leftSon != null) {
                if (leftSon.col == RBNode.Colour.RED) {
                    rotateRight(curNode, grandParent)
                    balance(curNode.parent, BalanceCase.OpType.INSERT, BalanceCase.Recursive.END)
                }
            } else {
                val rightSon = curNode.right
                if (rightSon != null) {
                    curNode.col = RBNode.Colour.BLACK
                    grandParent.col = RBNode.Colour.RED
                    rotateLeft(grandParent, grandParent.parent)
                }
            }
        } else {
            val leftSon = curNode.left
            if (leftSon != null) {
                if (leftSon.col == RBNode.Colour.RED) {
                    curNode.col = RBNode.Colour.BLACK
                    grandParent.col = RBNode.Colour.RED
                    rotateRight(grandParent, grandParent.parent)
                }
            } else {
                val rightSon = curNode.right
                if (rightSon != null) {
                    if (rightSon.col == RBNode.Colour.RED) {
                        rotateLeft(curNode, grandParent)
                        balance(curNode.parent, BalanceCase.OpType.INSERT, BalanceCase.Recursive.END)
                    }
                }
            }
        }
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