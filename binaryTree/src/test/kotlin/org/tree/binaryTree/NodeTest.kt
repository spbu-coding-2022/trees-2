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
}
