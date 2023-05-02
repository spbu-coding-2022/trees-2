package org.tree.binaryTree.templates

/**
 * This class is template class for creating your own balance binary search trees.
 * @param T the type of element stored in the tree's nodes
 * @param NODE_T the type of nodes in the tree
 *
 * @property balance abstract method that should balance your tree
 */
abstract class TemplateBalanceBSTree<T : Comparable<T>, NODE_T : TemplateNode<T, NODE_T>> :
    TemplateBSTree<T, NODE_T>() {

    //Balance
    /**
     * @property ChangedChild what child was changed
     * @property Recursive was call recursive or last
     * @property OperationType from what method was called
     */
    protected class BalanceCase {
        /**
         * @property LEFT left child was changed
         * @property RIGHT right child was changed
         * @property ROOT root was changed
         */
        enum class ChangedChild { LEFT, RIGHT, ROOT }

        /**
         * @property RECURSIVE_CALL the function was called recursively for traverse
         * @property END the last, significant call
         */
        enum class Recursive { RECURSIVE_CALL, END }

        /**
         * @property INSERT called from the insert method
         * @property REMOVE_0 called from the remove method when current node had 0 null children
         * @property REMOVE_1 called from the remove method when current node had 1 null children
         * @property REMOVE_2 called from the remove method when current node had 2 null children
         */
        enum class OperationType { INSERT, REMOVE_0, REMOVE_1, REMOVE_2 }
    }

    /**
     * @return remove type from [BalanceCase.OperationType] based on the [nullChildrenCount]
     *
     * @throws IllegalArgumentException if [nullChildrenCount] < 0 or > 2
     */
    protected fun getBalanceRemoveType(nullChildrenCount: Int): BalanceCase.OperationType {
        return when (nullChildrenCount) {
            2 -> BalanceCase.OperationType.REMOVE_2
            1 -> BalanceCase.OperationType.REMOVE_1
            0 -> BalanceCase.OperationType.REMOVE_0
            else -> throw IllegalArgumentException("Expected number was <= 2, because in a binary tree a node can have no more than two children")
        }
    }

    /**
     * @return what child of [curNode] should have [element]
     */
    protected fun getDirectionChangedChild(curNode: NODE_T?, element: T): BalanceCase.ChangedChild {
        return if (curNode == null) {
            BalanceCase.ChangedChild.ROOT
        } else if (element < curNode.element) {
            BalanceCase.ChangedChild.LEFT
        } else {
            BalanceCase.ChangedChild.RIGHT
        }
    }

    /**
     * This method automatic called after inserting or removing element from tree
     *
     * @param curNode - this is parent of changed node, if curNode is null => changed node is root
     * @property changedChild what child was changed
     * @property operationType from what method was called
     * @property recursive was call recursive or last
     */
    protected abstract fun balance(
        curNode: NODE_T?,
        changedChild: BalanceCase.ChangedChild,
        operationType: BalanceCase.OperationType,
        recursive: BalanceCase.Recursive
    )

    /**
     * Insert [newNode] into the subtree of the [curNode]. And after call [balance] with right arguments.
     *
     * @return the parent of the inserted node,
     * null if node with the same element already in tree or if inserted node is root
     */
    override fun insertNode(curNode: NODE_T?, newNode: NODE_T): NODE_T? {
        val parNode = super.insertNode(curNode, newNode)
        if (curNode != null) {
            if (curNode === parNode) {
                balance(
                    curNode,
                    getDirectionChangedChild(curNode, newNode.element),
                    BalanceCase.OperationType.INSERT,
                    BalanceCase.Recursive.END
                )
            } else {
                balance(
                    curNode,
                    getDirectionChangedChild(curNode, newNode.element),
                    BalanceCase.OperationType.INSERT,
                    BalanceCase.Recursive.RECURSIVE_CALL
                )
                if (curNode === root) {
                    balance(
                        null,
                        BalanceCase.ChangedChild.ROOT,
                        BalanceCase.OperationType.INSERT,
                        BalanceCase.Recursive.RECURSIVE_CALL
                    )
                }
            }
        }
        return parNode
    }

    /**
     * Remove [element] from subtree of the [curNode] with [parentNode] as parent.
     * And after call [balance] with right arguments
     *
     * @return the count of null children of deleted node or null if the node was not found
     */
    override fun remove(curNode: NODE_T?, parentNode: NODE_T?, element: T): Int? {
        if (curNode == null) {
            return null
        }

        val targetNode: NODE_T?
        val isRec: BalanceCase.Recursive
        val res =
            if (element < curNode.element) {
                isRec = BalanceCase.Recursive.RECURSIVE_CALL
                targetNode = parentNode
                remove(curNode.left, curNode, element)
            } else if (element > curNode.element) {
                isRec = BalanceCase.Recursive.RECURSIVE_CALL
                targetNode = parentNode
                remove(curNode.right, curNode, element)
            } else {
                isRec = BalanceCase.Recursive.END
                targetNode = parentNode
                deleteNode(curNode, parentNode)
            }

        if (res != null) {
            balance(targetNode, getDirectionChangedChild(targetNode, element), getBalanceRemoveType(res), isRec)
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
