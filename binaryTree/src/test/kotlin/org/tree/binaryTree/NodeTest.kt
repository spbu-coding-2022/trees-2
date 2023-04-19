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
            val act = root?.traversal(TemplateNode.Traversal.PREORDER)
            val exp = listOf(40, 20, 15, 60, 67)
            MatcherAssert.assertThat(act, Matchers.equalTo(exp))
        }

        @Test
        fun inorderTraversalTest() {
            val act = root?.traversal(TemplateNode.Traversal.INORDER)
            val exp = listOf(15, 20, 40, 60, 67)
            MatcherAssert.assertThat(act, Matchers.equalTo(exp))
        }

        @Test
        fun postorderTraversalTest() {
            val act = root?.traversal(TemplateNode.Traversal.POSTORDER)
            val exp = listOf(15, 20, 67, 60, 40)
            MatcherAssert.assertThat(act, Matchers.equalTo(exp))
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
            val exp = root?.left
            MatcherAssert.assertThat(act, Matchers.equalTo(exp))
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
            val exp = root?.right
            MatcherAssert.assertThat(act, Matchers.equalTo(exp))
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
            val exp = null
            MatcherAssert.assertThat(act, Matchers.equalTo(exp))
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
            MatcherAssert.assertThat(act, Matchers.anyOf(Matchers.equalTo(root?.left), Matchers.equalTo(root?.right)))
        }

    }

    @DisplayName("Count null children tests")
    @Nested
    inner class CountNullChildrenTests {

        @Test
        fun leftChildrenTest() {
            root = generateNodeTree(
                listOf(
                    40,
                    20, null
                )
            )
            val act = root?.countNullChildren()
            val exp = 1
            MatcherAssert.assertThat(act, Matchers.equalTo(exp))
        }

        @Test
        fun rightChildrenTest() {
            root = generateNodeTree(
                listOf(
                    40,
                    null, 60
                )
            )
            val act = root?.countNullChildren()
            val exp = 1
            MatcherAssert.assertThat(act, Matchers.equalTo(exp))
        }

        @Test
        fun noChildrenTest() {
            root = generateNodeTree(
                listOf(
                    40,
                    null, null
                )
            )
            val act = root?.countNullChildren()
            val exp = 2
            MatcherAssert.assertThat(act, Matchers.equalTo(exp))
        }

        @Test
        fun twoChildrenTest() {
            root = generateNodeTree(
                listOf(
                    40,
                    20, 60
                )
            )
            val act = root?.countNullChildren()
            val exp = 0
            MatcherAssert.assertThat(act, Matchers.equalTo(exp))
        }

    }
}
