package org.tree.app


import org.tree.binaryTree.Node
import org.tree.binaryTree.RBTree

fun insert(obj: Int, RB: RBTree<Int>) {
    println("insert ${obj}")
    RB.insert(obj)
    val root = RB.root
    if (root != null) {
        println(root.col)
        println(root.elem)
        println(root.parent?.elem)
        println("${root.left?.col}, ${root.right?.col}")
        println("${root.left?.elem}, ${root.right?.elem}")
        println("${root.left?.parent?.elem}, ${root.right?.parent?.elem}")
        println("${root.left?.left?.col}, ${root.left?.right?.col}, ${root.right?.left?.col}, ${root.right?.right?.col}")
        println("${root.left?.left?.elem}, ${root.left?.right?.elem}, ${root.right?.left?.elem}, ${root.right?.right?.elem}")
        println("${root.left?.left?.parent?.elem}, ${root.left?.right?.parent?.elem}, ${root.right?.left?.parent?.elem}, ${root.right?.right?.parent?.elem}")
        println("${root.left?.left?.left?.col}, ${root.left?.left?.right?.col}, ${root.left?.right?.left?.col}, ${root.left?.right?.right?.col}, ${root.right?.left?.left?.col}, ${root.right?.left?.right?.col}, ${root.right?.right?.left?.col}, ${root.right?.right?.right?.col}")
        println("${root.left?.left?.left?.elem}, ${root.left?.left?.right?.elem}, ${root.left?.right?.left?.elem}, ${root.left?.right?.right?.elem}, ${root.right?.left?.left?.elem}, ${root.right?.left?.right?.elem}, ${root.right?.right?.left?.elem}, ${root.right?.right?.right?.elem}")
        println("${root.left?.left?.left?.parent?.elem}, ${root.left?.left?.right?.parent?.elem}, ${root.left?.right?.left?.parent?.elem}, ${root.left?.right?.right?.parent?.elem}, ${root.right?.left?.left?.parent?.elem}, ${root.right?.left?.right?.parent?.elem}, ${root.right?.right?.left?.parent?.elem}, ${root.right?.right?.right?.parent?.elem}")
    }
    println()
}

fun remove(obj: Int, RB: RBTree<Int>) {
    println("remove ${obj}")
    RB.remove(obj)
    val root = RB.root
    if (root != null) {
        println(root.col)
        println(root.elem)
        println(root.parent?.elem)
        println("${root.left?.col}, ${root.right?.col}")
        println("${root.left?.elem}, ${root.right?.elem}")
        println("${root.left?.parent?.elem}, ${root.right?.parent?.elem}")
        println("${root.left?.left?.col}, ${root.left?.right?.col}, ${root.right?.left?.col}, ${root.right?.right?.col}")
        println("${root.left?.left?.elem}, ${root.left?.right?.elem}, ${root.right?.left?.elem}, ${root.right?.right?.elem}")
        println("${root.left?.left?.parent?.elem}, ${root.left?.right?.parent?.elem}, ${root.right?.left?.parent?.elem}, ${root.right?.right?.parent?.elem}")
        println("${root.left?.left?.left?.col}, ${root.left?.left?.right?.col}, ${root.left?.right?.left?.col}, ${root.left?.right?.right?.col}, ${root.right?.left?.left?.col}, ${root.right?.left?.right?.col}, ${root.right?.right?.left?.col}, ${root.right?.right?.right?.col}")
        println("${root.left?.left?.left?.elem}, ${root.left?.left?.right?.elem}, ${root.left?.right?.left?.elem}, ${root.left?.right?.right?.elem}, ${root.right?.left?.left?.elem}, ${root.right?.left?.right?.elem}, ${root.right?.right?.left?.elem}, ${root.right?.right?.right?.elem}")
        println("${root.left?.left?.left?.parent?.elem}, ${root.left?.left?.right?.parent?.elem}, ${root.left?.right?.left?.parent?.elem}, ${root.left?.right?.right?.parent?.elem}, ${root.right?.left?.left?.parent?.elem}, ${root.right?.left?.right?.parent?.elem}, ${root.right?.right?.left?.parent?.elem}, ${root.right?.right?.right?.parent?.elem}")
    }
    println()
}

fun main() {
    val RB = RBTree<Int>()
    insert(17, RB)
    insert(10, RB)
    insert(20, RB)
    insert(15, RB)
    remove(20, RB)
    insert(9, RB)
    remove(17, RB)
    insert(16, RB)
    remove(9, RB)
    insert(30, RB)
    insert(29, RB)
    remove(10, RB)
    remove(30, RB)
}
