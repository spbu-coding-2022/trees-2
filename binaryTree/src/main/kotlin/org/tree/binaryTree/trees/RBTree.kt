package org.tree.binaryTree.trees

import org.tree.binaryTree.RBNode
import org.tree.binaryTree.templates.TemplateBalanceBSTree

class RBTree<T : Comparable<T>> : TemplateBalanceBSTree<T, RBNode<T>>() {
    private fun findParentForNewNode(curNode: RBNode<T>?, obj: T): RBNode<T>? {
        if (curNode != null) {
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
            }
        }
        return null
    }

    override fun insert(curNode: RBNode<T>?, obj: T): RBNode<T>? {
        val parentForObj = findParentForNewNode(curNode, obj)
        val newNode = RBNode(parentForObj, obj)
        if (parentForObj == null) { // in case of root insert | node already exist (nothing will be changed)
            if (root == null) {
                newNode.col = RBNode.Colour.BLACK
            } else {
                return null
            }
        }
        return insertNode(parentForObj, newNode)
    }

    override fun balance(
        curNode: RBNode<T>?,
        changedChild: BalanceCase.ChangedChild,
        operationType: BalanceCase.OpType,
        recursive: BalanceCase.Recursive
    ) {
        if (recursive == BalanceCase.Recursive.END) {
            if (curNode != null) {
                when (operationType) {
                    BalanceCase.OpType.INSERT -> { // curNode is parent Node of inserted Node
                        balanceInsert(curNode)
                    }

                    BalanceCase.OpType.REMOVE_0 -> {
                        // does nothing
                    }

                    BalanceCase.OpType.REMOVE_1 -> {
                        balanceRemove1(curNode, changedChild)

                    }

                    BalanceCase.OpType.REMOVE_2 -> {
                        balanceRemove2(curNode, changedChild)
                    }
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
            val leftChild = parentNode.left
            if (leftChild?.col == RBNode.Colour.RED) {
                rotateRight(parentNode, grandParent)
                parentNode.parent?.let { balanceInsert(it) }
            } else {
                val rightChild = parentNode.right
                if (rightChild != null) {
                    parentNode.col = RBNode.Colour.BLACK
                    grandParent.col = RBNode.Colour.RED
                    rotateLeft(grandParent, grandParent.parent)
                }
            }
        } else {
            val leftChild = parentNode.left
            if (leftChild?.col == RBNode.Colour.RED) {
                parentNode.col = RBNode.Colour.BLACK
                grandParent.col = RBNode.Colour.RED
                rotateRight(grandParent, grandParent.parent)
            } else {
                val rightChild = parentNode.right
                if (rightChild != null) {
                    if (rightChild.col == RBNode.Colour.RED) {
                        rotateLeft(parentNode, grandParent)
                        parentNode.parent?.let { balanceInsert(it) }
                    }
                }
            }
        }
    }

    private fun balanceRemove1(parentNode: RBNode<T>?, removedChild: BalanceCase.ChangedChild) {
        if (removedChild == BalanceCase.ChangedChild.RIGHT) {
            val rightChild = parentNode?.right
            if (rightChild != null) {
                rightChild.col = RBNode.Colour.BLACK
            }
        } else if (removedChild == BalanceCase.ChangedChild.LEFT) {
            val leftChild = parentNode?.left
            if (leftChild != null) {
                leftChild.col = RBNode.Colour.BLACK
            }
        }
    }

    private fun balanceRemove2(parentNode: RBNode<T>, removedChild: BalanceCase.ChangedChild) {
        if (getColourOfRemovedNode(parentNode) == RBNode.Colour.BLACK) { // red => no need to balance
            if (parentNode.col == RBNode.Colour.RED) { // then other child is black
                if (removedChild == BalanceCase.ChangedChild.RIGHT) {
                    balanceRemove2InRightChildWithRedParent(parentNode)
                } else if (removedChild == BalanceCase.ChangedChild.LEFT) {
                    balanceRemove2InLeftChildWithRedParent(parentNode)
                }
            } else {
                if (removedChild == BalanceCase.ChangedChild.RIGHT) {
                    balanceRemove2InRightChildWithBlackParent(parentNode)
                } else if (removedChild == BalanceCase.ChangedChild.LEFT) {
                    balanceRemove2InLeftChildWithBlackParent(parentNode)
                }
            }
        }
    }

    private fun balanceRemove2InRightChildWithRedParent(parentNode: RBNode<T>) {
        val otherChild = parentNode.left
        if (otherChild != null) {
            val leftChildOfOtherChild = otherChild.left
            val rightChildOfOtherChild = otherChild.right
            if (leftChildOfOtherChild?.col == RBNode.Colour.RED) {
                otherChild.col = RBNode.Colour.RED
                parentNode.col = RBNode.Colour.BLACK
                leftChildOfOtherChild.col = RBNode.Colour.BLACK
                rotateRight(parentNode, parentNode.parent)
            } else if (rightChildOfOtherChild?.col == RBNode.Colour.RED) {
                parentNode.col = RBNode.Colour.BLACK
                rotateLeft(otherChild, parentNode)
                rotateRight(parentNode, parentNode.parent)
            } else {
                otherChild.col = RBNode.Colour.RED
                parentNode.col = RBNode.Colour.BLACK
            }
        }
    }

    private fun balanceRemove2InLeftChildWithRedParent(parentNode: RBNode<T>) {
        val otherChild = parentNode.right
        if (otherChild != null) {
            val leftChildOfOtherChild = otherChild.left
            val rightChildOfOtherChild = otherChild.right
            if (leftChildOfOtherChild?.col == RBNode.Colour.RED) {
                parentNode.col = RBNode.Colour.BLACK
                rotateRight(otherChild, parentNode)
                rotateLeft(parentNode, parentNode.parent)
            } else if (rightChildOfOtherChild?.col == RBNode.Colour.RED) {
                otherChild.col = RBNode.Colour.RED
                parentNode.col = RBNode.Colour.BLACK
                rightChildOfOtherChild.col = RBNode.Colour.BLACK
                rotateLeft(parentNode, parentNode.parent)
            } else {
                otherChild.col = RBNode.Colour.RED
                parentNode.col = RBNode.Colour.BLACK
            }
        }
    }

    private fun balanceRemove2InRightChildWithBlackParent(parentNode: RBNode<T>) {
        val otherChild = parentNode.left
        if (otherChild != null) {
            if (otherChild.col == RBNode.Colour.RED) {
                balanceRemove2InRightChildWithBlackParentRedOtherChild(parentNode, otherChild)
            } else {
                balanceRemove2InRightChildWithBlackParentBlackOtherChild(parentNode, otherChild)
            }
        }
    }

    private fun balanceRemove2InLeftChildWithBlackParent(parentNode: RBNode<T>) {
        val otherChild = parentNode.right
        if (otherChild != null) {
            if (otherChild.col == RBNode.Colour.RED) {
                balanceRemove2InLeftChildWithBlackParentRedOtherChild(parentNode, otherChild)
            } else {
                balanceRemove2InLeftChildWithBlackParentBlackOtherChild(parentNode, otherChild)
            }
        }
    }

    private fun balanceRemove2InRightChildWithBlackParentRedOtherChild(
        parentNode: RBNode<T>,
        otherChild: RBNode<T>
    ) {
        val rightChildOfOtherChild = otherChild.right
        if (rightChildOfOtherChild != null) {
            val leftChildOfRightChildOfOtherChild =
                rightChildOfOtherChild.left // https://skr.sh/sJD6DQ2ML5B
            val rightChildOfRightChildOfOtherChild = rightChildOfOtherChild.right

            if (leftChildOfRightChildOfOtherChild?.col == RBNode.Colour.RED) {
                leftChildOfRightChildOfOtherChild.col = RBNode.Colour.BLACK
                rotateLeft(otherChild, parentNode)
                rotateRight(parentNode, parentNode.parent)
            } else if (rightChildOfRightChildOfOtherChild?.col == RBNode.Colour.RED) {
                rightChildOfOtherChild.col = RBNode.Colour.RED
                rightChildOfRightChildOfOtherChild.col = RBNode.Colour.BLACK
                rotateLeft(rightChildOfOtherChild, otherChild)
                balanceRemove2InRightChildWithBlackParentRedOtherChild(parentNode, otherChild)
                // case: leftChildOfRightChildOfOtherChild?.col == RBNode.Colour.RED
            } else {
                otherChild.col = RBNode.Colour.BLACK
                rightChildOfOtherChild.col = RBNode.Colour.RED
                rotateRight(parentNode, parentNode.parent)
            }
        }
    }

    private fun balanceRemove2InLeftChildWithBlackParentRedOtherChild(
        parentNode: RBNode<T>,
        otherChild: RBNode<T>
    ) {
        val leftChildOfOtherChild = otherChild.left
        if (leftChildOfOtherChild != null) {
            val rightChildOfLeftChildOfOtherChild =
                leftChildOfOtherChild.right // https://skr.sh/sJD6DQ2ML5B (inverted)
            val leftChildOfLeftChildOfOtherChild = leftChildOfOtherChild.left

            if (rightChildOfLeftChildOfOtherChild?.col == RBNode.Colour.RED) {
                rightChildOfLeftChildOfOtherChild.col = RBNode.Colour.BLACK
                rotateRight(otherChild, parentNode)
                rotateLeft(parentNode, parentNode.parent)
            } else if (leftChildOfLeftChildOfOtherChild?.col == RBNode.Colour.RED) {
                leftChildOfOtherChild.col = RBNode.Colour.RED
                leftChildOfLeftChildOfOtherChild.col = RBNode.Colour.BLACK
                rotateRight(leftChildOfOtherChild, otherChild)
                balanceRemove2InLeftChildWithBlackParentRedOtherChild(parentNode, otherChild)
                // case: rightChildOfLeftChildOfOtherChild?.col == RBNode.Colour.RED
            } else {
                otherChild.col = RBNode.Colour.BLACK
                leftChildOfOtherChild.col = RBNode.Colour.RED
                rotateLeft(parentNode, parentNode.parent)
            }
        }
    }

    private fun balanceRemove2InRightChildWithBlackParentBlackOtherChild(
        parentNode: RBNode<T>,
        otherChild: RBNode<T>
    ): Int {
        val rightChildOfOtherChild = otherChild.right
        if (rightChildOfOtherChild != null) {
            if (rightChildOfOtherChild.col == RBNode.Colour.RED) {
                rightChildOfOtherChild.col = RBNode.Colour.BLACK
                rotateLeft(otherChild, parentNode)
                rotateRight(parentNode, parentNode.parent)
                return 0
            }
        }
        val leftChildOfOtherChild = otherChild.left
        if (leftChildOfOtherChild != null) {
            if (leftChildOfOtherChild.col == RBNode.Colour.RED) {
                leftChildOfOtherChild.col = RBNode.Colour.BLACK
                rotateRight(parentNode, parentNode.parent)
                return 0
            }
        }
        otherChild.col = RBNode.Colour.RED
        val grandParent = parentNode.parent
        if (grandParent != null) {
            if (parentNode.elem < grandParent.elem) {
                balanceRemove2(grandParent, BalanceCase.ChangedChild.LEFT)
            } else {
                balanceRemove2(grandParent, BalanceCase.ChangedChild.RIGHT)
            }
        }
        return 0
    }


    private fun balanceRemove2InLeftChildWithBlackParentBlackOtherChild(
        parentNode: RBNode<T>,
        otherChild: RBNode<T>
    ): Int {
        val leftChildOfOtherChild = otherChild.left
        if (leftChildOfOtherChild != null) {
            if (leftChildOfOtherChild.col == RBNode.Colour.RED) {
                leftChildOfOtherChild.col = RBNode.Colour.BLACK
                rotateRight(otherChild, parentNode)
                rotateLeft(parentNode, parentNode.parent)
                return 0
            }
        }
        val rightChildOfOtherChild = otherChild.right
        if (rightChildOfOtherChild != null) {
            if (rightChildOfOtherChild.col == RBNode.Colour.RED) {
                rightChildOfOtherChild.col = RBNode.Colour.BLACK
                rotateLeft(parentNode, parentNode.parent)
                return 0
            }
        }
        otherChild.col = RBNode.Colour.RED
        val grandParent = parentNode.parent
        if (grandParent != null) {
            if (parentNode.elem < grandParent.elem) {
                balanceRemove2(grandParent, BalanceCase.ChangedChild.LEFT)
            } else {
                balanceRemove2(grandParent, BalanceCase.ChangedChild.RIGHT)
            }
        }
        return 0
    }

    private fun getColourOfRemovedNode(parentNode: RBNode<T>): RBNode.Colour {
        return if (getBlackHeight(parentNode.left) != getBlackHeight(parentNode.right)) {
            RBNode.Colour.BLACK
        } else {
            RBNode.Colour.RED
        }
    }

    private fun getBlackHeight(curNode: RBNode<T>?, blackHeight: Int = 0): Int {
        var blackHeightVar = blackHeight
        if (curNode != null) {
            if (curNode.col == RBNode.Colour.BLACK) {
                blackHeightVar += 1
            }
            return getBlackHeight(curNode.left, blackHeightVar)
        }
        return blackHeightVar
    }

    override fun replaceNode(replacedNode: RBNode<T>, parentNode: RBNode<T>?, newNode: RBNode<T>?) {
        newNode?.parent = parentNode
        if (parentNode == null) {
            if (newNode != null) {
                newNode.col = RBNode.Colour.BLACK
            }
        }
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
