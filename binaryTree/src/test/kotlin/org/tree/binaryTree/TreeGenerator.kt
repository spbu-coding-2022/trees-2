package org.tree.binaryTree

import java.util.*
import kotlin.math.log2

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
