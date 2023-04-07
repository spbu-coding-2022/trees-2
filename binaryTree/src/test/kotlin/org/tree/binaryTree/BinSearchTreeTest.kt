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

    @DisplayName("remove() tests")
    class RemoveTests {
        @ParameterizedTest(name = "[{index}]: tree = {0}, remove = {1}")
        @MethodSource("testRemoveArgs")
        fun testRemove(values: List<Int?>, remVal: Int) {
            val tree = BinSearchTree<Int>()
            tree.root = generateNodeTree(values)
            val valuesSet = values.filterNotNull().toMutableSet()
            val exp = valuesSet.remove(remVal)
            val act = tree.remove(remVal)

            assertThat(act, equalTo(exp))
            checkBinSearchTree(tree, valuesSet.toTypedArray())
        }

        companion object {
            @JvmStatic
            fun testRemoveArgs(): Stream<Arguments> {
                return Stream.of(
                    //[1] root remove
                    Arguments.of(
                        listOf(
                            40,
                        ), 40
                    ),
                    //[2] left remove
                    Arguments.of(
                        listOf(
                            40,
                            20, 60,
                        ), 20
                    ),
                    //[3] right remove
                    Arguments.of(
                        listOf(
                            40,
                            20, 60,
                        ), 40
                    ),
                    //[4] empty remove
                    Arguments.of(
                        listOf(
                            40,
                            20, 60,
                            null, null, null, null,
                        ), 42
                    ),
                )
            }
        }
    }

    @DisplayName("find() tests")
    class FindTests {
        @ParameterizedTest(name = "[{index}]: tree = {0}, find = {1}")
        @MethodSource("testFindArgs")
        fun testFind(values: List<Int?>, fndVal: Int) {
            val tree = BinSearchTree<Int>()
            tree.root = generateNodeTree(values)
            val valuesSet = values.filterNotNull().toMutableSet()
            val exp = valuesSet.contains(fndVal)
            val act = tree.find(fndVal)

            assertThat(act != null, equalTo(exp))
            if (act != null) {
                assertThat(act.elem, equalTo(fndVal))
            }
        }

        companion object {
            @JvmStatic
            fun testFindArgs(): Stream<Arguments> {
                return Stream.of(
                    //[1] root find
                    Arguments.of(
                        listOf(
                            40,
                            20, 60
                        ), 40
                    ),
                    //[2] left find
                    Arguments.of(
                        listOf(
                            40,
                            20, 60
                        ), 20
                    ),
                    //[3] right find
                    Arguments.of(
                        listOf(
                            40,
                            20, 60,
                        ), 60
                    ),
                    //[4] impossible find
                    Arguments.of(
                        listOf(
                            40,
                            20, 60
                        ), 42
                    ),
                )
            }
        }
    }
}
