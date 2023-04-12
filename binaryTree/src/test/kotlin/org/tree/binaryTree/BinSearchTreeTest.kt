package org.tree.binaryTree

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.tree.binaryTree.trees.BinSearchTree

class BinSearchTreeTest {
    @Test
    fun rootInsertTest() {
        val testTree = BinSearchTree<Int>()
        testTree.insert(3)
        assertEquals(testTree.root?.elem, 3)
    }
}
