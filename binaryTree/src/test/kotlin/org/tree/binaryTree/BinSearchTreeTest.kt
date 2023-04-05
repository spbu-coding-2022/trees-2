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
    val testTree = BinSearchTree<Int>()

    @DisplayName("Insert tests")
    @Nested
    inner class InsertTests {
        @Test
        fun rootInsertTest() {
            val ok = testTree.insert(3)

            assertEquals(true, ok)
            assertEquals(3, testTree.root?.elem)
        }

        @Test
        fun leftInsertTest() {
            testTree.root = generateBinSearchTree(
                listOf(
                    40,
                )
            )
            val ok = testTree.insert(20)

            assertEquals(true, ok)
            assertEquals(20, testTree.root?.left?.elem)
        }

        @Test
        fun rightInsertTest() {
            testTree.root = generateBinSearchTree(
                listOf(
                    40,
                )
            )
            val ok = testTree.insert(60)

            assertEquals(true, ok)
            assertEquals(60, testTree.root?.right?.elem)
        }

        @Test
        fun leftRecInsertTest() {
            testTree.root = generateBinSearchTree(
                listOf(
                    40,
                    20, 60,
                )
            )
            val ok = testTree.insert(21)

            assertEquals(true, ok)
            assertEquals(21, testTree.root?.left?.right?.elem)
        }

        @Test
        fun rightRecInsertTest() {
            testTree.root = generateBinSearchTree(
                listOf(
                    40,
                    20, 60,
                )
            )
            val ok = testTree.insert(45)

            assertEquals(true, ok)
            assertEquals(45, testTree.root?.right?.left?.elem)
        }

        @Test
        fun sameInsertTest() {
            testTree.root = generateBinSearchTree(
                listOf(
                    40,
                    null, 60,
                    null, null, 45, null,
                )
            )
            val ok = testTree.insert(45)

            assertEquals(false, ok)
        }
    }
}
