package org.tree.binaryTree.trees

import org.tree.binaryTree.RBNode
import org.tree.binaryTree.templates.TemplateBalanceBSTree

// algorithm source: https://www.youtube.com/watch?v=T70nn4EyTrs&ab_channel=%D0%9B%D0%B5%D0%BA%D1%82%D0%BE%D1%80%D0%B8%D0%B9%D0%A4%D0%9F%D0%9C%D0%98

class RBTree<T : Comparable<T>> : TemplateBalanceBSTree<T, RBNode<T>>() {
    private fun findParentForNewNode(curNode: RBNode<T>?, obj: T): RBNode<T>? {
        if (curNode != null) {
            if (obj > curNode.element) {
                if (curNode.right == null) {
                    return curNode
                }
                return findParentForNewNode(curNode.right, obj)
            } else if (obj < curNode.element) {
                if (curNode.left == null) {
                    return curNode
                }
                return findParentForNewNode(curNode.left, obj)
            }
        }
        return null
    }

    override fun insert(curNode: RBNode<T>?, element: T): RBNode<T>? {
        val parentForObj = findParentForNewNode(curNode, element)
        val newNode = RBNode(parentForObj, element)
        if (parentForObj == null) { // in case of root insert | node already exist (nothing will be changed)
            if (root == null) {
                newNode.color = RBNode.Color.BLACK
            } else {
                return null
            }
        }
        return insertNode(parentForObj, newNode)
    }

    override fun balance(
        curNode: RBNode<T>?,
        changedChild: BalanceCase.ChangedChild,
        operationType: BalanceCase.OperationType,
        recursive: BalanceCase.Recursive
    ) {
        if (recursive == BalanceCase.Recursive.END) {
            if (curNode != null) {
                when (operationType) {
                    BalanceCase.OperationType.INSERT -> {
                        balanceInsert(curNode)
                    } // curNode is parent Node of inserted Node
                    BalanceCase.OperationType.REMOVE_0 -> {} // does nothing
                    BalanceCase.OperationType.REMOVE_1 -> {
                        balanceRemove1(curNode, changedChild)
                    }

                    BalanceCase.OperationType.REMOVE_2 -> {
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
        if (parentNode.color == RBNode.Color.RED) {
            val grandParent = parentNode.parent
            if (grandParent != null) { // in case when grandparent is null, there is no need to balance a tree
                val unclePosition: BalancePosition
                val uncle = if (parentNode.element < grandParent.element) {
                    unclePosition = BalancePosition.RIGHT_UNCLE
                    grandParent.right
                } else {
                    unclePosition = BalancePosition.LEFT_UNCLE
                    grandParent.left
                }
                if (uncle != null) {
                    if (uncle.color == RBNode.Color.RED) {
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
        uncle.color = RBNode.Color.BLACK
        parentNode.color = RBNode.Color.BLACK
        if (grandGrandParent != null) {
            grandParent.color = RBNode.Color.RED
            // https://skr.sh/sJ9LBQU2IGg, when y is curNode
            if (grandGrandParent.color == RBNode.Color.RED) {
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
            if (leftChild?.color == RBNode.Color.RED) {
                rotateRight(parentNode, grandParent)
                parentNode.parent?.let { balanceInsert(it) }
            } else {
                val rightChild = parentNode.right
                if (rightChild != null) {
                    parentNode.color = RBNode.Color.BLACK
                    grandParent.color = RBNode.Color.RED
                    rotateLeft(grandParent, grandParent.parent)
                }
            }
        } else {
            val leftChild = parentNode.left
            if (leftChild?.color == RBNode.Color.RED) {
                parentNode.color = RBNode.Color.BLACK
                grandParent.color = RBNode.Color.RED
                rotateRight(grandParent, grandParent.parent)
            } else {
                val rightChild = parentNode.right
                if (rightChild != null) {
                    if (rightChild.color == RBNode.Color.RED) {
                        rotateLeft(parentNode, grandParent)
                        parentNode.parent?.let { balanceInsert(it) }
                    }
                }
            }
        }
    }

    /** Balanced remove with 1 non-null child */
    private fun balanceRemove1(parentNode: RBNode<T>?, removedChild: BalanceCase.ChangedChild) {
        when (removedChild) {
            BalanceCase.ChangedChild.RIGHT -> {
                parentNode?.right?.run { color = RBNode.Color.BLACK }
            }

            BalanceCase.ChangedChild.LEFT -> {
                parentNode?.left?.run { color = RBNode.Color.BLACK }
            }

            else -> {}
        }
    }

    /** Balanced remove with 2 null children */
    private fun balanceRemove2(parentNode: RBNode<T>, removedChild: BalanceCase.ChangedChild) {
        if (getColourOfRemovedNode(parentNode) == RBNode.Color.BLACK) { // red => no need to balance
            if (parentNode.color == RBNode.Color.RED) { // then other child is black
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
            if (leftChildOfOtherChild?.color == RBNode.Color.RED) {
                otherChild.color = RBNode.Color.RED
                parentNode.color = RBNode.Color.BLACK
                leftChildOfOtherChild.color = RBNode.Color.BLACK
                rotateRight(parentNode, parentNode.parent)
            } else if (rightChildOfOtherChild?.color == RBNode.Color.RED) {
                parentNode.color = RBNode.Color.BLACK
                rotateLeft(otherChild, parentNode)
                rotateRight(parentNode, parentNode.parent)
            } else {
                otherChild.color = RBNode.Color.RED
                parentNode.color = RBNode.Color.BLACK
            }
        }
    }

    private fun balanceRemove2InLeftChildWithRedParent(parentNode: RBNode<T>) {
        val otherChild = parentNode.right
        if (otherChild != null) {
            val leftChildOfOtherChild = otherChild.left
            val rightChildOfOtherChild = otherChild.right
            if (leftChildOfOtherChild?.color == RBNode.Color.RED) {
                parentNode.color = RBNode.Color.BLACK
                rotateRight(otherChild, parentNode)
                rotateLeft(parentNode, parentNode.parent)
            } else if (rightChildOfOtherChild?.color == RBNode.Color.RED) {
                otherChild.color = RBNode.Color.RED
                parentNode.color = RBNode.Color.BLACK
                rightChildOfOtherChild.color = RBNode.Color.BLACK
                rotateLeft(parentNode, parentNode.parent)
            } else {
                otherChild.color = RBNode.Color.RED
                parentNode.color = RBNode.Color.BLACK
            }
        }
    }

    private fun balanceRemove2InRightChildWithBlackParent(parentNode: RBNode<T>) {
        val otherChild = parentNode.left
        if (otherChild != null) {
            if (otherChild.color == RBNode.Color.RED) {
                balanceRemove2InRightChildWithBlackParentRedOtherChild(parentNode, otherChild)
            } else {
                balanceRemove2InRightChildWithBlackParentBlackOtherChild(parentNode, otherChild)
            }
        }
    }

    private fun balanceRemove2InLeftChildWithBlackParent(parentNode: RBNode<T>) {
        val otherChild = parentNode.right
        if (otherChild != null) {
            if (otherChild.color == RBNode.Color.RED) {
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

            if (leftChildOfRightChildOfOtherChild?.color == RBNode.Color.RED) {
                leftChildOfRightChildOfOtherChild.color = RBNode.Color.BLACK
                rotateLeft(otherChild, parentNode)
                rotateRight(parentNode, parentNode.parent)
            } else if (rightChildOfRightChildOfOtherChild?.color == RBNode.Color.RED) {
                rightChildOfOtherChild.color = RBNode.Color.RED
                rightChildOfRightChildOfOtherChild.color = RBNode.Color.BLACK
                rotateLeft(rightChildOfOtherChild, otherChild)
                balanceRemove2InRightChildWithBlackParentRedOtherChild(parentNode, otherChild)
                // case: leftChildOfRightChildOfOtherChild?.col == RBNode.Colour.RED
            } else {
                otherChild.color = RBNode.Color.BLACK
                rightChildOfOtherChild.color = RBNode.Color.RED
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

            if (rightChildOfLeftChildOfOtherChild?.color == RBNode.Color.RED) {
                rightChildOfLeftChildOfOtherChild.color = RBNode.Color.BLACK
                rotateRight(otherChild, parentNode)
                rotateLeft(parentNode, parentNode.parent)
            } else if (leftChildOfLeftChildOfOtherChild?.color == RBNode.Color.RED) {
                leftChildOfOtherChild.color = RBNode.Color.RED
                leftChildOfLeftChildOfOtherChild.color = RBNode.Color.BLACK
                rotateRight(leftChildOfOtherChild, otherChild)
                balanceRemove2InLeftChildWithBlackParentRedOtherChild(parentNode, otherChild)
                // case: rightChildOfLeftChildOfOtherChild?.col == RBNode.Colour.RED
            } else {
                otherChild.color = RBNode.Color.BLACK
                leftChildOfOtherChild.color = RBNode.Color.RED
                rotateLeft(parentNode, parentNode.parent)
            }
        }
    }

    private fun balanceRemove2InRightChildWithBlackParentBlackOtherChild(
        parentNode: RBNode<T>,
        otherChild: RBNode<T>
    ) {
        val rightChildOfOtherChild = otherChild.right
        if (rightChildOfOtherChild != null) {
            if (rightChildOfOtherChild.color == RBNode.Color.RED) {
                rightChildOfOtherChild.color = RBNode.Color.BLACK
                rotateLeft(otherChild, parentNode)
                rotateRight(parentNode, parentNode.parent)
                return
            }
        }
        val leftChildOfOtherChild = otherChild.left
        if (leftChildOfOtherChild != null) {
            if (leftChildOfOtherChild.color == RBNode.Color.RED) {
                leftChildOfOtherChild.color = RBNode.Color.BLACK
                rotateRight(parentNode, parentNode.parent)
                return
            }
        }
        otherChild.color = RBNode.Color.RED
        val grandParent = parentNode.parent
        if (grandParent != null) {
            if (parentNode.element < grandParent.element) {
                balanceRemove2(grandParent, BalanceCase.ChangedChild.LEFT)
            } else {
                balanceRemove2(grandParent, BalanceCase.ChangedChild.RIGHT)
            }
        }
    }


    private fun balanceRemove2InLeftChildWithBlackParentBlackOtherChild(
        parentNode: RBNode<T>,
        otherChild: RBNode<T>
    ) {
        val leftChildOfOtherChild = otherChild.left
        if (leftChildOfOtherChild != null) {
            if (leftChildOfOtherChild.color == RBNode.Color.RED) {
                leftChildOfOtherChild.color = RBNode.Color.BLACK
                rotateRight(otherChild, parentNode)
                rotateLeft(parentNode, parentNode.parent)
                return
            }
        }
        val rightChildOfOtherChild = otherChild.right
        if (rightChildOfOtherChild != null) {
            if (rightChildOfOtherChild.color == RBNode.Color.RED) {
                rightChildOfOtherChild.color = RBNode.Color.BLACK
                rotateLeft(parentNode, parentNode.parent)
                return
            }
        }
        otherChild.color = RBNode.Color.RED
        val grandParent = parentNode.parent
        if (grandParent != null) {
            if (parentNode.element < grandParent.element) {
                balanceRemove2(grandParent, BalanceCase.ChangedChild.LEFT)
            } else {
                balanceRemove2(grandParent, BalanceCase.ChangedChild.RIGHT)
            }
        }
    }

    private fun getColourOfRemovedNode(parentNode: RBNode<T>): RBNode.Color {
        return if (getBlackHeight(parentNode.left) != getBlackHeight(parentNode.right)) {
            RBNode.Color.BLACK
        } else {
            RBNode.Color.RED
        }
    }

    private fun getBlackHeight(curNode: RBNode<T>?, blackHeight: Int = 0): Int {
        if (curNode != null) {
            return getBlackHeight(
                curNode.left,
                if (curNode.color == RBNode.Color.BLACK) {
                    blackHeight + 1
                } else {
                    blackHeight
                }
            )
        }
        return blackHeight
    }

    override fun replaceNode(replacedNode: RBNode<T>, parentNode: RBNode<T>?, newNode: RBNode<T>?) {
        newNode?.parent = parentNode
        if (parentNode == null) {
            if (newNode != null) {
                newNode.color = RBNode.Color.BLACK
            }
        }
        super.replaceNode(replacedNode, parentNode, newNode)
    }

    override fun rotateRight(curNode: RBNode<T>, parentNode: RBNode<T>?) {
        curNode.parent = curNode.left
        curNode.left?.parent = parentNode
        curNode.left?.right?.parent = curNode
        if (curNode === root) {
            curNode.left?.color = RBNode.Color.BLACK
        }
        super.rotateRight(curNode, parentNode)
    }

    override fun rotateLeft(curNode: RBNode<T>, parentNode: RBNode<T>?) {
        curNode.parent = curNode.right
        curNode.right?.parent = parentNode
        curNode.right?.left?.parent = curNode
        if (curNode === root) {
            curNode.right?.color = RBNode.Color.BLACK
        }
        super.rotateLeft(curNode, parentNode)
    }
}
