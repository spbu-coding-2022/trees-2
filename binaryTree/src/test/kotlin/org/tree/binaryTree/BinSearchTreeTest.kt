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

    @DisplayName("Find tests")
    @Nested
    inner class FindTests {
        @BeforeEach
        fun init() {
            testTree.root = generateBinSearchTree(
                listOf(
                    40,
                    20, 60
                )
            )
        }

        @Test
        fun rootFindTest() {
            val act = testTree.find(40)

            assertEquals(testTree.root, act)
        }

        @Test
        fun leftFindTest() {
            val act = testTree.find(20)

            assertEquals(testTree.root?.left, act)
        }

        @Test
        fun rightFindTest() {
            val act = testTree.find(60)

            assertEquals(testTree.root?.right, act)
        }

        @Test
        fun emptyFindTest() {
            val act = testTree.find(50)

            assertEquals(null, act)
        }
    }

    @DisplayName("Remove tests")
    @Nested
    inner class RemoveTests {


        @Test
        fun rootRemoveTest() {
            testTree.root = generateBinSearchTree(
                listOf(
                    40,
                )
            )
            val ok = testTree.remove(40)

            assertEquals(true, ok)
            assertEquals(null, testTree.root)
        }

        @Test
        fun leftRemoveTest() {
            testTree.root = generateBinSearchTree(
                listOf(
                    40,
                    20, 60,
                )
            )
            val ok = testTree.remove(20)

            assertEquals(true, ok)
            assertNotEquals(null, testTree.root)
            assertEquals(null, testTree.root?.left)
        }

        @Test
        fun rightRemoveTest() {
            testTree.root = generateBinSearchTree(
                listOf(
                    40,
                    20, 60,
                )
            )
            val ok = testTree.remove(60)

            assertEquals(true, ok)
            assertNotEquals(null, testTree.root)
            assertEquals(null, testTree.root?.right)
        }

        @Test
        fun emptyRemoveTest() {
            testTree.root = generateBinSearchTree(
                listOf(
                    40,
                    20, 60,
                )
            )
            val ok = testTree.remove(33)

            assertEquals(false, ok)
            assertNotEquals(null, testTree.root?.left)
            assertNotEquals(null, testTree.root?.right)
        }
    }
}
