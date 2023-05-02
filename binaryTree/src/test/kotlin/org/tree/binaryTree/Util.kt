package org.tree.binaryTree

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.tree.binaryTree.templates.TemplateBSTree
import org.tree.binaryTree.templates.TemplateNode
import org.tree.binaryTree.trees.AVLTree
import org.tree.binaryTree.trees.RBTree
import java.util.*
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.max


// Generate
fun <T, NODE_T : TemplateNode<T, NODE_T>> generateTree(nodes: List<NODE_T?>): NODE_T? {
    val fheight = log2((nodes.size + 1).toFloat())
    val height = fheight.toInt()
    if (fheight - height > 0.0) {
        throw IllegalArgumentException("Received an invalid array to create a tree. Size of array should be equal (2**N)-1")
    }

    val q: Queue<NODE_T?> = LinkedList<NODE_T?>()
    val root = nodes[0]
    q.add(root)
    var i = 1
    while (i < nodes.size) {
        val cur = q.poll()
        cur?.left = nodes[i]
        i++
        q.add(cur?.left)
        cur?.right = nodes[i]
        i++
        q.add(cur?.right)
    }
    return root
}

fun <T : Comparable<T>> generateNodeTree(objects: List<T?>): Node<T>? {
    val new = objects.map {
        if (it != null) {
            Node(it)
        } else {
            null
        }
    }
    return generateTree(new)
}

// Check
fun <T : Comparable<T>, NODE_T : TemplateNode<T, NODE_T>> checkTreeNode(curNode: NODE_T?) {
    if (curNode != null) {
        val l = curNode.left
        if (l != null) {
            assertThat(curNode.element, greaterThanOrEqualTo(l.element))
            checkTreeNode(l)
        }

        val r = curNode.right
        if (r != null) {
            assertThat(curNode.element, lessThanOrEqualTo(r.element))
            checkTreeNode(r)
        }
    }
}

fun <T : Comparable<T>, NODE_T : TemplateNode<T, NODE_T>> checkBinSearchTree(
    tree: TemplateBSTree<T, NODE_T>,
    contain: Array<T>
) {
    assertThat(tree.traversal(TemplateNode.Traversal.INORDER), containsInAnyOrder(*contain))
    checkTreeNode(tree.root)
}

fun <T : Comparable<T>> checkRBTreeNode(curNode: RBNode<T>?, parNode: RBNode<T>?): Int {
    var blackHeight = 0
    if (curNode != null) {
        // two red nodes in row
        if (parNode?.col == RBNode.Colour.RED) {
            assertThat(curNode.col, equalTo(RBNode.Colour.BLACK))
        }

        // right parent
        assertThat(curNode.parent, equalTo(parNode))

        var lBlackHeight = 0
        val l = curNode.left
        if (l != null) {
            assertThat(curNode.element, greaterThanOrEqualTo(l.element))
            lBlackHeight = checkRBTreeNode(l, curNode)
        }

        var rBlackHeight = 0
        val r = curNode.right
        if (r != null) {
            assertThat(curNode.element, lessThanOrEqualTo(r.element))
            rBlackHeight = checkRBTreeNode(r, curNode)
        }

        // same black height
        assertThat(lBlackHeight, equalTo(rBlackHeight))
        blackHeight = lBlackHeight

        if (curNode.col == RBNode.Colour.BLACK) {
            blackHeight += 1
        }
    }

    return blackHeight
}

fun <T : Comparable<T>> checkRBTree(
    tree: RBTree<T>,
    contain: Array<T>
) {
    assertThat(tree.traversal(TemplateNode.Traversal.INORDER), containsInAnyOrder(*contain))
    checkRBTreeNode(tree.root, null)
}

private fun <T : Comparable<T>> checkHeight(curNode: AVLNode<T>?): Int{
    if (curNode == null) {
        return 0
    }
    val leftHeight = checkHeight(curNode.left)
    val rightHeight = checkHeight(curNode.right)
    return 1 + max(leftHeight, rightHeight)
}

private fun <T : Comparable<T>> checkAVLTreeNode(node: AVLNode<T>?) {
    if (node == null) {
        return
    }

    val leftHeight = checkHeight(node.left)
    val rightHeight = checkHeight(node.right)

    assertThat(abs(leftHeight - rightHeight), lessThanOrEqualTo(1))

    checkAVLTreeNode(node.left)
    checkAVLTreeNode(node.right)

}

fun <T : Comparable<T>> checkAVLTree(
    tree: AVLTree<T>,
    contain: Array<T>
) {
    assertThat(tree.traversal(TemplateNode.Traversal.INORDER), containsInAnyOrder(*contain))
    checkAVLTreeNode(tree.root)
}
