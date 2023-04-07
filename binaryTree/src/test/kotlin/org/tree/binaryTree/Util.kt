package org.tree.binaryTree

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import java.util.*
import kotlin.math.log2


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
            assertThat(curNode.elem, greaterThanOrEqualTo(l.elem))
            checkTreeNode(l)
        }

        val r = curNode.right
        if (r != null) {
            assertThat(curNode.elem, lessThanOrEqualTo(r.elem))
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
