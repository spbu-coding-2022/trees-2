package org.tree.binaryTree

import org.junit.jupiter.api.*

class NodeTest {
    var root: Node<Int>? = null

    @DisplayName("Traversal tests")
    @Nested
    inner class TraversalTests {
        @BeforeEach
        fun init() {
            root = generateNodeTree(
                listOf(
                    40,
                    20, 60,
                    15, null, null, 67,
                )
            )
        }

        @Test
        fun preorderTraversalTest() {
            val act = root?.traversal(TemplateNode.Traversal.PREORDER)
            val exp = listOf(40, 20, 15, 60, 67)
            Assertions.assertEquals(exp, act)
        }

        @Test
        fun inorderTraversalTest() {
            val act = root?.traversal(TemplateNode.Traversal.INORDER)
            val exp = listOf(15, 20, 40, 60, 67)
            Assertions.assertEquals(exp, act)
        }

        @Test
        fun postorderTraversalTest() {
            val act = root?.traversal(TemplateNode.Traversal.POSTORDER)
            val exp = listOf(15, 20, 67, 60, 40)
            Assertions.assertEquals(exp, act)
        }

    }

    @DisplayName("Get non null child tests")
    @Nested
    inner class GetNonNullChildTests {

        @Test
        fun leftChildrenTest() {
            root = generateNodeTree(
                listOf(
                    40,
                    20, null
                )
            )
            val act = root?.getNonNullChild()
            Assertions.assertEquals(root?.left, act)
        }

        @Test
        fun rightChildrenTest() {
            root = generateNodeTree(
                listOf(
                    40,
                    null, 60
                )
            )
            val act = root?.getNonNullChild()
            Assertions.assertEquals(root?.right, act)
        }

        @Test
        fun noChildrenTest() {
            root = generateNodeTree(
                listOf(
                    40,
                    null, null
                )
            )
            val act = root?.getNonNullChild()
            Assertions.assertEquals(null, act)
        }

        @Test
        fun twoChildrenTest() {
            root = generateNodeTree(
                listOf(
                    40,
                    20, 60
                )
            )
            val act = root?.getNonNullChild()
            Assertions.assertNotEquals(null, act)
        }

    }
}
