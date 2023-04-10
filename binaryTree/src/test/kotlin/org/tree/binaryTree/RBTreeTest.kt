package org.tree.binaryTree

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.random.Random

class RBTreeTest {
    @DisplayName("insert() tests")
    class InsertTests {
        @ParameterizedTest(name = "[{index}]: insertCount = {1}, seed = {0}")
        @MethodSource("testInsertArgs")
        fun testInsert(seed: Long, insertCount: Int) {
            val tree = RBTree<Int>()
            val values = mutableSetOf<Int>()
            val randomizer = Random(seed)

            for (i in 1..insertCount) {
                val newVal = randomizer.nextInt()
                val exp = values.add(newVal)
                val act = tree.insert(newVal)
                //println("Insert $newVal")

                MatcherAssert.assertThat(act, Matchers.equalTo(exp))
                checkRBTree(tree, values.toTypedArray())
            }


        }

        companion object {
            @JvmStatic
            fun testInsertArgs(): Stream<Arguments> {
                return Stream.of(
                    genArguments(0xdeadbeef),
                    genArguments(0xabacaba, 10),
                    genArguments(42),
                    genArguments(13),
                    genArguments(0xcafe),
                    genArguments(1337),
                )
            }

            private fun genArguments(seed: Long, insertCount: Int = 1000): Arguments {
                return Arguments.of(seed, insertCount)
            }
        }

    }


    @DisplayName("remove() tests")
    class RemoveTests {
        @ParameterizedTest(name = "[{index}]: treeSize = {1}, removeCount = {2}, seed = {0}")
        @MethodSource("testRemoveArgs")
        fun testRemove(seed: Long, treeSize: Int, removeCount: Int) {
            val tree = RBTree<Int>()
            val values = mutableSetOf<Int>()
            val randomizer = Random(seed)
            for (i in 1..treeSize) {
                val newVal = randomizer.nextInt()
                values.add(newVal)
                tree.insert(newVal)
            }

            for (i in 1..removeCount * 2) {
                val curVal = if ((i % 2 != 0) and (values.isNotEmpty())) {
                    values.random(randomizer)
                } else {
                    randomizer.nextInt()
                }
                val exp = values.remove(curVal)
                val act = tree.remove(curVal)


                MatcherAssert.assertThat(act, Matchers.equalTo(exp))
                checkRBTree(tree, values.toTypedArray())
            }
        }

        companion object {
            @JvmStatic
            fun testRemoveArgs(): Stream<Arguments> {
                return Stream.of(
                    genArguments(0xdeadbeef),
                    genArguments(0xdeadbeef, 1000, 1050),
                    genArguments(0xdeadbeef, 1050, 1000),
                    genArguments(0xdeadbeef, 10, 5),
                    genArguments(0xdeadbeef, 0, 1),
                    genArguments(42),
                    genArguments(13),
                    genArguments(0xcafe),
                    genArguments(1337),
                )
            }

            private fun genArguments(seed: Long, treeSize: Int = 1000, removeCount: Int = 1000): Arguments {
                return Arguments.of(seed, treeSize, removeCount)
            }
        }

    }
}
