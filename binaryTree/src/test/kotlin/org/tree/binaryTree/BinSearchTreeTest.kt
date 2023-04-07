package org.tree.binaryTree


import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class BinSearchTreeTest {
    @DisplayName("insert() tests")
    class InsertTests {
        @ParameterizedTest(name = "[{index}]: tree = {0}, insert = {1}")
        @MethodSource("testInsertArgs")
        fun testInsert(values: List<Int?>, insVal: Int) {
            val tree = BinSearchTree<Int>()
            tree.root = generateNodeTree(values)
            val valuesSet = values.filterNotNull().toMutableSet()
            val exp = valuesSet.add(insVal)
            val act = tree.insert(insVal)

            assertThat(act, equalTo(exp))
            checkBinSearchTree(tree, valuesSet.toTypedArray())
        }

        companion object {
            @JvmStatic
            fun testInsertArgs(): Stream<Arguments> {
                return Stream.of(
                    //[1] root insert
                    Arguments.of(listOf(null), 40),
                    //[2] left insert
                    Arguments.of(
                        listOf(
                            40,
                        ),
                        20
                    ),
                    //[3] right insert
                    Arguments.of(
                        listOf(
                            40,
                        ),
                        60
                    ),
                    //[4] left rec insert
                    Arguments.of(
                        listOf(
                            40,
                            20, 60
                        ),
                        21
                    ),
                    //[5] right rec insert
                    Arguments.of(
                        listOf(
                            40,
                            20, 60
                        ),
                        55
                    ),
                    //[6] same insert
                    Arguments.of(
                        listOf(
                            40,
                            null, 60,
                            null, null, 45, null,
                        ),
                        45
                    ),
                )
            }
        }
    }
}
