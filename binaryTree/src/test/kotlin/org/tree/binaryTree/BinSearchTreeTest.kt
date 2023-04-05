package org.tree.binaryTree

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.util.*


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
            testTree.root = generateNodeTree(
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
            testTree.root = generateNodeTree(
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
            testTree.root = generateNodeTree(
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
            testTree.root = generateNodeTree(
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
            testTree.root = generateNodeTree(
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
            testTree.root = generateNodeTree(
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
            testTree.root = generateNodeTree(
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
            testTree.root = generateNodeTree(
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
            testTree.root = generateNodeTree(
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
            testTree.root = generateNodeTree(
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

    @DisplayName("DeleteNode tests")
    @Nested
    inner class DeleteNodeTests {
        @Test
        fun zeroChildrenDeleteTest() {
            testTree.root = generateNodeTree(
                listOf(
                    40,
                    20, 60,
                    null, null, null, null,
                )
            )
            val ok = testTree.remove(60)

            assertEquals(true, ok)
            assertNotEquals(null, testTree.root)
            assertEquals(null, testTree.root?.right)
        }

        @Test
        fun oneChildrenDeleteTest() {
            testTree.root = generateNodeTree(
                listOf(
                    40,
                    20, 60,
                    null, null, 55, null,
                )
            )
            val ok = testTree.remove(60)

            assertEquals(true, ok)
            assertEquals(55, testTree.root?.right?.elem)
            assertEquals(null, testTree.root?.right?.left)
        }

        @Test
        fun twoChildrenDeleteTest() {
            testTree.root = generateNodeTree(
                listOf(
                    40,
                    20, 60,
                    null, null, 55, 70,
                )
            )
            val ok = testTree.remove(60)

            assertEquals(true, ok)
            assertEquals(70, testTree.root?.right?.elem)
            assertEquals(null, testTree.root?.right?.right)
            assertEquals(55, testTree.root?.right?.left?.elem)
        }

        @Test
        fun twoChildrenBigDeleteTest() {
            testTree.root = generateNodeTree(
                listOf(
                    40,
                    20, 60,
                    null, null, 55, 70,
                    null, null, null, null, 53, 57, 67, 71,
                )
            )
            val ok = testTree.remove(60)

            assertEquals(true, ok)
            assertEquals(67, testTree.root?.right?.elem)
            assertEquals(55, testTree.root?.right?.left?.elem)
            assertEquals(70, testTree.root?.right?.right?.elem)
            assertEquals(null, testTree.root?.right?.right?.left)
        }
    }

    @DisplayName("Traversal tests")
    @Nested
    inner class TraversalTests {
        @Test
        fun preorderTraversalTest() {
            testTree.root = generateNodeTree(
                listOf(
                    40,
                    20, 60,
                    15, null, null, 67,
                )
            )
            val act = testTree.traversal(TemplateNode.Traversal.PREORDER)
            val exp = listOf(40, 20, 15, 60, 67)
            assertEquals(exp, act)
        }

        @Test
        fun nullTraversalTest() {
            testTree.root = null
            val act = testTree.traversal(TemplateNode.Traversal.PREORDER)
            assertEquals(null, act)
        }
    }
}
