package org.tree.binaryTree

class RBTree<T : Comparable<T>> : TemplateBalanceBSTree<T, RBNode<T>>() {
    private fun findParentForNewNode(curNode: RBNode<T>?, obj: T): RBNode<T>? {
        if (curNode == null) { // impossible, but check for null is needed
            throw IllegalArgumentException("Received null as root")
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
        if (recursive == BalanceCase.Recursive.END) {
            if (curNode != null) {
                when (operationType) {
                    BalanceCase.OpType.INSERT -> { // curNode is parent Node of inserted Node
                        balanceInsert(curNode)
                    }
                    BalanceCase.OpType.REMOVE_0 -> TODO()
                    BalanceCase.OpType.REMOVE_1 -> TODO()
                    BalanceCase.OpType.REMOVE_2 -> TODO()
                }
            }
        }
    }

    enum class BalancePosition {
        LEFT_UNCLE, RIGHT_UNCLE
    }

    private fun balanceInsert(parentNode: RBNode<T>) {
        if (parentNode.col == RBNode.Colour.RED) {
            val grandParent = parentNode.parent
            if (grandParent != null) { // in case when grandparent is null, there is no need to balance a tree
                val unclePosition: BalancePosition
                val uncle = if (parentNode.elem < grandParent.elem) {
                    unclePosition = BalancePosition.RIGHT_UNCLE
                    grandParent.right
                } else {
                    unclePosition = BalancePosition.LEFT_UNCLE
                    grandParent.left
                }
                if (uncle != null) {
                    if (uncle.col == RBNode.Colour.RED) {
                        balanceInsertCaseOfRedUncle(parentNode, grandParent, uncle)
                    } else {
                        balanceInsertCaseOfBLackUncle(parentNode, grandParent, unclePosition)
                    }
                } else { // null uncle means that he is black
                    balanceInsertCaseOfBLackUncle(parentNode, grandParent, unclePosition)
                }
            }
        }
    }

    private fun balanceInsertCaseOfRedUncle(parentNode: RBNode<T>, grandParent: RBNode<T>, uncle: RBNode<T>) {
        val grandGrandParent = grandParent.parent
        uncle.col = RBNode.Colour.BLACK
        parentNode.col = RBNode.Colour.BLACK
        if (grandGrandParent != null) {
            grandParent.col = RBNode.Colour.RED
            // https://skr.sh/sJ9LBQU2IGg, when y is curNode
            if (grandGrandParent.col == RBNode.Colour.RED) {
                balanceInsert(grandGrandParent)
            }
        }
    }

    private fun balanceInsertCaseOfBLackUncle(
        parentNode: RBNode<T>,
        grandParent: RBNode<T>,
        position: BalancePosition
    ) { // can be null uncle
        if (position == BalancePosition.LEFT_UNCLE) {
            val leftSon = parentNode.left
            if (leftSon != null) {
                if (leftSon.col == RBNode.Colour.RED) {
                    rotateRight(parentNode, grandParent)
                    parentNode.parent?.let { balanceInsert(it) }
                }
            } else {
                val rightSon = parentNode.right
                if (rightSon != null) {
                    parentNode.col = RBNode.Colour.BLACK
                    grandParent.col = RBNode.Colour.RED
                    rotateLeft(grandParent, grandParent.parent)
                }
            }
        } else {
            val leftSon = parentNode.left
            if (leftSon != null) {
                if (leftSon.col == RBNode.Colour.RED) {
                    parentNode.col = RBNode.Colour.BLACK
                    grandParent.col = RBNode.Colour.RED
                    rotateRight(grandParent, grandParent.parent)
                }
            } else {
                val rightSon = parentNode.right
                if (rightSon != null) {
                    if (rightSon.col == RBNode.Colour.RED) {
                        rotateLeft(parentNode, grandParent)
                        parentNode.parent?.let { balanceInsert(it) }
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
