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
        if (parentForObj == null) {
            val RB_root = RBNode(parentForObj, obj)
            RB_root.col = RBNode.Colour.BLACK
            return insertNode(parentForObj, RB_root)
        }
        return insertNode(parentForObj, RBNode(parentForObj, obj)) // "в прод"
    }

    override fun balance(curNode: RBNode<T>?, operationType: BalanceCase.OpType, recursive: BalanceCase.Recursive) {
        if (operationType == BalanceCase.OpType.INSERT) {
            if (curNode != null) {
                if (recursive == BalanceCase.Recursive.END) { // curNode is parent Node of inserted Node
                    if (curNode.col == RBNode.Colour.RED) {
                        val grandParent = curNode.parent
                        val uncle: RBNode<T>
                        if (grandParent != null) { // in case when grandparent is null, there is no need to balance a tree
                            uncle = if (curNode.elem < grandParent.elem) {
                                grandParent.right as RBNode<T>
                            } else {
                                grandParent.left as RBNode<T>
                            }
                            if (uncle.col == RBNode.Colour.RED) {
                                val grandGrandParent = grandParent.parent
                                if (grandGrandParent != null) {
                                    uncle.col = RBNode.Colour.BLACK
                                    curNode.col = RBNode.Colour.BLACK
                                    grandParent.col = RBNode.Colour.RED
                                    // three lines above is https://skr.sh/sJ9LBQU2IGg, when y is curNode
                                    if (grandGrandParent.col == RBNode.Colour.RED) {
                                        balance(grandParent, BalanceCase.OpType.INSERT, BalanceCase.Recursive.END)
                                    }
                                } else {
                                    uncle.col = RBNode.Colour.BLACK
                                    curNode.col = RBNode.Colour.BLACK
                                }
                            } else {
                                val leftSon = curNode.left as RBNode
                                if (leftSon.col == RBNode.Colour.RED) {
                                    curNode.col = RBNode.Colour.BLACK
                                    grandParent.col = RBNode.Colour.RED
                                    rotateRight(curNode, grandParent)
                                }
                                val rightSon = curNode.right as RBNode
                                if (rightSon.col == RBNode.Colour.RED) {
                                    rotateLeft(rightSon, curNode)
                                    balance(rightSon, operationType, recursive)
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}