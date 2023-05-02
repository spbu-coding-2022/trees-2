package org.tree.binaryTree

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.tree.binaryTree.templates.TemplateNode

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
            val act = root?.traverse(TemplateNode.Traversal.PREORDER)
            val exp = listOf(40, 20, 15, 60, 67)
            MatcherAssert.assertThat(act, Matchers.equalTo(exp))
        }

        @Test
        fun inorderTraversalTest() {
            val act = root?.traverse(TemplateNode.Traversal.INORDER)
            val exp = listOf(15, 20, 40, 60, 67)
            MatcherAssert.assertThat(act, Matchers.equalTo(exp))
        }

        @Test
        fun postorderTraversalTest() {
            val act = root?.traverse(TemplateNode.Traversal.POSTORDER)
            val exp = listOf(15, 20, 67, 60, 40)
            MatcherAssert.assertThat(act, Matchers.equalTo(exp))
        }

    }
}
