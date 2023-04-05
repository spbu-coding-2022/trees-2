package org.tree.binaryTree

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.util.*

fun <T : Comparable<T>> generateBinSearchTree(objects: List<T?>): Node<T>? {
    val new = objects.map {
        if (it != null) {
            Node(it)
        } else {
            null
        }
    }
    return generateTree(new)
}

class BinSearchTreeTest {
    @Test
    fun rootInsertTest() {
        val testTree = BinSearchTree<Int>()
        testTree.insert(3)
        assertEquals(testTree.root?.elem, 3)
    }
}
